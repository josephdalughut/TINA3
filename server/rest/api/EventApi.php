<?php

require_once 'Api.php';
require_once 'AbstractApi.php';
require_once '../model/User.php';
require_once '../model/Token.php';
require_once '../util/HTTPStatusCode.php';
require_once '../crypt/Crypt.php';
require_once '../util/Time.php';

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 5:23 PM
 */
class EventApi extends AbstractApi
{

    /**
     * @param $args
     * @return string
     */
    public function predict($args)
    {
        if (!$this->method == "POST") {
            return $this->_response("only POST requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        if (!self::checkParams($args, "yesterday", "date", "smartPlugId")) {
            return $this->_response("required parameter not found", HTTPStatusCode::$BAD_REQUEST);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        $smartPlugId = $args["smartPlugId"];
        $date = $args["date"];
        if(is_string($user)){
            return $user;
        }

        /** @var array $events */
        $objects = json_decode($args["yesterday"], true);
        $events = Array();
        foreach ($objects as $object){
            /** Event */
            $event = new Event();
            $event->fromArray(json_decode($object, true));
            array_push($events, $event);
        }
        $training = $this->train($user->getId(), $events);
        if(is_string($training))
            return $training;
        $prediction = $this->predictStatus($user->getId(), $date, $smartPlugId);
        return $prediction;
    }

    /**
     * @param array $args
     * @return string
     */
    public function toJson($args){
        $objects = json_decode($args["json"], true);
        $events = Array();
        foreach ($objects as $object){
            /** Event */
            $event = new Event();
            $event = $event->fromArray(json_decode($object, true));
            array_push($events, $event);
        }
        return $this->_response($events[0], HTTPStatusCode::$OK);
    }

    /**
     * @param $userId
     * @param $events
     * @return bool|string
     */
    private function train($userId, $events){
        if($events == null || empty($events))
            return true;
        /** @var Event $firstEvent */
        $firstEvent = $events[0];
        $date = $firstEvent->getDate();
        $findConflictSql = "select * from ".Event::$database_tableName
            ." where ".Event::$database_tableColumn_userId."=".$userId." and "
            .Event::$database_tableColumn_date."='".$date."' and "
            .Event::$database_tableColumn_smartPlugId."='".$firstEvent->getSmartPlugId()."' limit 1;";
        $res = $this->_getDatabase()->query($findConflictSql);
        if(!$res)
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        if($res->num_rows>0)
            return true;
        /** @var Event $event */
        foreach ($events as $event){
            $event->setDate($date)->setCreatedAt(Time::now())->setUpdatedAt($event->getCreatedAt());
            $insertSQL = Event::wrapToReplaceSQL($event);
            $res = $this->_getDatabase()->query($insertSQL);
            if(!$res)
                return $this->_response("Internal error, error adding events to db ".$this->_getDatabase()->error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        return true;
    }

    /**
     * @param $userId
     * @param $date
     * @param $smartPlugId
     * @return string
     */
    private function predictStatus($userId, $date, $smartPlugId){
        $arr = explode("_", $date);
        $now = new DateTime();
        $now->setDate(intval($arr[0]), intval($arr[1]), intval($arr[2]));
        $daysToConsider = 1;

        $firstDay = new DateTime("-".($daysToConsider + 1)." days");
        $lastDay = new DateTime("-1 day");
        $firstDayDate = $firstDay->format("Y_m_d");
        $lastDayDate = $lastDay->format("Y_m_d");
        if(strlen($firstDayDate)> 0)
            return $this->_response($firstDayDate.", ".$lastDay, HTTPStatusCode::$BAD_REQUEST);
        $selectSQL = "select * from ".Event::$database_tableName." where ".Event::$database_tableColumn_userId."=".$userId." and "
            .Event::$database_tableColumn_smartPlugId."='".$smartPlugId."' and "
            .Event::$database_tableColumn_date."  between '".$firstDayDate."' and '".$lastDayDate."' ";
        $res = $this->_getDatabase()->query($selectSQL);
        if(!$res)
            return $this->_response("Internal error, error querying events from db. ", HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        $mapOfEventLists = Array();
        while ($row = mysqli_fetch_array($res, MYSQLI_NUM))
        {
            $event = Event::fromSQL($row);
            $date = $event->getDate();
            if(!isset($mapOfEventLists[$date])){
                $mapOfEventLists[$date] = Array();
            }
            array_push($mapOfEventLists[$date], $event);
        }
        $profile = "";
        /** @var array $criticalDays */
        $criticalDays = Array();
        for ($i = $daysToConsider; $i >= 0; $i--){
            $day = new DateTime("-".($i + 1)." days");
            $isset =  isset($mapOfEventLists[$day->format("Y_m_d")]);
            $profile .= $isset ? "1" : "0";
            if($isset){
                foreach ($mapOfEventLists[$day->format("Y_m_d")] as $criticalDay) {
                    array_push($criticalDays, $criticalDay);
                }
            }
        }
        if(!$this->pred($profile, "1"))
            return $this->_response(Array(), HTTPStatusCode::$OK);

        $variance = 20;
        $criticalDays = $this->groupByZScores($variance, $criticalDays);
        return $this->_response($this->groupToSingleEvents($smartPlugId, $userId, $date, $criticalDays), HTTPStatusCode::$OK);
    }


    /**
     * @param string $profile
     * @param string $nextString
     * @return boolean
     */
    private function pred($profile, $nextString){
        $numberOfDaysMonitored = strlen($profile);
        $i = 1;
        while($i < $numberOfDaysMonitored){
            $s = substr($profile, $numberOfDaysMonitored - $i);
            $s1 = $s.$nextString;
            $s0 = $s.$nextString;
            $monitoringProfile = substr($profile, 0, $numberOfDaysMonitored -1);
            $occurencesOfS = $this->getOccurrenceCount($monitoringProfile, $s);
            $occurenceOfS1 = $this->getOccurrenceCount($profile, $s1);
            $occurenceOfS0 = $this->getOccurrenceCount($profile, $s0);

            /** @var float $prob0 */
            $prob0 = floatval($occurenceOfS0) / floatval($occurencesOfS);
            $prob1 = floatval($occurenceOfS1) / floatval($occurencesOfS);
            if($prob1 == 1){
                return true;
            }else if ($prob0 == 1){
                return false;
            }
            $i++;
        }
        return false;
    }


    /**
     * @param string $string
     * @param string $find
     * @return integer
     */
    private function getOccurrenceCount($string, $find){
        $foundChars = 0; $found = 0;
        for ($i = 0; $i < strlen($string); $i++){
            if($string[$i]==$find[$foundChars]){
                $foundChars++;
                if($foundChars == strlen($find)){
                    $found++;
                    $foundChars=0;
                }
            }else if ($string[$i] == $find[0]){
                $foundChars = 1;
            }else{
                $foundChars = 0;
            }
        }
        return $found;
    }

    /**
     * @param integer $variance
     * @param array $events
     * @return array
     */
    private function groupByZScores($variance, $events){
        $groupedEvents = Array();
        $sumOfMillis = 0;
        /** @var Event $event */
        foreach ($events as $event){
            $sumOfMillis+=$event->getStart();
        }
        $mean = floatval($sumOfMillis) / floatval(sizeof($events));
        foreach ($events as $event){
            $z = floatval($event->getStart() - $mean) / floatval($variance);
            if(!isset($groupedEvents[$z]))
                $groupedEvents[$z] = Array();
            array_push($groupedEvents[$z], $event);
        }
        return $groupedEvents;
    }

    /**
     * @param string $smartPlugId
     * @param integer $userId
     * @param string $date
     * @param array $eventGroups
     */
    private function groupToSingleEvents($smartPlugId, $userId, $date, $eventGroups){
        /** @var array $predictedEvents */
        $predictedEvents = Array();
        foreach ($eventGroups as $eventGroup){
            $predictedEvent = new Event();
            $meanStart = 0;
            $meanEnd = 0;
            /** @var Event $event */
            foreach ($eventGroup as $event){
                $meanStart+=$event->getStart();
                $meanEnd+=$event->getEnd();
            }
            $meanStart = floatval($meanStart) / floatval(sizeof($eventGroup));
            $meanEnd = floatval($meanEnd) / floatval(sizeof($eventGroup));
            $predictedEvent->setDate($date)->setStart($meanStart)->setEnd($meanEnd)
                ->setId(Crypt::UUIDClear())->setSmartPlugId($smartPlugId)->setUserId($userId)
                ->setPredicted(1)->setStatus(1);
            array_push($predictedEvents, $predictedEvent);
        }
    }

}
