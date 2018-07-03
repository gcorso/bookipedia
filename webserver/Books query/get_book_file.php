<?php
$response = array();
require_once __DIR__ . '/db_connect.php';
 
$db = new DB_CONNECT();
if (isset($_GET["bookid"])) {
	$bookid = $_GET['bookid'];
 
	$result = mysql_query("SELECT file FROM book WHERE bookid = $bookid");

	if (!empty($result)) {
		if (mysql_num_rows($result) > 0) {

			$result = mysql_fetch_array($result);

			$book = array();
			$book["file"] = $result["file"];
			
			$response["success"] = 1;
			$response["book"] = array();

			array_push($response["book"], $book);

			echo json_encode($response);
			$result = mysql_query("UPDATE book SET downloads = downloads + 1 WHERE bookid = $bookid");
		} else {
			// no book found
			$response["success"] = 0;
			$response["message"] = "No book found";

			echo json_encode($response);
		}
	} else {
		// no book found
		$response["success"] = 0;
		$response["message"] = "No book found";

		echo json_encode($response);
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>