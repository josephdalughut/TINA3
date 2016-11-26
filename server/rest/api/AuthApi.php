<?php

require_once 'Api.php';
require_once 'AbstractApi.php';
require_once '../model/User.php';
require_once '../model/Token.php';
require_once '../util/HTTPStatusCode.php';

class AuthApi extends AbstractApi
{

    public function __construct($method, $authorization)
    {
        parent::__construct($method, $authorization);
    }

    /**
     * @param array $args
     * @return string
     */
    public function login($args){
        if(!self::checkParams($args, "username", "password")){
            return $this->_response("required parameter not found", HTTPStatusCode::$BAD_REQUEST);
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
        if(!password_verify($password, $pass)){
            return $this->_response("Conflict", HTTPStatusCode::$CONFLICT);
        }
        $user = User::fromSQL($row);
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

    /**
     * @param $args
     * @return string
     */
    public function refresh($args){
        if(!$this->method == "POST"){
            return $this->_response("Only POST requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        if(!self::checkParams($args, "refresh_token")){
            return $this->_response("required parameter not found", HTTPStatusCode::$BAD_REQUEST);
        }
        $refresh_token = Token::getToken($args["refresh_token"], $this, $this->_getDatabase());
        if(!$refresh_token instanceof Token){
            return $refresh_token;
        }
        $arr = Array();
        $user = new User();
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