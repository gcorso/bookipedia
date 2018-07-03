<?php
$response = array();
 
// check for required fields
if (isset($_GET['bookid']) && isset($_GET['userid'])) {
 
    $bookid = $_GET['bookid'];
    $userid = $_GET['userid'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	
	$result = mysql_query("SELECT progress, notes FROM reading WHERE bookid = $bookid AND userid = $userid");
	
	if ((empty($result))||(mysql_num_rows($result) < 1)) {
		// no reading already present, therefore create it
		
		// mysql inserting a new row
		$result = mysql_query("INSERT INTO reading(bookid, userid) VALUES('$bookid', '$userid')");
	 
		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Reading successfully created.";
			
			$book = array();
			$book["progress"] = 0;
			$book["notes"] = "";
			$response["book"] = array();
			array_push($response["book"], $book);
	 
			echo json_encode($response);
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred. From insertion.";
	 
			echo json_encode($response);
		}
		
	} else {
		//reading already present, therefore activate it 
		$result = mysql_fetch_array($result);

		$book = array();
		$book["progress"] = $result["progress"];
		if($book["progress"]==null){
			$book["progress"]=0;
		}
		$book["notes"] = $result["notes"];
		if($book["notes"]==null){
			$book["notes"]="";
		}
		$response["book"] = array();
		array_push($response["book"], $book);
		
		// mysql inserting a new row
		$result = mysql_query("UPDATE reading SET active = 1 WHERE bookid = $bookid AND userid = $userid");
	 
		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Reading successfully updated.";
	 
			echo json_encode($response);
		} else {
			// failed to insert row
			$response["success"] = 0;
			$response["message"] = "Oops! An error occurred. No results from active set.";
	 
			echo json_encode($response);
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>