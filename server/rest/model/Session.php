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

class Session extends Entity{

    public static $database_tableName = "sessions";
    public static $database_tableColumn_userId = "userId";
    public static $database_tableColumn_expiresAt = "expiresAt";
    public static $database_tableColumn_type = "type";

    private static $cookie_key_session = "session";
    private static $cookie_encryption_key = "4785e4c6c7ddb2565a460e0dba88cae24637f5084fd05e57";

    public static function _getDatabaseTableCreateStatement(){
        $sql = Session::$database_tableColumn_userId." int, "
            .Session::$database_tableColumn_type." varchar(20), "
            .Session::$database_tableColumn_expiresAt." float, ";
        return Entity::_createDatabaseTableCreateStatementPad(Session::$database_tableName, $sql);
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
     * @return Session
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
     * @return Session
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
     * @return Session
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
     * @param AbstractAPI $api
     * @param mysqli $conn
     * @param array $cookie
     * @throws Exception
     * @return User|string
     */
    public static function toUser($api, $conn, $cookie){
        if(!AbstractAPI::checkParams($cookie, Session::$cookie_key_session)){
            return $api->_response("Session Parameter required", HTTPStatusCode::$UNAUTHORIZED);
        }
        $sessionId = Crypt::decryptAES(Session::$cookie_encryption_key, $cookie[Session::$cookie_key_session]);
        if($sessionId == Null){
            return $api->_response("Session not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $findSQL = "select * from ".Session::$database_tableName." where ".Entity::$database_tableColumn_id."='".$sessionId."'";
        $res = $conn->query($findSQL);
        if(!$res||$res->num_rows<1){
            return $api->_response("Session not found", HTTPStatusCode::$AUTHENTICATION_TIMEOUT);
        }
        $session = self::fromSQL(mysqli_fetch_row($res));
        if($session->isExpired()){
            return $api->_response("Session Expired", HTTPStatusCode::$AUTHENTICATION_TIMEOUT);
        }
        $findUserSQL = "select * from ".User::$database_tableName." where ".User::$database_tableColumn_id."='".$session->getUserId()."'";
        $resUser = $conn->query($findUserSQL);
        if(!$resUser||$resUser->num_rows<1){
            return $api->_response("User not found", HTTPStatusCode::$NOT_FOUND);
        }
        $user = User::fromSQL(mysqli_fetch_row($res));
        return $user;
    }


    /**
     * @param AbstractAPI $api
     * @param mysqli $conn
     * @param array $cookie
     * @return mixed
     */
    public static function toSession($api, $conn, $cookie){
        if(!AbstractAPI::checkParams($cookie, Session::$cookie_key_session)){
            return $api->_response("Session Parameter required", HTTPStatusCode::$UNAUTHORIZED);
        }
        $sessionId = Crypt::decryptAES(Session::$cookie_encryption_key, $cookie[Session::$cookie_key_session]);
        if($sessionId == Null){
            return $api->_response("Session not found", HTTPStatusCode::$UNAUTHORIZED);
        }
        $findSQL = "select * from ".Session::$database_tableName." where ".Entity::$database_tableColumn_id."='".$sessionId."'";
        $res = $conn->query($findSQL);
        if(!$res||$res->num_rows<1){
            return $api->_response("Session not found", HTTPStatusCode::$AUTHENTICATION_TIMEOUT);
        }
        $session = self::fromSQL(mysqli_fetch_row($res));
        if($session->getType()== "refresh"){
            return $api->_response("Session Rejected", HTTPStatusCode::$UNAUTHORIZED);
        }
        return $session;
    }

    /**
     * @param AbstractAPI $api
     * @param mysqli $conn
     * @param User $user
     * @throws Exception
     * @return string
     */
    public function toAccessToken($api, $conn, $user){
        $now = Time::now();
        $expiresAt = $now + 3600000;
        $this->setType("access")->setUserId($user->getId())->setExpiresAt($expiresAt)->setCreatedAt($now)->setUpdatedAt($now);
        $sql = "insert into ".Session::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Session::$database_tableColumn_userId.", "
            .Session::$database_tableColumn_type.", "
            .Session::$database_tableColumn_expiresAt.","
            .Session::$database_tableColumn_createdAt.","
            .Session::$database_tableColumn_updatedAt.") values ("
            ."'".$this->getId()."', "
            ."'".$this->getUserId()."', "
            ."'".$this->getType()."', "
            ."'".$this->getExpiresAt()."', "
            ."'".$this->getCreatedAt()."', "
            ."'".$this->getUpdatedAt()."'"
            .")";
        if(!$conn->query($sql)){
            return $api->_response("Service unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        $crypt = Crypt::encryptAES(Session::$cookie_encryption_key, $this->getId());
        if($crypt == Null){
            return $api->_response("Failed to encrypt data", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        return $crypt;
    }

    /**
     * @param AbstractAPI $api
     * @param mysqli $conn
     * @param User $user
     * @throws Exception
     * @return string
     */
    public function toRefreshToken($api, $conn, $user){
        $now = Time::now();
        $this->setType("refresh")->setUserId($user->getId())->setExpiresAt(-1)->setCreatedAt($now)->setUpdatedAt($now);
        $sql = "insert into ".Session::$database_tableName." ("
            .Entity::$database_tableColumn_id.", "
            .Session::$database_tableColumn_userId.", "
            .Session::$database_tableColumn_type.", "
            .Session::$database_tableColumn_expiresAt.","
            .Session::$database_tableColumn_createdAt.","
            .Session::$database_tableColumn_updatedAt.") values ("
            ."'".$this->getId()."', "
            ."'".$this->getUserId()."', "
            ."'".$this->getType()."', "
            ."'".$this->getExpiresAt()."', "
            ."'".$this->getCreatedAt()."', "
            ."'".$this->getUpdatedAt()."'"
            .")";
        if(!$conn->query($sql)){
            return $api->_response("Service unavailable", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        $crypt = Crypt::encryptAES(Session::$cookie_encryption_key, $this->getId());
        if($crypt == Null){
            return $api->_response("Failed to encrypt data", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        return $crypt;
    }

    /**
     * @param array $row
     * @return Session
     */
    public static function fromSQL($row){
        $session = new Session(false);
        $session->setId($row[0]);
        $session->setUserId($row[1]);
        $session->setType($row[2]);
        $session->setExpiresAt($row[3]);
        $session->setCreatedAt($row[4]);
        $session->setUpdatedAt($row[5]);
        return $session;
    }

}