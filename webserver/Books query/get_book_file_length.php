<?php
 
/*
 * Following code will get single book details
 * A book is identified by book id (BookId)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["bookid"])) {
	$bookid = $_GET['bookid'];
 
	// get a book from books table
	$result = mysql_query("SELECT LENGTH(file) AS length FROM book WHERE bookid = $bookid");

	if (!empty($result)) {
		// check for empty result
		if (mysql_num_rows($result) > 0) {

			$result = mysql_fetch_array($result);

			$book = array();
			$book["length"] = $result["length"];
			
			// success
			$response["success"] = 1;

			// user node
			$response["book"] = array();

			array_push($response["book"], $book);

			// echoing JSON response
			echo json_encode($response);
		} else {
			// no book found
			$response["success"] = 0;
			$response["message"] = "No book found";

			// echo no users JSON
			echo json_encode($response);
		}
	} else {
		// no book found
		$response["success"] = 0;
		$response["message"] = "No book found";

		// echo no users JSON
		echo json_encode($response);
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>