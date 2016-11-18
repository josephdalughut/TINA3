<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 5:23 PM
 */

require_once 'API.php';
require_once 'AbstractAPI.php';
require_once '../model/User.php';
require_once '../model/Session.php';
require_once '../model/SmartPlug.php';
require_once '../util/HTTPStatusCode.php';
require_once '../util/Time.php';

class SmartPlugAPI extends AbstractAPI
{

    /**
     * @param string $smartPlugId
     * @throws Exception
     * @return string
     */
    public function create($smartPlugId){
        if(!$this->method == "POST"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Session::toUser($this, $this->_getDatabase(), $_COOKIE);
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Internal Error");
        }
        if ($res->num_rows > 0){
            $this->_response("Conflict: smartPlug", HTTPStatusCode::$CONFLICT);
            throw new Exception("Conflict");
        }
        $command = $smartPlugId."_SAY_STATE";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("Failed to create smartPlug: Not found");
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            //$CMD = $arr["CMD"];
            //$VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
        }
        $smartPlug = new SmartPlug();
        $smartPlug->setUserId($user->getId())->setState("OFF");
        $now = Time::now();
        $smartPlug->setCreatedAt($now)->setUpdatedAt($now);
        $insertSQL = SmartPlug::wrapToInsertSQL($smartPlug);
        if(!$this->_getDatabase()->query($insertSQL)){
            $this->_response("Failed", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Failed to create smartPlug");
        }
        $arr = Array();
        $arr["data"] = $smartPlug;
        return json_encode($arr);
    }

    /**
     * @param string $smartPlugId
     * @throws Exception
     * @return string
     */
    public function delete($smartPlugId){
        if(!$this->method == "DELETE"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Session::toUser($this, $this->_getDatabase(), $_COOKIE);
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("No smartPlug found");
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
            throw new Exception("Forbidden");
        }
        $deleteSQL = "delete from ".SmartPlug::$database_tableName." where ".Entity::$database_tableColumn_id
            ."='".$smartPlug->getId()."'";
        if(!$this->_getDatabase()->query($deleteSQL)){
            $this->_response("Failed", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Failed to delete smart plug");
        }
        $arr = Array();
        $arr["data"] = true;
        return json_encode($arr);
    }

    /**
     * @param $smartPlugId
     * @throws Exception
     * @return string
     */
    public function on($smartPlugId){
        if(!$this->method == "PUT"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Session::toUser($this, $this->_getDatabase(), $_COOKIE);
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("No smartPlug found");
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
            throw new Exception("Forbidden");
        }
        $command = $smartPlug->getId()."_TO_ON";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("Failed to create smartPlug: Not found");
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            switch ($VAL){
                case "ON":
                case "OFF":
                    $smartPlug->setState($VAL);
                    $update = $this->_getDatabase()->query(
                        "update ".SmartPlug::$database_tableName." set ".SmartPlug::$database_tableColumn_state."=".$VAL
                        ." where ".SmartPlug::$database_tableColumn_id."='".$smartPlug->getId()."'"
                    );
                    if(!$update){
                        $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                        throw new Exception("Internal Error");
                    }
                    if($update->num_rows<1){
                        $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                        throw new Exception("No smartPlug found");
                    }
                    break;
                default:
                    $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return json_encode($arr);
        }
    }

    /**
     * @param $smartPlugId
     * @throws Exception
     * @return string
     */
    public function Off($smartPlugId){
        if(!$this->method == "PUT"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Session::toUser($this, $this->_getDatabase(), $_COOKIE);
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("No smartPlug found");
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
            throw new Exception("Forbidden");
        }
        $command = $smartPlug->getId()."_TO_OFF";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("Failed to create smartPlug: Not found");
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            switch ($VAL){
                case "ON":
                case "OFF":
                    $smartPlug->setState($VAL);
                    $update = $this->_getDatabase()->query(
                        "update ".SmartPlug::$database_tableName." set ".SmartPlug::$database_tableColumn_state."=".$VAL
                        ." where ".SmartPlug::$database_tableColumn_id."='".$smartPlug->getId()."'"
                    );
                    if(!$update){
                        $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                        throw new Exception("Internal Error");
                    }
                    if($update->num_rows<1){
                        $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                        throw new Exception("No smartPlug found");
                    }
                    break;
                default:
                    $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return json_encode($arr);
        }
    }

    /**
     * @param string $smartPlugId
     * @throws Exception
     * @return string
     */
    public function id($smartPlugId){
        if(!$this->method == "GET"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Session::toUser($this, $this->_getDatabase(), $_COOKIE);
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
            throw new Exception("Internal Error");
        }
        if($res->num_rows<1){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("No smartPlug found");
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
            throw new Exception("Forbidden");
        }
        $command = $smartPlug->getId()."_SAY_STATE";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            throw new Exception("Failed to create smartPlug: Not found");
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            switch ($VAL){
                case "ON":
                case "OFF":
                    $smartPlug->setState($VAL);
                    $update = $this->_getDatabase()->query(
                        "update ".SmartPlug::$database_tableName." set ".SmartPlug::$database_tableColumn_state."=".$VAL
                        ." where ".SmartPlug::$database_tableColumn_id."='".$smartPlug->getId()."'"
                    );
                    if(!$update){
                        $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                        throw new Exception("Internal Error");
                    }
                    if($update->num_rows<1){
                        $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                        throw new Exception("No smartPlug found");
                    }
                    break;
                default:
                    $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    throw new Exception("Failed to create smartPlug: UUID Mismatch");
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return json_encode($arr);
        }
    }

    /**
     * @param string $result
     * @return array
     */
    public function _responseToArr($result){
        $uuid_idx = strpos($result, "_");
        $cmd_idx = strpos($result, "_", $uuid_idx + 1);
        $val_idx = strpos($result, "_", $cmd_idx + 1);
        $uuid = substr($result, 0, $uuid_idx);
        $cmd = substr($result, $uuid_idx + 1, $cmd_idx);
        $val = substr($result, $cmd_idx + 1, $val_idx);
        return array (
          "UUID" => $uuid, "CMD" => $cmd, "VAL" => $val
        );
    }
}
