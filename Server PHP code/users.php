<?php

$username = $_GET['username'];

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

$sql = "SELECT * FROM `user`;";
$result = $conn->query($sql);
echo "hi";

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "username = " . $row['username']. "<br>";
		echo "password = " . $row['uPassword']. "<br>";
		echo "e-mail = " . $row['uEmail']. "<br>";
		echo "firstName = " . $row['uFirstname']. "<br>";
		echo "middleName = " . $row['uMiddlename']. "<br>";
		echo "lastName = " . $row['uLastname']. "<br>";
		echo "address = " . $row['uAddress']. "<br>";
		echo "postalCode = " . $row['uPostalCode']. "<br>";
		echo "creditCard = " . $row['uCCnumber']. "<br>";
		echo "expirationDate = " . $row['uCCexpdate']. "<br>";
		echo "CVS = " . $row['uCCsecurecode']. "<br><br><br><br>";
    }
} 
$conn->close();
?>