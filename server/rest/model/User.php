<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 5:49 PM
 */

require_once 'Entity.php';

class User extends Entity
{

    public static $database_tableName = "users";
    public static $database_tableColumn_username = "username";
    public static $database_tableColumn_password = "password";

    /**
     * @return string
     */
    public static function _getDatabaseTableCreateStatement(){
        $sql = "create table if not exists ".User::$database_tableName." ("
            .Entity::$database_tableColumn_id." int unsigned auto_increment primary key, "
            .User::$database_tableColumn_username." varchar(20), "
            .User::$database_tableColumn_password." varchar(30), "
            .Entity::$database_tableColumn_createdAt." float, "
            .Entity::$database_tableColumn_updatedAt." float)";
        return $sql;
    }

    /**
     * @var integer null
     */
    private $id = Null;

    /*** @var string */
    private $username = Null;

    /*** @var string */
    private $password = Null;

    /**
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param int $id
     * @return User
     */
    public function setId($id)
    {
        $this->id = $id;
        return $this;
    }



    /**
     * @return string
     */
    public function getUsername()
    {
        return $this->username;
    }

    /**
     * @param string $username
     * @return User
     */
    public function setUsername($username)
    {
        $this->username = $username;
        return $this;
    }

    /**
     * @return string
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * @param string $password
     * @return User
     */
    public function setPassword($password)
    {
        $this->password = $password;
        return $this;
    }

    /**
     * @param User $device
     * @return string
     */
    public static function wrapToInsertSQL($device){
        $sql = "insert into ".User::$database_tableName." ("
            .User::$database_tableColumn_username .", "
            .User::$database_tableColumn_password.", "
            .Entity::$database_tableColumn_createdAt.", "
            .Entity::$database_tableColumn_updatedAt.") values ("
            ."'".$device->getUsername()."', "
            ."'".$device->getPassword()."', "
            ."'".$device->getCreatedAt()."', "
            ."'".$device->getUpdatedAt()."'"
            .")";
        return $sql;
    }

    /**
     * @param array $row
     * @return User
     */
    public static function fromSQL($row){
        $user = new User();
        $user->setId($row[0]);
        $user->setUsername($row[1]);
        $user->setPassword($row[2]);
        $user->setCreatedAt($row[3]);
        $user->setUpdatedAt($row[4]);
        return $user;
    }

    /**
     * @param array $array
     * @return User
     */
    public static function fromArray($array){
        $user = new User();
        $user->setId($array[Entity::$database_tableColumn_id]);
        $user->setUsername($array[User::$database_tableColumn_username]);
        $user->setPassword($array[User::$database_tableColumn_password]);
        $user->setCreatedAt($array[Entity::$database_tableColumn_createdAt]);
        $user->setUpdatedAt($array[Entity::$database_tableColumn_updatedAt]);
    }


}