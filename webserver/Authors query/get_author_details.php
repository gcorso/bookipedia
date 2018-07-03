<?php
/*
 * Following code will get single author details
 * A author is identified by author id (authorId)
 */
$response = array();
require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();
 
if (isset($_GET["authorid"])) {
    $authorid = $_GET['authorid'];
 
    $result = mysql_query("SELECT authorid, name, biography FROM author WHERE authorid = $authorid");
 
    if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $author = array();
            $author["authorid"] = $result["authorid"];
            $author["name"] = $result["name"];
            $author["biography"] = $result["biography"];
			
            $response["success"] = 1;
            $response["author"] = array();
 
            array_push($response["author"], $author);
 
            echo json_encode($response);
        } else {
            // no author found
            $response["success"] = 0;
            $response["message"] = "No author found";
 
            echo json_encode($response);
        }
    } else {
        // no author found
        $response["success"] = 0;
        $response["message"] = "No author found";
 
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>