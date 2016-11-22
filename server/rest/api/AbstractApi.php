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

    /**
     * @var string
     */
    public $authorization;


    public function __construct($method, $authorization)
    {
        $this->method = $method;
        $this->authorization = $authorization;
    }

    public function _response($data, $status) {
        header("HTTP/1.1 " . $status . " " . HTTPStatusCode::requestStatus($status));
        return utf8_encode(json_encode((array)$data));
    }

    /**
     * return string
     */
    public function _connectDatabase(){
        $conn = new mysqli(DatabaseConstants::$database_serverName, DatabaseConstants::$database_userName, DatabaseConstants::$database_password);
        if ($conn->connect_error) {
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        //create database and tables
        if(!$conn->query("create database if not exists tina3")){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        if(!$conn->query("use tina3")){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        if(!$conn->query(User::_getDatabaseTableCreateStatement())){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        if(!$conn->query(Event::_getDatabaseTableCreateStatement())){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        if(!$conn->query(SmartPlug::_getDatabaseTableCreateStatement())){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        if(!$conn->query(Token::_getDatabaseTableCreateStatement())){
            return $this->_response($conn->connect_error, HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
        $this->conn = $conn;
        return null;
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