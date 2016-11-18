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
    public static $database_tableColumn_state = "state";

    /**
     * @return string
     */
    public static function _getDatabaseTableCreateStatement(){
        $sql = SmartPlug::$database_tableColumn_userId." int, "
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
     * @param SmartPlug $outlet
     * @return string
     */
    public static function wrapToInsertSQL($outlet){
        $sql = "insert into ".SmartPlug::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .SmartPlug::$database_tableColumn_userId.", "
            .SmartPlug::$database_tableColumn_state.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$outlet->getId()."', "
            ."'".$outlet->getUserId()."', "
            ."'".$outlet->isState()."', "
            ."'".$outlet->getCreatedAt()."', "
            ."'".$outlet->getUpdatedAt()."'"
            .")";
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
        $outlet->setState($row[2]);
        $outlet->setCreatedAt($row[3]);
        $outlet->setUpdatedAt($row[4]);
        return $outlet;
    }

}