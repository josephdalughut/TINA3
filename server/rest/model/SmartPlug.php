<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 6:23 PM
 */

require_once 'Entity.php';

class SmartPlug extends Entity
{
    public static $database_tableName = "smartPlugs";
    public static $database_tableColumn_userId = "userId";
    public static $database_tableColumn_name = "name";
    public static $database_tableColumn_type = "type";
    public static $database_tableColumn_state = "state";

    /**
     * @return string
     */
    public static function _getDatabaseTableCreateStatement(){
        $sql = SmartPlug::$database_tableColumn_userId." int, "
            .SmartPlug::$database_tableColumn_name." varchar(255), "
            .SmartPlug::$database_tableColumn_type." varchar(30), "
            .SmartPlug::$database_tableColumn_state." varchar(5), ";
        return Entity::_createDatabaseTableCreateStatementPad(SmartPlug::$database_tableName, $sql);
    }

    /**
     * @var integer
     */
    private $userId = Null;

    /**
     * @var string
     */
    private $state = Null;

    /**
     * @var string
     */
    private $name = Null;

    /**
     * @var string
     */
    private $type = Null;

    /**
     * @return integer
     */
    public function getUserId()
    {
        return $this->userId;
    }

    /**
     * @param integer $userId
     * @return SmartPlug
     */
    public function setUserId($userId)
    {
        $this->userId = $userId;
        return $this;
    }

    /**
     * @return string
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * @param string $state
     * @return SmartPlug
     */
    public function setState($state)
    {
        $this->state = $state;
        return $this;
    }

    /**
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * @param string $name
     * @return SmartPlug
     */
    public function setName($name)
    {
        $this->name = $name;
        return $this;
    }

    /**
     * @return string
     */
    public function getType()
    {
        return $this->type;
    }

    /**
     * @param string $type
     * @return SmartPlug
     */
    public function setType($type)
    {
        $this->type = $type;
        return $this;
    }


    /**
     * @param SmartPlug $outlet
     * @return string
     */
    public static function wrapToInsertSQL($outlet){
        $sql = "insert into ".SmartPlug::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .SmartPlug::$database_tableColumn_userId.", "
            .SmartPlug::$database_tableColumn_name.", "
            .SmartPlug::$database_tableColumn_type.", "
            .SmartPlug::$database_tableColumn_state.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$outlet->getId()."', "
            .$outlet->getUserId().", "
            ."'".$outlet->getName()."', "
            ."'".$outlet->getType()."', "
            ."'".$outlet->getState()."', "
            ."'".$outlet->getCreatedAt()."', "
            ."'".$outlet->getUpdatedAt()."'"
            .")";
        return $sql;
    }



    /**
     * @param SmartPlug $outlet
     * @return string
     */
    public static function wrapToReplaceSQL($outlet){
        $sql = "replace into ".SmartPlug::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .SmartPlug::$database_tableColumn_userId.", "
            .SmartPlug::$database_tableColumn_name.", "
            .SmartPlug::$database_tableColumn_type.", "
            .SmartPlug::$database_tableColumn_state.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$outlet->getId()."', "
            .$outlet->getUserId().", "
            ."'".$outlet->getName()."', "
            ."'".$outlet->getType()."', "
            ."'".$outlet->getState()."', "
            ."'".$outlet->getCreatedAt()."', "
            ."'".$outlet->getUpdatedAt()."'"
            .")";
        return $sql;
    }

    /**
     * @param SmartPlug $outlet
     * @return string
     */
    public static function wrapToUpdateSQL($outlet){
        $sql = "update ".SmartPlug::$database_tableName." set "
            .SmartPlug::$database_tableColumn_userId." =".$outlet->getUserId().", "
            .SmartPlug::$database_tableColumn_name." ="."'".$outlet->getName()."', "
            .SmartPlug::$database_tableColumn_type." ="."'".$outlet->getType()."', "
            .SmartPlug::$database_tableColumn_state." ="."'".$outlet->getState()."', "
            .Entity::$database_tableColumn_createdAt." ="."'".$outlet->getCreatedAt()."', "
            .Entity::$database_tableColumn_updatedAt." ="."'".$outlet->getUpdatedAt()."' "
            ."where ".Entity::$database_tableColumn_id." ="."'".$outlet->getId()."';";

        return $sql;
    }

    /**
     * @param array $row
     * @return SmartPlug
     */
    public static function fromSQL($row){
        $outlet = new SmartPlug();
        $outlet->setId($row[0]);
        $outlet->setUserId($row[1]);
        $outlet->setName($row[2]);
        $outlet->setType($row[3]);
        $outlet->setState($row[4]);
        $outlet->setCreatedAt($row[5]);
        $outlet->setUpdatedAt($row[6]);
        return $outlet;
    }

}