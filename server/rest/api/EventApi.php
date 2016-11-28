<?php

require_once 'Api.php';
require_once 'AbstractApi.php';
require_once '../model/User.php';
require_once '../model/Token.php';
require_once '../util/HTTPStatusCode.php';

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 5:23 PM
 */
class EventApi extends AbstractApi
{

    public function switchOff($args)
    {
        if (!$this->method == "PUT") {
            return $this->_response("only PUT requests supported", HTTPStatusCode::$METHOD_NOT_ALLOWED);
        }
        if (!self::checkParams($args, "id")) {
            return $this->_response("required parameter not found", HTTPStatusCode::$BAD_REQUEST);
        }
        $smartPlugId = $args["id"];
    }



}
