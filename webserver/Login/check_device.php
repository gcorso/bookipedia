<?php
$response = array();

// check for required fields
if (isset($_GET['userid']) && isset($_GET['lastdevice'])) {
 
    $userid = $_GET['userid'];
	$lastdevice = $_GET['lastdevice'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("SELECT lastdevice FROM user WHERE userid = $userid");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
			
			if ($result["lastdevice"]!=$lastdevice){
				
				$response["success"] = 1;
				$response["sync"] = 1;
				
				$userid = $user["userid"];
				$result = mysql_query("UPDATE user SET lastdevice = $lastdevice WHERE  userid = $userid");
				
				echo json_encode($response);
			} else {

				$response["success"] = 1;
				$response["sync"] = 0;
	 
				echo json_encode($response);
			}
            
        } else {
            // no user found
            $response["success"] = 0;
            $response["message"] = "No user found";
 
            echo json_encode($response);
        }
    } else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "No user found";
 
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>