<?php

	class Result {
		
	      public $orderResult = false;

    }

	//Store the client information.
	
	$_username = $_GET['username'];
	$_firstName = $_GET['firstName'];
	$_middleName = $_GET['middleName'];
	$_lastName = $_GET['lastName'];
	$_address = $_GET['address'];
	$_postalCode = $_GET['postalCode'];
	$_creditCard = $_GET['creditCard'];
	$_expirationDate = $_GET['expirationDate'];
	$_cvs = $_GET['cvs'];
	$_productListBarcode = array();
	$_productListQuantity = array();
	$_listSize = $_GET['listSize'];
	//echo $_listSize;

	for ($i=0 ; $i<$_listSize ; $i++){
		
		$_productListBarcode[$i] = $_GET["barcode".$i]; 
		$_productListQuantity[$i] = $_GET["quantity".$i];
		//echo "barcode " . $i . " = " . $_productListBarcode[$i];
		//echo "quantity " . $i . " = " . $_productListQuantity[$i];
	}
	
	
	//Database credentials.
	$servername = "localhost:3306";
    $username = "exoph_master";
    $password = "ma582468";
    $dbname = "exophren804440_Grinia";

	//Create connection.
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
    	echo("Connection failed: " . $conn->connect_error);
	}
	
	$aResult = new Result;
	//TODO fix the sql querry.
	$sql = "UPDATE user SET uAddress = '$_address' , uFirstname = '$_firstName', uMiddleName = '$_middleName', uLastname = '$_lastName' , uPostalcode = '$_postalCode', uCCnumber = '$_creditCard', uCCexpdate = '$_expirationDate', uCCsecurecode = '$_cvs' WHERE username = '$_username';";
	$flag = TRUE;
	if ($conn->query($sql) !== TRUE) {
		echo $conn->error;
		$flag = FALSE;
	}
	
	for ($i=0 ; $i<$_listSize ; $i++){
		
        $sql = "INSERT INTO `order` (pBarcode,username,quantity) VALUES  ('$_productListBarcode[$i]','$_username','$_productListQuantity[$i]');";
		
		if ($conn->query($sql) !== TRUE){
			echo $conn->error;
			$flag = FALSE;
		}
	}
	if ($flag === TRUE){
		$aResult->orderResult=TRUE;	
	}
	else{
		echo "Database errror : " . $conn->error;	
	}
	echo json_encode($aResult);
	$conn->close();
?>