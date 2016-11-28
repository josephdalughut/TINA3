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
    public static $database_tableColumn_predicted = "predicted";


    public static function _getDatabaseTableCreateStatement(){
        return "create table if not exists ".Event::$database_tableName." ("
            .Entity::$database_tableColumn_id." varchar(32) primary key, "
            .Event::$database_tableColumn_userId." int, "
            .Event::$database_tableColumn_smartPlugId." varchar(255), "
            .Event::$database_tableColumn_date." varchar(20), "
            .Event::$database_tableColumn_start." int(4), "
            .Event::$database_tableColumn_end." int(4), "
            .Entity::$database_tableColumn_predicted." int(1))";
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
     * @return string
     */
    public function getUserId()
    {
        return $this->userId;
    }

    /**
     * @param string $userId
     */
    public function setUserId($userId)
    {
        $this->userId = $userId;
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
     */
    public function setSmartPlugId($smartPlugId)
    {
        $this->smartPlugId = $smartPlugId;
    }

    /**
     * @return string
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     * @param string $date
     */
    public function setDate($date)
    {
        $this->date = $date;
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
     */
    public function setStart($start)
    {
        $this->start = $start;
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
     */
    public function setEnd($end)
    {
        $this->end = $end;
    }

    /**
     * @return int
     */
    public function getPredicted()
    {
        return $this->predicted;
    }

    /**
     * @param int $predicted
     */
    public function setPredicted($predicted)
    {
        $this->predicted = $predicted;
    }



}