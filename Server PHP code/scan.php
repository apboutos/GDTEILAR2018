<?php

class Result {
		
	public $barcodeResult = false;
	public $productName = "";
	public $productDescription = "";
	public $productPrice = 0;
	
}

$_barcode = $_GET['barcode']; 

$servername = "localhost:3306";
$username = "exoph_master";
$password = "ma582468";
$dbname = "exophren804440_Grinia";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
mysqli_set_charset($conn, "utf8");
// Check connection
if ($conn->connect_error) {
    echo("Connection failed: " . $conn->connect_error);
}
else
{
	//echo "connected";
}

$sql = "SELECT * FROM product WHERE pBarcode = '$_barcode';";
$result = $conn->query($sql);
$aResult = new Result;

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        if ($row["pBarcode"] == $_barcode){
			

				$aResult -> barcodeResult = true;
			    $aResult -> productName = $row["pName"];
	            $aResult -> productDescription = $row["pDescription"];
	            $aResult -> productPrice = $row["pPrice"];
		
		}
    }
} 

echo json_encode($aResult);

$conn->close();
?>