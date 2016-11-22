<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 5:23 PM
 */

require_once 'Api.php';
require_once 'AbstractApi.php';
require_once '../model/User.php';
require_once '../model/Token.php';
require_once '../model/SmartPlug.php';
require_once '../util/HTTPStatusCode.php';
require_once '../util/Time.php';

class SmartPlugApi extends AbstractApi
{

    /**
     * @param string $smartPlugId
     * @return string
     */
    public function create($smartPlugId){
        if(!$this->method == "POST"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if ($res->num_rows > 0){
            return $this->_response("Conflict: smartPlug", HTTPStatusCode::$CONFLICT);
        }
        $command = $smartPlugId."_SAY_STATE";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            //$CMD = $arr["CMD"];
            //$VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            }
        }
        $smartPlug = new SmartPlug();
        $smartPlug->setUserId($user->getId())->setState("OFF");
        $now = Time::now();
        $smartPlug->setCreatedAt($now)->setUpdatedAt($now);
        $insertSQL = SmartPlug::wrapToInsertSQL($smartPlug);
        if(!$this->_getDatabase()->query($insertSQL)){
            return $this->_response("Failed", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        $arr = Array();
        $arr["data"] = $smartPlug;
        return $this->_response(json_encode($arr), HTTPStatusCode::$OK);
    }

    /**
     * @param string $smartPlugId
     * @return string
     */
    public function delete($smartPlugId){
        if(!$this->method == "DELETE"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if($res->num_rows<1){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            return $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
        }
        $deleteSQL = "delete from ".SmartPlug::$database_tableName." where ".Entity::$database_tableColumn_id
            ."='".$smartPlug->getId()."'";
        if(!$this->_getDatabase()->query($deleteSQL)){
            return $this->_response("Failed", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        $arr = Array();
        $arr["data"] = true;
        return $this->_response(json_encode($arr), HTTPStatusCode::$OK);
    }

    /**
     * @param $smartPlugId
     * @return string
     */
    public function on($smartPlugId){
        if(!$this->method == "PUT"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if($res->num_rows<1){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            return $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
        }
        $command = $smartPlug->getId()."_TO_ON";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
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
                        return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                    }
                    if($update->num_rows<1){
                        return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    }
                    break;
                default:
                    return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return $this->_response(json_encode($arr), HTTPStatusCode::$OK);
        }
    }

    /**
     * @param $smartPlugId
     * @return string
     */
    public function Off($smartPlugId){
        if(!$this->method == "PUT"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if($res->num_rows<1){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            return $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
        }
        $command = $smartPlug->getId()."_TO_OFF";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
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
                        return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                    }
                    if($update->num_rows<1){
                        return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    }
                    break;
                default:
                    return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return $this->_response(json_encode($arr), HTTPStatusCode::$OK);
        }
    }

    /**
     * @param string $smartPlugId
     * @return string
     */
    public function id($smartPlugId){
        if(!$this->method == "GET"){
            return $this->_response("Only GET requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        $user = Token::getUserFromRequest($this, $this->_getDatabase());
        if(!$user instanceof User){
            return $user;
        }
        $findSQL = "select * from ".SmartPlug::$database_tableName." where ".SmartPlug::$database_tableColumn_id." ='".$smartPlugId->getId()."'";
        /** @var mysqli_result $res */
        $res = $this->_getDatabase()->query($findSQL);
        if(!$res){
            return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
        }
        if($res->num_rows<1){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }
        $smartPlug = SmartPlug::fromSQL(mysqli_fetch_row($res));
        if($smartPlug->getUserId() != $user->getId()){
            return $this->_response("Forbidden", HTTPStatusCode::$FORBIDDEN);
        }
        $command = $smartPlug->getId()."_SAY_STATE";
        $result = exec("python ../../rf/send.py ".$command);
        if($result == "ERROR"){
            return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
        }else{
            $arr = $this->_responseToArr($result);
            $UUID = $arr["UUID"];
            $CMD = $arr["CMD"];
            $VAL = $arr["VAL"];
            if($UUID != $smartPlugId){
                return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
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
                        return $this->_response("Internal error", HTTPStatusCode::$SERVICE_UNAVAILABLE);
                    }
                    if($update->num_rows<1){
                        return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
                    }
                    break;
                default:
                    return $this->_response("Not found", HTTPStatusCode::$NOT_FOUND);
            }
            $arr = Array();
            $arr["data"] = $smartPlug;
            return $this->_response(json_encode($arr), HTTPStatusCode::$OK);
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
