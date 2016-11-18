<?php

require_once 'API.php';
require_once 'AbstractAPI.php';
require_once '../model/User.php';
require_once '../model/Session.php';
require_once '../util/HTTPStatusCode.php';

class AuthAPI extends AbstractAPI
{

    public function __construct($method)
    {
        parent::__construct($method);
    }

    /**
     * @param array $args
     * @throws Exception
     * @return string
     */
    public function auth($args){
        if(!self::checkParams($args, "username", "password")){
            return $this->_response("Required parameter not found", HTTPStatusCode::$NOT_FOUND);
        }
        $username = $args["username"];
        $password = $args["password"];
        if(!$this->method == "GET"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = new User();
        $user->setUsername($username)->setPassword($password);
        $findSQL = "select * from ".User::$database_tableName." where ".User::$database_tableColumn_username." ='".$user->getUsername()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if($res->num_rows<1){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }
        $row = mysqli_fetch_row($res);
        $pass = $row[2];
        $hash = password_hash($user->getPassword(), PASSWORD_BCRYPT);
        if(!$hash == $pass){
            return $this->_response("Conflict", HTTPStatusCode::$CONFLICT);
        }
        $user = User::fromSQL($row);
        $user->setPassword(Null);
        $arr = Array();
        $access_session = new Session(true);
        $refresh_session = new Session(true);
        $arr["data"] = json_encode($user);
        $arr["access"] = $access_session->toAccessToken($this, $this->_getDatabase(), $user);
        $arr["refresh"] = $refresh_session->toRefreshToken($this, $this->_getDatabase(), $user);
        return json_encode($arr);
    }

    /**
     * @return string
     */
    public function refresh(){
        $session = Session::toSession($this, $this->_getDatabase(), $_COOKIE);
        if(!$session instanceof Session){
            return $session;
        }
        $arr = Array();
        $access_session = new Session(true);
        $refresh_session = new Session(true);
        $user = new User();
        $user->setId($session->getUserId());
        $arr["access"] = $access_session->toAccessToken($this, $this->_getDatabase(), $user);
        $arr["refresh"] = $refresh_session->toRefreshToken($this, $this->_getDatabase(), $user);
        return json_encode($arr);
    }

}