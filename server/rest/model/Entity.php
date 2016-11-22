<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 5:57 PM
 */
abstract class Entity
{

    /**
     * @param string $tableName
     * @param string $statement
     * @return string
     */
    public static function _createDatabaseTableCreateStatementPad($tableName, $statement){
        return "create table if not exists ".$tableName." ("
        .Entity::$database_tableColumn_id." varchar(255) primary key, "
        .$statement
        .Entity::$database_tableColumn_createdAt." float, "
        .Entity::$database_tableColumn_updatedAt." float)";
    }

    public static $database_tableColumn_id = "id";
    public static $database_tableColumn_createdAt = "createdAt";
    public static $database_tableColumn_updatedAt = "updatedAt";

    /*** @var string */
    public $id = Null;

    /*** @var float */
    public $createdAt = Null;

    /*** @var float */
    public $updatedAt = Null;

    /*** @var array */
    public $data;

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
     * @return float
     */
    public function getCreatedAt()
    {
        return $this->createdAt;
    }

    /**
     * @param float $createdAt
     * @return Entity
     */
    public function setCreatedAt($createdAt)
    {
        $this->createdAt = $createdAt;
        return $this;
    }

    /**
     * @return float
     */
    public function getUpdatedAt()
    {
        return $this->updatedAt;
    }

    /**
     * @param float $updatedAt
     * @return Entity
     */
    public function setUpdatedAt($updatedAt)
    {
        $this->updatedAt = $updatedAt;
        return $this;
    }

    /**
     * @return array
     */
    public function getData()
    {
        return $this->data;
    }

    /**
     * @param array $data
     */
    public function setData($data)
    {
        $this->data = $data;
    }



}