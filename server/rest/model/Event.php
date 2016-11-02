<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 6:19 PM
 */
class Event extends Entity
{

    public static $database_tableName = "events";
    public static $database_tableColumn_deviceId = "deviceId";
    public static $database_tableColumn_outlet = "outletId";
    public static $database_tableColumn_year = "year";
    public static $database_tableColumn_monthOfYear = "monthOfYear";
    public static $database_tableColumn_dayOfMonth = "dayOfMonth";
    public static $database_tableColumn_dayOfWeek = "dayOfWeek";
    public static $database_tableColumn_hourOfDay = "hourOfDay";
    public static $database_tableColumn_minOfHour = "minOfHour";
    public static $database_tableColumn_secOfMin = "secOfMin";

    public static function _getDatabaseTableCreateStatement(){
        return "create table if not exists ".Event::$database_tableName." ( "
            .Entity::$database_tableColumn_id." int unsigned auto_increment primary key, "
            .Event::$database_tableColumn_deviceId." varchar(max), "
            .Event::$database_tableColumn_outlet." varchar(max), "
            .Event::$database_tableColumn_year." int(4), "
            .Event::$database_tableColumn_monthOfYear." int(2), "
            .Event::$database_tableColumn_dayOfMonth." int(2), "
            .Event::$database_tableColumn_dayOfWeek." varchar(3), "
            .Event::$database_tableColumn_hourOfDay." int(2), "
            .Event::$database_tableColumn_minOfHour." int(2), "
            .Event::$database_tableColumn_secOfMin." int(2), "
            .Entity::$database_tableColumn_createdAt." datetime, "
            .Entity::$database_tableColumn_updatedAt." datetime )";
    }

    /*** @var string */
    public $deviceId = Null;
    /*** @var string */
    public $outletId = Null;
    /*** @var integer */
    public $year = Null;
    /*** @var integer */
    public $monthOfYear = Null;
    /*** @var integer */
    public $dayOfMonth = Null;
    /*** @var string */
    public $dayOfWeek = Null;

    /*** @var integer */
    public $hourOfDay = Null;
    /*** @var integer */
    public $minOfHour = Null;
    /*** @var integer */
    public $secOfMin = Null;

    /**
     * @return string
     */
    public function getDeviceId()
    {
        return $this->deviceId;
    }

    /**
     * @param string $deviceId
     * @return Event
     */
    public function setDeviceId($deviceId)
    {
        $this->deviceId = $deviceId;
        return $this;
    }

    /**
     * @return string
     */
    public function getOutletId()
    {
        return $this->outletId;
    }

    /**
     * @param string $outletId
     * @return Event
     */
    public function setOutletId($outletId)
    {
        $this->outletId = $outletId;
        return $this;
    }

    /**
     * @return int
     */
    public function getYear()
    {
        return $this->year;
    }

    /**
     * @param int $year
     * @return Event
     */
    public function setYear($year)
    {
        $this->year = $year;
        return $this;
    }

    /**
     * @return int
     */
    public function getMonthOfYear()
    {
        return $this->monthOfYear;
    }

    /**
     * @param int $monthOfYear
     * @return Event
     */
    public function setMonthOfYear($monthOfYear)
    {
        $this->monthOfYear = $monthOfYear;
        return $this;
    }

    /**
     * @return int
     */
    public function getDayOfMonth()
    {
        return $this->dayOfMonth;
    }

    /**
     * @param int $dayOfMonth
     * @return Event
     */
    public function setDayOfMonth($dayOfMonth)
    {
        $this->dayOfMonth = $dayOfMonth;
        return $this;
    }

    /**
     * @return string
     */
    public function getDayOfWeek()
    {
        return $this->dayOfWeek;
    }

    /**
     * @param string $dayOfWeek
     * @return Event
     */
    public function setDayOfWeek($dayOfWeek)
    {
        $this->dayOfWeek = $dayOfWeek;
        return $this;
    }

    /**
     * @return int
     */
    public function getHourOfDay()
    {
        return $this->hourOfDay;
    }

    /**
     * @param int $hourOfDay
     * @return Event
     */
    public function setHourOfDay($hourOfDay)
    {
        $this->hourOfDay = $hourOfDay;
        return $this;
    }

    /**
     * @return int
     */
    public function getMinOfHour()
    {
        return $this->minOfHour;
    }

    /**
     * @param int $minOfHour
     * @return Event
     */
    public function setMinOfHour($minOfHour)
    {
        $this->minOfHour = $minOfHour;
        return $this;
    }

    /**
     * @return int
     */
    public function getSecOfMin()
    {
        return $this->secOfMin;
    }

    /**
     * @param int $secOfMin
     * @return Event
     */
    public function setSecOfMin($secOfMin)
    {
        $this->secOfMin = $secOfMin;
        return $this;
    }

}