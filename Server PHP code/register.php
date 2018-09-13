<?php
class Result {
		
	public $registrationResult = false;
	public $usernameError = false;
	public $emailError = false;	
}

$_username = $_GET['username']; 
$_password = $_GET['password']; 
$_email    = $_GET['email'];


$servername = "localhost:3306";
$username = "exoph_master";
$password = "ma582468";
$dbname = "exophren804440_Grinia";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
mysqli_set_charset($conn, "utf8");

// Check connection
if ($conn->connect_error) {
    //echo("Connection failed: " . $conn->connect_error);
}
else
{
	//echo "connected";
}

$sql = "INSERT INTO user (username,uPassword,uEmail) VALUES ('$_username','$_password','$_email');";
//echo $sql;
$aResult = new Result;

if ($conn->query($sql) === TRUE) {
	//echo "Entry done";
    $aResult->registrationResult = true;
	$aResult->usernameError = false;
	$aResult->emailError = false;
}
else if (strpos($conn->error, 'PRIMARY') !== false) {
    //echo "Error : PRIMARY";
	$aResult->registrationResult = false;
	$aResult->usernameError = true;
	$aResult->emailError = false;
	//echo $aResult;
} 
else if (strpos($conn->error, 'email_UNIQUE') !== false) {
	//echo "Error : email_UNIQUE"; 
	$aResult->registrationResult = false;
	$aResult->usernameError = false;
	$aResult->emailError = true;
}

echo json_encode($aResult);
// Error 1      ->       Duplicate entry 'exophrenik' for key 'PRIMARY'
// Error 2      ->       Duplicate entry 'exophrenik@gmail.com' for key 'email_UNIQUE'

$conn->close();
?>