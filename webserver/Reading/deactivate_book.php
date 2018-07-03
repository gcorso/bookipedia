<?php
$response = array();
 
// check for required fields
if (isset($_POST['bookid']) && isset($_POST['userid'])) {
 
    $bookid = $_POST['bookid'];
    $userid = $_POST['userid'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched bookid and userid
    $result = mysql_query("UPDATE reading SET active = 2 WHERE bookid = $bookid AND userid = $userid");
 
    // check if row inserted or not
    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Reading successfully updated.";
 
        echo json_encode($response);
    } else {
		// required field is missing
		$response["success"] = 0;
		$response["message"] = "Problem in the connection with the database";
	 
		echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>