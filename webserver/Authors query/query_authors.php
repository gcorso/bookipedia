<?php
$response = array();
require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if (isset($_GET["where"])) {
    $where = $_GET['where'];
	
	$result = mysql_query("SELECT authorid, name FROM author $where") or die(mysql_error());
	
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		$response["authors"] = array();
	 
		while ($row = mysql_fetch_array($result)) {
			$author = array();
			$author["authorid"] = $row["authorid"];
			$author["name"] = $row["name"];
	 
			array_push($response["authors"], $author);
		}
		$response["success"] = 1;
	 
		echo json_encode($response);
	} else {
		// no authors found
		$response["success"] = 0;
		$response["message"] = "No authors found";
	 
		echo json_encode($response);
	}
	
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>