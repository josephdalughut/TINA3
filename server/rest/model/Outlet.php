<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 6:23 PM
 */
class Outlet extends Entity
{
    public static $database_tableName = "outlets";
    public static $database_tableColumn_deviceId = "deviceId";
    public static $database_tableColumn_state = "state";

    /**
     * @return string
     */
    public static function _getDatabaseTableCreateStatement(){
        $sql = Outlet::$database_tableColumn_deviceId." varchar(max), "
            .Outlet::$database_tableColumn_state." boolean, ";
        return Entity::_createDatabaseTableCreateStatementPad(Outlet::$database_tableName, $sql);
    }

    /**
     * @var string
     */
    private $deviceId = Null;

    /**
     * @var boolean
     */
    private $state = Null;

    /**
     * @return string
     */
    public function getDeviceId()
    {
        return $this->deviceId;
    }

    /**
     * @param string $deviceId
     * @return Outlet
     */
    public function setDeviceId($deviceId)
    {
        $this->deviceId = $deviceId;
        return this;
    }

    /**
     * @return boolean
     */
    public function isState()
    {
        return $this->state;
    }

    /**
     * @param boolean $state
     * @return Outlet
     */
    public function setState($state)
    {
        $this->state = $state;
        return $this;
    }

    /**
     * @param Outlet $outlet
     * @return string
     */
    public static function wrapToInsertSQL($outlet){
        $sql = "insert into ".Outlet::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Outlet::$database_tableColumn_deviceId.", "
            .Outlet::$database_tableColumn_state.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$outlet->getId()."', "
            ."'".$outlet->getDeviceId()."', "
            ."'".$outlet->isState()."', "
            ."'".$outlet->getCreatedAt()."', "
            ."'".$outlet->getUpdatedAt()."'"
            .")";
        return $sql;
    }

    /**
     * @param array $row
     * @return Outlet
     */
    public static function fromSQL($row){
        $outlet = new Outlet();
        $outlet->setId($row[0]);
        $outlet->setDeviceId($row[1]);
        $outlet->setState($row[2]);
        $outlet->setCreatedAt($row[3]);
        $outlet->setUpdatedAt($row[4]);
        return $outlet;
    }

}