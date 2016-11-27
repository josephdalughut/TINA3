<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 11:38 AM
 */

require_once 'Entity.php';
require_once '../crypt/Crypt.php';
require_once '../util/Time.php';

class Token extends Entity{

    public static $database_tableName = "tokens";
    public static $database_tableColumn_userId = "userId";
    public static $database_tableColumn_expiresAt = "expiresAt";
    public static $database_tableColumn_type = "type";

    private static $cookie_key_token= "token";
    private static $cookie_encryption_key = "4785e4c6c7ddb2565a460e0dba88cae24637f5084fd05e57";

    public static function _getDatabaseTableCreateStatement(){
        $sql = Token::$database_tableColumn_userId." int, "
            .Token::$database_tableColumn_type." varchar(20), "
            .Token::$database_tableColumn_expiresAt." float, ";
        return Entity::_createDatabaseTableCreateStatementPad(Token::$database_tableName, $sql);
    }

    /**
     * @var string
     */
    private $type;

    /**
     * @var integer
     */
    private $userId;

    /**
     * @var float
     */
    private $expiresAt;

    /**
     * @return string
     */
    public function getType()
    {
        return $this->type;
    }

    /**
     * @param string $type
     * @return Token
     */
    public function setType($type)
    {
        $this->type = $type;
        return $this;
    }

    /**
     * @return int
     */
    public function getUserId()
    {
        return $this->userId;
    }

    /**
     * @param int $userId
     * @return Token
     */
    public function setUserId($userId)
    {
        $this->userId = $userId;
        return $this;
    }

    /**
     * @return float
     */
    public function getExpiresAt()
    {
        return $this->expiresAt;
    }

    /**
     * @param float $expiresAt
     * @return Token
     */
    public function setExpiresAt($expiresAt)
    {
        $this->expiresAt = $expiresAt;
        return $this;
    }

    /**
     * @return boolean
     */
    public function isExpired(){
        return $this->getExpiresAt() < Time::now();
    }

    /**
     * Session constructor.
     * @param boolean $generateID
     */
    function __construct($generateID){
        if($generateID){
            $this->setId(Crypt::UUIDClear());
        }
    }

    /**
     * @param AbstractApi $api
     * @param mysqli $conn
     * @return User|string
     */
    public static function getUserFromRequest($api, $conn){
        $tokenId = $api->authorization;
        if($tokenId == Null){
            return $api->_response("token not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $findSQL = "select * from ".Token::$database_tableName." where ".Entity::$database_tableColumn_id."='".$tokenId."'";
        $res = $conn->query($findSQL);
        if(!$res||$res->num_rows<1){
            return $api->_response("token not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $tokenId = self::fromSQL(mysqli_fetch_row($res));
        if($tokenId->isExpired()){
            //return $api->_response("token Expired", HTTPStatusCode::$AUTHENTICATION_TIMEOUT);
        }
        $findUserSQL = "select * from ".User::$database_tableName." where ".User::$database_tableColumn_id."='".$tokenId->getUserId()."'";
        $resUser = $conn->query($findUserSQL);
        if(!$resUser||$resUser->num_rows<1){
            return $api->_response("user not found", HTTPStatusCode::$NOT_FOUND);
        }
        $user = User::fromSQL(mysqli_fetch_row($resUser));
        return $user;
    }


    /**
     * @param AbstractApi $api
     * @param mysqli $conn
     * @return mixed
     */
    public static function getToken($tokenId, $api, $conn){
        if($tokenId == Null){
            return $api->_response("token not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $findSQL = "select * from ".Token::$database_tableName." where ".Entity::$database_tableColumn_id."='".$tokenId."'";
        $res = $conn->query($findSQL);
        if(!$res||$res->num_rows<1){
            return $api->_response("token not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $token = self::fromSQL(mysqli_fetch_row($res));
        return $token;
    }

    /**
     * @param AbstractApi $api
     * @param mysqli $conn
     * @param User $user
     * @return Token
     */
    public function createAccessToken($api, $conn, $user){
        $now = Time::now();
        $expiresAt = $now + 3600000;
        $this->setType("access")->setUserId($user->getId())->setExpiresAt($expiresAt)->setCreatedAt($now)->setUpdatedAt($now);
        $sql = "insert into ".Token::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Token::$database_tableColumn_userId.", "
            .Token::$database_tableColumn_type.", "
            .Token::$database_tableColumn_expiresAt.","
            .Token::$database_tableColumn_createdAt.","
            .Token::$database_tableColumn_updatedAt.") values ("
            ."'".$this->getId()."', "
            ."'".$this->getUserId()."', "
            ."'".$this->getType()."', "
            ."'".$this->getExpiresAt()."', "
            ."'".$this->getCreatedAt()."', "
            ."'".$this->getUpdatedAt()."'"
            .")";
        if(!$conn->query($sql)){
            return $api->_response("service unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        return $this;
    }

    /**
     * @param AbstractApi $api
     * @param mysqli $conn
     * @param User $user
     * @return Token
     */
    public function createRefreshToken($api, $conn, $user){
        $now = Time::now();
        $this->setType("refresh")->setUserId($user->getId())->setExpiresAt(-1)->setCreatedAt($now)->setUpdatedAt($now);
        $sql = "insert into ".Token::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Token::$database_tableColumn_userId.", "
            .Token::$database_tableColumn_type.", "
            .Token::$database_tableColumn_expiresAt.","
            .Token::$database_tableColumn_createdAt.","
            .Token::$database_tableColumn_updatedAt.") values ("
            ."'".$this->getId()."', "
            ."'".$this->getUserId()."', "
            ."'".$this->getType()."', "
            ."'".$this->getExpiresAt()."', "
            ."'".$this->getCreatedAt()."', "
            ."'".$this->getUpdatedAt()."'"
            .")";
        if(!$conn->query($sql)){
            return $api->_response("service unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        return $this;
    }

    /**
     * @param array $row
     * @return Token
     */
    public static function fromSQL($row){
        $session = new Token(false);
        $session->setId($row[0]);
        $session->setUserId($row[1]);
        $session->setType($row[2]);
        $session->setExpiresAt($row[3]);
        $session->setCreatedAt($row[4]);
        $session->setUpdatedAt($row[5]);
        return $session;
    }

}