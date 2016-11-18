<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/13/16
 * Time: 4:18 PM
 */
class Time
{

    /**
     * @return float
     */
    public static function now(){
        return round(microtime(true) * 1000);
    }


}