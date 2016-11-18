<?php

require_once 'API.php';
require_once 'AbstractAPI.php';
require_once '../model/User.php';
require_once '../model/Session.php';
require_once '../util/HTTPStatusCode.php';

class UserAPI extends AbstractAPI
{

    /**
     * @param array $args
     * @throws Exception
     * @return string
     */
    public function signup($args){
        if(!$this->method == "POST"){
            $this->_response("Only POST requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
            throw new Exception();
        }
        if(!self::checkParams($args, "username", "password")){
            return $this->_response("Required parameter not found", HTTPStatusCode::$NOT_FOUND);
        }
        $username = $args["username"];
        $password = $args["password"];
        $user = new User();
        $user->setUsername($username)->setPassword($password);
        $findSQL = "select * from ".User::$database_tableName." where ".User::$database_tableColumn_username." ='".$user->getUsername()."'";
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Service Unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if ($res->num_rows > 0){
            return $this->_response("Conflict: user", HTTPStatusCode::$CONFLICT);
        }
        $pass = password_hash($user->getPassword(), PASSWORD_BCRYPT);
        $now = Time::now();
        $user->setPassword($pass)->setCreatedAt($now)->setUpdatedAt($now);
        $insertSQL = User::wrapToInsertSQL($user);

        if(!$this->_getDatabase()->query($insertSQL)){
            return $this->_response("Failed to create User", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }

        $user->setPassword(Null);
        $arr = Array();
        $arr["data"] = json_encode($user);
        return json_encode($arr);
    }
}
