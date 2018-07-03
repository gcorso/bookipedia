<?php
$response = array();
require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();
if (isset($_GET["bookid"])) {
    $bookid = $_GET['bookid'];
 
    $result = mysql_query("SELECT bookid, name, authorid, authorname, year, description, genre, language, rating, reviews FROM book WHERE bookid = $bookid");
 
    if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $book = array();
            $book["bookid"] = $result["bookid"];
            $book["name"] = $result["name"];
            $book["authorid"] = $result["authorid"];
            $book["authorname"] = $result["authorname"];
            $book["year"] = $result["year"];
            $book["description"] = $result["description"];
            $book["genre"] = $result["genre"];
            $book["language"] = $result["language"];
            $book["rating"] = $result["rating"];
            $book["reviews"] = $result["reviews"];
			
            $response["success"] = 1;
            $response["book"] = array();
 
            array_push($response["book"], $book);
 
            echo json_encode($response);
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