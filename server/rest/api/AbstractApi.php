<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 11/3/16
 * Time: 4:52 PM
 */
require_once '../data/DatabaseConstants.php';
require_once '../model/User.php';
require_once '../model/Entity.php';
require_once '../model/Event.php';
require_once '../model/SmartPlug.php';
require_once '../util/HTTPStatusCode.php';

class AbstractApi
{

    /**
     * @var mysqli
     */
    private $conn = Null;
    public $method = '';


    public function __construct($method)
    {
        $this->method = $method;
        $this->_connectDatabase();
    }

    public function _response($data, $status = 200) {
        header("HTTP/1.1 " . $status . " " . HTTPStatusCode::requestStatus($status));
        return json_encode($data);
    }

    private function _connectDatabase(){
        $conn = new mysqli(DatabaseConstants::$database_serverName, DatabaseConstants::$database_userName, DatabaseConstants::$database_password);
        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }
        //create database and tables
        if(!$conn->query("create database if not exists tina3")){
            echo ("MySQL Error Creating database: ".$conn->error);
        }
        if(!$conn->query("use tina3")){
            echo ("MySQL Error selecting database: ".$conn->error);
        }
        if(!$conn->query(User::_getDatabaseTableCreateStatement())){
            echo ("MySQL Error Creating Devices table: ".$conn->error);
        }
        if(!$conn->query(Event::_getDatabaseTableCreateStatement())){
            echo ("MySQL Error Creating events table: ".$conn->error);
        }
        if(!$conn->query(SmartPlug::_getDatabaseTableCreateStatement())){
            echo ("MySQL Error Creating outlets table: ".$conn->error);
        }
        if(!$conn->query(Token::_getDatabaseTableCreateStatement())){
            echo ("MySQL Error Creationg session table: ".$conn->error);
        }
        $this->conn = $conn;
    }

    /**
     * @return mysqli
     */
    public function _getDatabase(){
        return $this->conn;
    }

    /**
     * @param array $args
     * @param array ...$params
     * @throws Exception
     * @return boolean
     */
    public static function checkParams($args, ...$params){
        try {
            foreach ($params as $item) {
                if(!isset($args[$item]))
                    return false;
            }
            return true;
        }catch (Exception $ignored){
            return false;
        }
    }


}