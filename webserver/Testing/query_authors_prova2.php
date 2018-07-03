<?php
 
/*
 * 
 */
error_reporting(E_ALL);
ini_set('display_errors', TRUE);
ini_set('display_startup_errors', TRUE);
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
    // get a author from authors table
    $result = mysql_query("SELECT authorid, name, biography FROM author WHERE authorid = 3");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			// success
            $response["success"] = 1;
			
            $result = mysql_fetch_array($result);
			
            $author = array();
            $author["authorid"] = $result["authorid"];
            $author["name"] = $result["name"];
            $author["biography"] = $result["biography"];
			$author["biography"] = "casa";
 
            // user node
            $response["author"] = array();
 
            array_push($response["author"], $author);

			/*$response["author"] = [
				"authorid"  => $result["authorid"],
				"name"      => $result["name"],
				"biography" => $result["biography"]
			];*/
			
			var_dump($response);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no author found
            $response["success"] = 0;
            $response["message"] = "No author found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no author found
        $response["success"] = 0;
        $response["message"] = "No author found";
 
        // echo no users JSON
        echo json_encode($response);
    }
 
?>