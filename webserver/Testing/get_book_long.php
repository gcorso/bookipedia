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
 

 
// get a book from books table
$result = mysql_query("SELECT SUBSTRING(file, 1, 8500) AS file FROM book WHERE bookid = 3");

if (!empty($result)) {
	// check for empty result
	if (mysql_num_rows($result) > 0) {

		$result = mysql_fetch_array($result);

		$book = array();
		/*$book["bookid"] = $result["bookid"];
		$book["name"] = $result["name"];
		$book["authorid"] = $result["authorid"];
		$book["authorname"] = $result["authorname"];
		$book["year"] = $result["year"];
		$book["description"] = $result["description"];*/
		$book["file"] = $result["file"];
		/*$book["genre"] = $result["genre"];
		$book["language"] = $result["language"];
		$book["rating"] = $result["rating"];
		$book["reviews"] = $result["reviews"];*/
		
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
?>