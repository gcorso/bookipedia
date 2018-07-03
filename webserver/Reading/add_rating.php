<?php
$response = array();
if (isset($_POST['bookid']) && isset($_POST['userid']) && isset($_POST['rating'])) {
 
    $bookid = $_POST['bookid'];
    $userid = $_POST['userid'];
    $rating = $_POST['rating'];
 
    require_once __DIR__ . '/db_connect.php';
 
    $db = new DB_CONNECT();
	
	$result = mysql_query("SELECT rating FROM reading WHERE bookid = $bookid AND userid = $userid");
	if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
			$ratingorig = $result["rating"];
			
			$result = mysql_query("SELECT rating, reviews FROM book WHERE bookid = $bookid");
			
			if (!empty($result)) {
				if (mysql_num_rows($result) > 0) {
					
					$result = mysql_fetch_array($result);
					$ratingall = $result["rating"];
					$reviewsall = $result["reviews"];
					
					$response["original"] = "rating = $ratingall, reviews = $reviewsall, ratingorig = $ratingorig, rating = $rating " ;
					
					if ($ratingorig < 0){
						$ratingall = ($ratingall * $reviewsall + $rating)/($reviewsall + 1);
						$reviewsall = $reviewsall + 1;
						$response["type"] = "incrementato";
					} else {
						$ratingall = ($ratingall * $reviewsall + $rating - $ratingorig)/$reviewsall;
					}
					
					$response["final"] = "rating = $ratingall, reviews = $reviewsall" ;
					
					
					$result = mysql_query("UPDATE book SET rating = $ratingall, reviews = $reviewsall WHERE bookid = $bookid");
					$result2 = mysql_query("UPDATE reading SET rating = $rating WHERE bookid = $bookid AND userid = $userid");
					
					// check if row inserted or not
					if ($result && $result2) {
						$response["success"] = 1;
						$response["message"] = "Rated successfully.";
				 
						echo json_encode($response);
					} else {
						if ($result == false){
							$response["message"] = "Rating not success due to book table rating = $ratingall, reviews = $reviewsall" ;
						} else {
							$response["message"] = "Rating not success due to reading table";
						}
						$response["success"] = 0;
						
						echo json_encode($response);
					}
				}
			}
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    echo json_encode($response);
}
?>