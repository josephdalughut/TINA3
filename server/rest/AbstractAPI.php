<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 10:53 AM
 */

abstract class AbstractAPI
{

    protected $method = '';
    protected $endpoint = '';
    protected $verb = '';
    protected $args = Array();
    protected $file = Null;

    /**
     * @var mysqli
     */
    protected $conn = Null;

    //constructor
    public function __construct($request){

        $this->_connectDatabase();

        //headers, send raw HTTP header to client
        header("Access-Control-Allow-Origin: *");
        header("Access-Control-Allow-Methods: *");
        header("Content-Type: application/json");

        //split arguments via delimiter '/', as our $args array
        $this->args = explode('/', rtrim($request, '/'));

        //first element should be our endpoint
        $this->endpoint = array_shift($this->args);

        //next element should be verb
        if (array_key_exists(0, $this->args) && !is_numeric($this->args[0])) {
            $this->verb = array_shift($this->args);
        }
        $this->method = $_SERVER['REQUEST_METHOD'];
        if ($this->method == 'POST' && array_key_exists('HTTP_X_HTTP_METHOD', $_SERVER)) {
            if ($_SERVER['HTTP_X_HTTP_METHOD'] == 'DELETE') {
                $this->method = 'DELETE';
            } else if ($_SERVER['HTTP_X_HTTP_METHOD'] == 'PUT') {
                $this->method = 'PUT';
            } else {
                throw new Exception("Unexpected Header");
            }
        }

        switch($this->method) {
            case 'DELETE':
            case 'POST':
                $this->request = $this->_cleanInputs($_POST);
                break;
            case 'GET':
                $this->request = $this->_cleanInputs($_GET);
                break;
            case 'PUT':
                $this->request = $this->_cleanInputs($_GET);
                $this->file = file_get_contents("php://input");
                break;
            default:
                $this->_response('Invalid Method', 405);
                break;
        }
        foreach ($this->args as $arg){
            echo ("argument is ".$arg);
        }
    }



    private function _connectDatabase(){
        $conn = new mysqli(DatabaseConstants::$database_serverName, DatabaseConstants::$database_userName, DatabaseConstants::$database_password);
        if ($conn->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }

        //create database tables
        $conn->query(Device::_getDatabaseTableCreateStatement());
        $conn->query(Event::_getDatabaseTableCreateStatement());
        $conn->query(Outlet::_getDatabaseTableCreateStatement());
    }

    /**
     * @return mysqli
     */
    public function _getDatabase(){
        return $this->conn;
    }

    public function processAPI() {
        if (method_exists($this, $this->endpoint)) {
            return $this->_response($this->{$this->endpoint}($this->args));
        }
        return $this->_response("No Endpoint: $this->endpoint", 404);
    }

    public function _response($data, $status = 200) {
        header("HTTP/1.1 " . $status . " " . $this->_requestStatus($status));
        return json_encode($data);
    }

    private function _cleanInputs($data) {
        $clean_input = Array();
        if (is_array($data)) {
            foreach ($data as $k => $v) {
                $clean_input[$k] = $this->_cleanInputs($v);
            }
        } else {
            $clean_input = trim(strip_tags($data));
        }
        return $clean_input;
    }

    private function _requestStatus($code) {
        $status = array(
            200 => 'OK',
            401 => 'Unauthorized Exception',
            403 => 'Forbidden Exception',
            404 => 'Not Found Exception',
            409 => 'Conflict Exception',
            500 => 'Internal Server Error',
            503 => 'Service Unavailable Exception'
        );
        return ($status[$code])?$status[$code]:$status[500];
    }

}