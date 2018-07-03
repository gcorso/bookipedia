<?php
$response = array();
if (isset($_GET['email']) && isset($_GET['password'])&& isset($_GET['lastdevice'])) {
 
    $email = $_GET['email'];
    $password = $_GET['password'];
	$lastdevice = $_GET['lastdevice'];
 
    require_once __DIR__ . '/db_connect.php';
    $db = new DB_CONNECT();
 
    $result = mysql_query("SELECT userid, password_hash, language, favgenres FROM user WHERE email = '$email'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
			
			if (password_verify( $password, $result["password_hash"] )){
				
				$user = array();
				$user["userid"] = $result["userid"];
				$user["language"] = $result["language"];
				$user["favgenres"] = $result["favgenres"];
				
				$response["success"] = 1;
				$response["user"] = array();
	 
				array_push($response["user"], $user);
				
				$userid = $user["userid"];
				$result = mysql_query("UPDATE user SET lastdevice = $lastdevice WHERE  userid = $userid");
				
				if ($result){
					$response["message"] = "Last device set";
				} else {
					$response["message"] = "Last device NOT set";
				}
	 
				echo json_encode($response);
			} else {
				$response["success"] = 0;
				$response["message"] = "Password incorrect";
	 
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