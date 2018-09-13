<?php

$_username = $_GET['username'];

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

$sql = "SELECT * FROM `order` WHERE username = '$_username';";
$result = $conn->query($sql);
echo "hi";

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "orderID = " . $row['orderID']. "<br>";
		echo "barcode = " . $row['pBarcode']. "<br>";
		echo "quantity = " . $row['quantity']. "<br><br><br><br>";
    }
} 
$conn->close();
?>