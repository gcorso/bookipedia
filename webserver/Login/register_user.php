<?php
$response = array();
 
if (isset($_POST['email']) && isset($_POST['password']) && isset($_POST['language']) && isset($_POST['favgenres'])) {
 
    $email = $_POST['email'];
    $password = $_POST['password'];
	$language = $_POST['language'];
    $favgenres = $_POST['favgenres'];
	$password_hash = password_hash($password, PASSWORD_BCRYPT);
 
    require_once __DIR__ . '/db_connect.php';
    $db = new DB_CONNECT();
 
    $result = mysql_query("INSERT INTO user(email, password_hash, language, favgenres) VALUES('$email', '$password_hash', '$language', '$favgenres')");
 
    // check if row inserted or not
    if ($result) {
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";

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