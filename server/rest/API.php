<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 11:13 AM
 */

require_once 'AbstractAPI.php';

class API extends AbstractAPI
{
    /**
     * @var Device
     */
    protected $device = Null;

    public function __construct($request, $origin) {
        parent::__construct($request);
        echo ("Request type is ".$this->method);
        echo ("Verb is ".$this->verb);
        echo ("Endpoint is ".$this->endpoint);
        $this->device = $this->_authenticate($request);
    }

    /**
     * @param $request
     * @return Device
     * @throws Exception
     */
    protected function _authenticate($request){
        $this->_response('Unauthorized device', 401);
    }


    //____________________________________________________

    /**
     * @param Device $device
     * @throws Exception
     * @return Device
     */
    public function _createDevice($device){
        $findSQL = "select * from ".Device::$database_tableName." where ".Device::$database_tableColumn_id." ='".$device->getId()."'";
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", 503);
            throw new Exception("Internal Error");
        }
        if ($res->num_rows > 0){
            $this->_response("Conflict: user", 409);
            throw new Exception("Conflict");
        }
        $pass = password_hash($device->getDevicePassKey(), PASSWORD_BCRYPT);
        $device->setDevicePassKey($pass);
        $insertSQL = Device::wrapToInsertSQL($device);
        if(!$this->_getDatabase()->query($insertSQL)){
            $this->_response("Failed", 500);
            throw new Exception("Failed to create device");
        }
        return $device->setDevicePassKey(Null);
    }

    /**
     * @param Device $device
     * @throws Exception
     * @return Device
     */
    public function _loginDevice($device){
        $findSQL = "select * from ".Device::$database_tableName." where ".Device::$database_tableColumn_id." ='".$device->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", 503);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", 404);
            throw new Exception("No user found");
        }
        $row = mysqli_fetch_row($res);
        $pass = $row[2];
        if(!password_verify($device->getDevicePassKey(), $pass)){
            $this->_response("Conflict", 409);
            throw new Exception("Password conflict");
        }
        return Device::fromSQL($row)->setDevicePassKey(Null);
    }

    //____________________________________________________

    /**
     * @param Outlet $outlet
     * @throws Exception
     * @return Outlet
     */
    public function _createOutlet($outlet){
        $this->_checkAuthenticated();
        $findSQL = "select * from ".Outlet::$database_tableName." where ".Outlet::$database_tableColumn_id." ='".$outlet->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", 503);
            throw new Exception("Internal Error");
        }
        if ($res->num_rows > 0){
            $this->_response("Conflict: outlet", 409);
            throw new Exception("Conflict");
        }
        $outlet->setDeviceId($this->device->getId());
        $insertSQL = Outlet::wrapToInsertSQL($outlet);
        if(!$this->_getDatabase()->query($insertSQL)){
            $this->_response("Failed", 500);
            throw new Exception("Failed to create device");
        }
        return $outlet;
    }

    /**
     * @param Outlet $outlet
     * @throws Exception
     */
    public function _deleteOutlet($outlet){
        $this->_checkAuthenticated();
        $findSQL = "select * from ".Outlet::$database_tableName." where ".Outlet::$database_tableColumn_id." ='".$outlet->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", 503);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", 404);
            throw new Exception("No outlet found");
        }
        $outlet = Outlet::fromSQL(mysqli_fetch_row($res));
        if($outlet->getDeviceId() != $this->device->getId()){
            $this->_response("Forbidden", 403);
            throw new Exception("Forbidden");
        }
        $deleteSQL = "delete from ".Outlet::$database_tableName." where ".Outlet::$database_tableColumn_deviceId
            ."='".$outlet->getDeviceId()."'";
        if(!$this->_getDatabase()->query($deleteSQL)){
            $this->_response("Failed", 500);
            throw new Exception("Failed to create device");
        }
    }

    //____________________________________________________

    /**
     * @param Event $event
     * @return Event
     */
    public function _createEvent($event){
        $this->_checkAuthenticated();

    }

    /**
     * @throws Exception
     */
    private function _checkAuthenticated(){
        if($this->device == Null){
            $this->_response("Unauthorized", 401);
            throw new Exception("unauthorized");
        }
    }

}