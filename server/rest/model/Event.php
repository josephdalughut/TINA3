<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 6:19 PM
 */

require_once 'Entity.php';

class Event extends Entity
{

    public static $database_tableName = "events";
    public static $database_tableColumn_userId = "userId";
    public static $database_tableColumn_smartPlugId = "smartPlugId";
    public static $database_tableColumn_date = "date";
    public static $database_tableColumn_start = "start";
    public static $database_tableColumn_end = "end";
    public static $database_tableColumn_status = "status";
    public static $database_tableColumn_predicted = "predicted";


    public static function _getDatabaseTableCreateStatement(){
        return "create table if not exists ".Event::$database_tableName." ("
            .Entity::$database_tableColumn_id." varchar(32) primary key, "
            .Event::$database_tableColumn_userId." int, "
            .Event::$database_tableColumn_smartPlugId." varchar(255), "
            .Event::$database_tableColumn_date." varchar(20), "
            .Event::$database_tableColumn_start." int(4), "
            .Event::$database_tableColumn_end." int(4), "
            .Event::$database_tableColumn_status." int(1), "
            .Event::$database_tableColumn_predicted." int(1))";
    }

    /**
     * @var string
     */
    private $id;

    /**
     * @var integer
     */
    private $userId;

    /**
     * @var string
     */
    private $smartPlugId;

    /**
     * @var string
     */
    private $date;

    /**
     * @var float
     */
    private $start;

    /**
     * @var float
     */
    private $end;

    /**
     * @var integer
     */
    private $predicted;

    /**
     * @var integer
     */
    private $status;

    /**
     * @return string
     */
    public function getUserId()
    {
        return $this->userId;
    }

    /**
     * @param string $userId
     * @return Event
     */
    public function setUserId($userId)
    {
        $this->userId = $userId;
        return $this;
    }

    /**
     * @return string
     */
    public function getSmartPlugId()
    {
        return $this->smartPlugId;
    }

    /**
     * @param string $smartPlugId
     * @return Event
     */
    public function setSmartPlugId($smartPlugId)
    {
        $this->smartPlugId = $smartPlugId;
        return $this;
    }

    /**
     * @return string
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     * @param $date
     * @return Event
     */
    public function setDate($date)
    {
        $this->date = $date;
        return $this;
    }

    /**
     * @return float
     */
    public function getStart()
    {
        return $this->start;
    }

    /**
     * @param float $start
     * @return Event
     */
    public function setStart($start)
    {
        $this->start = $start;
        return $this;
    }

    /**
     * @return float
     */
    public function getEnd()
    {
        return $this->end;
    }

    /**
     * @param float $end
     * @return Event
     */
    public function setEnd($end)
    {
        $this->end = $end;
        return $this;
    }

    /**
     * @return int
     */
    public function getPredicted()
    {
        return $this->predicted;
    }

    /**
     * @return int
     */
    public function getStatus()
    {
        return $this->status;
    }

    /**
     * @param int $status
     * @return Event
     */
    public function setStatus($status)
    {
        $this->status = $status;
        return $this;
    }

    /**
     * @return string
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param string $id
     * @return Event
     */
    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }



    /**
     * @param int $predicted
     * @return Event
     */
    public function setPredicted($predicted)
    {
        $this->predicted = $predicted;
        return $this;
    }

    /**
     * @param Event $event
     * @return string
     */
    public static function wrapToReplaceSQL($event){
        $sql = "replace into ".Event::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Event::$database_tableColumn_userId.", "
            .Event::$database_tableColumn_smartPlugId.", "
            .Event::$database_tableColumn_date.", "
            .Event::$database_tableColumn_start.", "
            .Event::$database_tableColumn_end.", "
            .Event::$database_tableColumn_status.", "
            .Event::$database_tableColumn_predicted.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$event->getId()."', "
            .$event->getUserId().", "
            ."'".$event->getSmartPlugId()."', "
            ."'".$event->getDate()."', "
            .$event->getStart().", "
            .$event->getEnd().", "
            .$event->getStatus().", "
            .$event->getPredicted().", "
            ."'".$event->getCreatedAt()."', "
            ."'".$event->getUpdatedAt()."'"
            .")";
        return $sql;
    }

    /**
     * @param array $row
     * @return Event
     */
    public static function fromSQL($row){
        $event = new Event();
        $event->setId($row[0]);
        $event->setUserId($row[1]);
        $event->setSmartPlugId($row[2]);
        $event->setDate($row[3]);
        $event->setStart($row[4]);
        $event->setEnd($row[5]);
        $event->setStatus($row[6]);
        $event->setPredicted($row[7]);
        $event->setCreatedAt($row[8]);
        $event->setUpdatedAt($row[9]);
        return $event;
    }

}