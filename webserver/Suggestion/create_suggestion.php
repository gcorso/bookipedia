<?php
$response = array();
 
// check for required fields
if (isset($_POST['userid']) && isset($_POST['email']) && isset($_POST['book']) && isset($_POST['author'])) {
 
    $userid = $_POST['userid'];
    $email = $_POST['email'];
	$book = $_POST['book'];
    $author = $_POST['author'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO suggestion(userid, email, book, author) VALUES($userid, '$email', '$book', '$author')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Reading successfully created.";
 
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>