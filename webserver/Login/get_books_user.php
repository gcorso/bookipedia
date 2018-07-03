<?php
$response = array();
require_once __DIR__ . '/db_connect.php';
$db = new DB_CONNECT();
 
if (isset($_GET["userid"])) {
    $userid = $_GET['userid'];
 
    // get a book from books table
    $result = mysql_query("SELECT book.bookid, name, authorid, authorname, year, description, file, genre, language, book.rating, reviews, progress, notes FROM book, reading WHERE userid = $userid AND book.bookid = reading.bookid AND active = 1");
	
	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// looping through all results
		$response["books"] = array();
	 
		while ($row = mysql_fetch_array($result)) {
			$book = array();
            $book["bookid"] = $row["bookid"];
            $book["name"] = $row["name"];
            $book["authorid"] = $row["authorid"];
            $book["authorname"] = $row["authorname"];
            $book["year"] = $row["year"];
            $book["description"] = $row["description"];
			$book["file"] = $row["file"];
            $book["genre"] = $row["genre"];
            $book["language"] = $row["language"];
            $book["rating"] = $row["rating"];
            $book["reviews"] = $row["reviews"];
			$book["progress"] = $row["progress"];
			$book["notes"] = $row["notes"];
	 
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