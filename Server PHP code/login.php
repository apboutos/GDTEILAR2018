<?php

class Result {
		
	public $authenticationResult = false;
	
}

$_username = $_GET['username']; 
$_password = $_GET['password'];

$servername = "localhost:3306";
$username = "exoph_master";
$password = "ma582468";
$dbname = "exophren804440_Grinia";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    echo("Connection failed: " . $conn->connect_error);
}
else
{
	//echo "connected";
}

$sql = "SELECT * FROM `user` WHERE `username` = '$_username'";
$result = $conn->query($sql);
$aResult = new Result;

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        if ($row["username"] == $_username){
			//echo "username found";
			if ($row["uPassword"] == $_password){
				$aResult -> authenticationResult = true;
			}
		}
    }
} 

echo json_encode($aResult);
$conn->close();
?>