<?php
$response = array();
require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();

if (isset($_GET["where"])) {
    $where = $_GET['where'];
	
	$result = mysql_query("SELECT bookid, name, authorname FROM book $where") or die(mysql_error());
	
	if (mysql_num_rows($result) > 0) {
		$response["books"] = array();
	 
		while ($row = mysql_fetch_array($result)) {
			$book = array();
			$book["bookid"] = $row["bookid"];
			$book["name"] = $row["name"];
			$book["authorname"] = $row["authorname"];
	 
			array_push($response["books"], $book);
		}
		$response["success"] = 1;
	 
		echo json_encode($response);
	} else {
		// no books found
		$response["success"] = 0;
		$response["message"] = "No books found";
	 
		echo json_encode($response);
	}
	
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>