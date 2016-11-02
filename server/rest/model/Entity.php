<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 5:57 PM
 */
class Entity
{

    /**
     * @param string $tableName
     * @param string $statement
     * @return string
     */
    public static function _createDatabaseTableCreateStatementPad($tableName, $statement){
        return "create table if not exists ".$tableName." ( "
        .Entity::$database_tableColumn_id." varchar(max) primary key, "
        .$statement
        .Entity::$database_tableColumn_createdAt." datetime, "
        .Entity::$database_tableColumn_updatedAt." datetime )";
    }

    public static $database_tableColumn_id = "id";
    public static $database_tableColumn_createdAt = "createdAt";
    public static $database_tableColumn_updatedAt = "updatedAt";

    /*** @var string */
    private $id = Null;

    /*** @var DateTime */
    private $createdAt = Null;

    /*** @var DateTime */
    private $updatedAt = Null;

    /**
     * @return string
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param string $id
     * @return Entity
     */
    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }

    /**
     * @return DateTime
     */
    public function getCreatedAt()
    {
        return $this->createdAt;
    }

    /**
     * @param DateTime $createdAt
     * @return Entity
     */
    public function setCreatedAt($createdAt)
    {
        $this->createdAt = $createdAt;
        return $this;
    }

    /**
     * @return DateTime
     */
    public function getUpdatedAt()
    {
        return $this->updatedAt;
    }

    /**
     * @param DateTime $updatedAt
     * @return Entity
     */
    public function setUpdatedAt($updatedAt)
    {
        $this->updatedAt = $updatedAt;
        return $this;
    }



}