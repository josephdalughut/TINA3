<?php

require_once 'Api.php';
require_once 'AbstractApi.php';
require_once '../model/User.php';
require_once '../model/Token.php';
require_once '../util/HTTPStatusCode.php';

class UserApi extends AbstractApi
{
    /**
     * @param array $args
     * @return string
     */
    public function signup($args){
        if(!$this->method == "POST"){
            return $this->_response("only POST requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        if(!self::checkParams($args, "username", "password")){
            return $this->_response("required parameter not found", HTTPStatusCode::$BAD_REQUEST);
        }
        $username = $args["username"];
        $password = $args["password"];
        $user = new User();
        $user->setUsername($username)->setPassword($password);
        $findSQL = "select * from ".User::$database_tableName." where ".User::$database_tableColumn_username." ='".$user->getUsername()."'";
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("service Unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if ($res->num_rows > 0){
            return $this->_response("conflict: user", HTTPStatusCode::$CONFLICT);
        }
        $pass = password_hash($password, PASSWORD_BCRYPT);
        $now = Time::now();
        $user->setPassword($pass)->setCreatedAt($now)->setUpdatedAt($now);
        $insertSQL = User::wrapToInsertSQL($user);

        if(!$this->_getDatabase()->query($insertSQL)){
            return $this->_response("failed to create User", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }

        $user->setPassword(Null);
        $arr = Array();
        $access_token = new Token(true);
        $refresh_token = new Token(true);
        $access_token = $access_token->createAccessToken($this, $this->_getDatabase(), $user);
        $refresh_token = $refresh_token->createAccessToken($this, $this->_getDatabase(), $user);
        $arr["access_token"] = "".$access_token->getId();
        $arr["refresh_token"] = "".$refresh_token->getId();
        $arr["expiresAt"] = "".$access_token->getExpiresAt();
        $user->setData($arr);
        return $this->_response($user, HTTPStatusCode::$OK);
    }
}
