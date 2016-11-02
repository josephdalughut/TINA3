<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 5:49 PM
 */
class Device extends Entity
{

    public static $database_tableName = "devices";
    public static $database_tableColumn_deviceName = "deviceName";
    public static $database_tableColumn_devicePassKey = "devicePassKey";

    /**
     * @return string
     */
    public static function _getDatabaseTableCreateStatement(){
        $sql = Device::$database_tableColumn_deviceName." varchar(40), "
            .Device::$database_tableColumn_devicePassKey." varchar(4), ";
        return Entity::_createDatabaseTableCreateStatementPad(Device::$database_tableName, $sql);
    }

    /*** @var string */
    private $deviceName = Null;

    /*** @var string */
    private $devicePassKey = Null;

    /**
     * @return string
     */
    public function getDeviceName()
    {
        return $this->deviceName;
    }

    /**
     * @param string $deviceName
     * @return Device
     */
    public function setDeviceName($deviceName)
    {
        $this->deviceName = $deviceName;
        return $this;
    }

    /**
     * @return string
     */
    public function getDevicePassKey()
    {
        return $this->devicePassKey;
    }

    /**
     * @param string $devicePassKey
     * @return Device
     */
    public function setDevicePassKey($devicePassKey)
    {
        $this->devicePassKey = $devicePassKey;
        return $this;
    }

    /**
     * @param Device $device
     * @return string
     */
    public static function wrapToInsertSQL($device){
        $sql = "insert into ".Device::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Device::$database_tableColumn_deviceName .", "
            .Device::$database_tableColumn_devicePassKey.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$device->getId()."', "
            ."'".$device->getDeviceName()."', "
            ."'".$device->getDevicePassKey()."', "
            ."'".$device->getCreatedAt()."', "
            ."'".$device->getUpdatedAt()."'"
            .")";
        return $sql;
    }

    /**
     * @param array $row
     * @return Device
     */
    public static function fromSQL($row){
        $device = new Device();
        $device->setId($row[0]);
        $device->setDeviceName($row[1]);
        $device->setDevicePassKey($row[2]);
        $device->setCreatedAt($row[3]);
        $device->setUpdatedAt($row[4]);
        return $device;
    }


}