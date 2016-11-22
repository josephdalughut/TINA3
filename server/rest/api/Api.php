<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 10:53 AM
 */

require_once '../data/DatabaseConstants.php';
require_once '../model/User.php';
require_once '../model/Entity.php';
require_once '../model/Event.php';
require_once '../model/SmartPlug.php';
require_once '../util/HTTPStatusCode.php';
require_once 'AuthApi.php';
require_once 'EventApi.php';
require_once 'SmartPlugApi.php';
require_once 'UserApi.php';

if (!date_default_timezone_set('UTC')){
    echo "Timezone 'UTC' unknown, Maybe try updating tzinfo with your package manager?";
}

class Api
{

    protected $method = '';
    protected $endpoint = '';
    protected $endpointClass = '';
    protected $verb = '';
    protected $args = Array();
    protected $file = Null;

    //constructor
    public function __construct(){
        $this->_checkTimeSync();
        //headers, send raw HTTP header to client
        header("Access-Control-Allow-Origin: *");
        header("Access-Control-Allow-Methods: *");
        header("Content-Type: application/json");
        $req = array_shift($_REQUEST);
        $reqs = explode("/", $req);
        $this->endpointClass = $reqs[0];
        $this->endpoint = $reqs[1];
        $this->args = $_REQUEST;

        $headers = getallheaders();
        $this->authorization = $headers["Authorization"];
        if(!$this->authorization==null && !$this->authorization==""){
            $this->authorization = explode(" ", $this->authorization)[1];
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
        echo ($this->processAPI());
    }

    public function processAPI() {
        if($this->endpoint == '' || $this ->endpointClass == ''){
            return $this->_response("REQUEST FORMAT INVALID", HTTPStatusCode::$BAD_REQUEST);
        }
        /**
         * @var AbstractApi
         */
        $apiClass = null;
        switch ($this->endpointClass){
            case "user":
                $apiClass = new UserApi($this->method, $this->authorization);
                break;
            case "smartPlug":
                $apiClass = new SmartPlugApi($this->method, $this->authorization);
                break;
            case "event":
                $apiClass = new EventApi($this->method, $this->authorization);
                break;
            case "auth":
                $apiClass = new AuthApi($this->method, $this->authorization);
                break;
            default:
                return $this->_response("No API: $this->endpointClass", HTTPStatusCode::$NOT_FOUND);
        }
        $error = $apiClass->_connectDatabase();
        if($error != null){
            return $error;
        }
        return $this->_respond($apiClass, $this->endpoint, $this->args);
    }

    /**
     * @return string
     */
    public function _respond($object, $method, $args){
        if (method_exists($object, $method)) {
            return $object->{$method}($args);
        }
        return $this->_response("No Endpoint: $this->endpoint", HTTPStatusCode::$NOT_FOUND);
    }

    public function _response($data, $status) {
        header("HTTP/1.1 " . $status . " " . HTTPStatusCode::requestStatus($status));
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

    private function _checkTimeSync(){
        $t=round(microtime(true) * 1000);
        if($t < 1444732449623){
            echo("System time incorrect, please check again");
            $this->_response("incorrect system time", HTTPStatusCode::$INTERNAL_SERVER_ERROR);
        }
    }
}
new Api();
