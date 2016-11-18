<?php
/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 10/12/16
 * Time: 6:38 PM
 */


$conn = new mysqli(DatabaseConstants::$database_serverName, DatabaseConstants::$database_userName, DatabaseConstants::$database_password);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//create database tables
$conn->query(User::_getDatabaseTableCreateStatement());
$conn->query(Event::_getDatabaseTableCreateStatement());
$conn->query(SmartPlug::_getDatabaseTableCreateStatement());


echo "Connected successfully";
