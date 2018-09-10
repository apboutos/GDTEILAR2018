package com.exophrenik.grinia.server;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.exophrenik.grinia.login.LoginScreen;
import com.exophrenik.grinia.order.OrderScreen;
import com.exophrenik.grinia.register.RegisterScreen;
import com.exophrenik.grinia.scan.ScanScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServerSimulationService extends IntentService {

    private String action;
    private ArrayList<User> userList;
    private ArrayList<Product> productList;

    public ServerSimulationService() { super("ServerSimulationService");}
    @Override

    protected void onHandleIntent(@Nullable Intent intent) {


        action = intent.getStringExtra("action");
        Log.d("RED",action);
        loadUserList();
        loadProductList();

        if (action.equals("login")) {

            String username = intent.getStringExtra("submittedUsername");
            String password = intent.getStringExtra("submittedPassword");

            boolean authentication = false;
            for (User user : userList){
                if (username.equals(user.getUsername())) {
                    if (password.equals(user.getPassword())) {
                        authentication = true;
                    }
                }
            }
            broadcastLoginResponse(authentication);
        }
        if(action.equals("register")){

            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            String email = intent.getStringExtra("email");

            boolean registration = true;
            boolean usernameError = false;
            boolean emailError = false;
            for (User user : userList){
                if (username.equals(user.getUsername())) {
                    registration = false;
                    usernameError = true;
                    break;
                }
            }
            if (usernameError == false){
                for (User user : userList){
                    if (email.equals(user.getEmail())) {
                        registration = false;
                        emailError = true;
                        break;
                    }
                }
            }
            if (registration == true){

                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                userList.add(user);
            }
            updateUserList();
            broadcastRegistrationResponse(registration,usernameError,emailError);

        }
        if(action.equals("scan")){

            String barcode = intent.getStringExtra("submittedBarcode");
            boolean result = false;
            for (Product product : productList){
                if (barcode.equals(product.getBarcode())){
                    broadcastScanResponse(true,product.getName(),product.getPrice(),product.getDescription());
                    result = true;
                    break;
                }
            }
            if (result == false){
                broadcastScanResponse(false,"",0.0,"");
            }
        }
        if(action.equals("order")){
            broadcastOrderResponse(true,true);
        }
    }


    private void loadUserList() {

        userList = new ArrayList<>();
        File file = new File(getApplicationContext().getFilesDir(), "offlineUserData");
        // If the file exists read the userList from the file otherwise create a new empty arrayList
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file.getPath());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                userList = (ArrayList<User>) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                //Log.d("RED","CartList exists");
            }
            catch (Exception e) {
                Log.d("RED", e.getMessage());
            }
        }
        else {
            userList = new ArrayList<User>();
            //Log.d("RED","CartList doesnt exist.");
        }


    }

    private void updateUserList(){

        File file = new File(getApplicationContext().getFilesDir(), "offlineUserData");
        // Write the cartList as a serializable object in the new file we just created
        if (file.exists()){
            file.delete();
            try{
                file.createNewFile();
            }
            catch (Exception e){
                Log.d("RED",e.getMessage());
            }

        }
        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userList);
            oos.close();
        }
        catch (Exception e){
            Log.d("RED",e.getMessage());
        }
    }


    private void broadcastLoginResponse(boolean authenticationResult){

        Intent authenticationRespondIntent = new Intent();
        authenticationRespondIntent.putExtra("authenticationResult",authenticationResult);
        authenticationRespondIntent.putExtra("unreachableError",false);
        authenticationRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        authenticationRespondIntent.setAction(LoginScreen.LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        sendBroadcast(authenticationRespondIntent);

    }

    private void broadcastRegistrationResponse(boolean registrationResult, boolean usernameError, boolean emailError){

        Intent registerRespondIntent = new Intent();
        registerRespondIntent.putExtra("registrationResult",registrationResult);
        registerRespondIntent.putExtra("usernameError",usernameError);
        registerRespondIntent.putExtra("emailError",emailError);
        registerRespondIntent.putExtra("unreachableError",false);
        registerRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        registerRespondIntent.setAction(RegisterScreen.RegisterResponseReceiver.SERVER_REGISTER_RESPONSE);
        sendBroadcast(registerRespondIntent);

    }

    private void broadcastScanResponse(boolean barcodeResult, String productName, double productPrice, String productDescription){
        Intent barcodeRespondIntent = new Intent();
        barcodeRespondIntent.putExtra("barcodeResult",barcodeResult);
        barcodeRespondIntent.putExtra("connectionError", false);
        barcodeRespondIntent.putExtra("productName",productName);
        barcodeRespondIntent.putExtra("productDescription",productDescription);
        barcodeRespondIntent.putExtra("productPrice",productPrice);
        barcodeRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        barcodeRespondIntent.setAction(ScanScreen.ScanResponseReceiver.SERVER_SCAN_RESPONSE);
        sendBroadcast(barcodeRespondIntent);
    }

    private void broadcastOrderResponse(boolean connectionError,boolean orderResult){
        Intent orderRespondIntent = new Intent();
        orderRespondIntent.putExtra("orderResult",orderResult);
        orderRespondIntent.putExtra("connectionError", connectionError);
        orderRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        orderRespondIntent.setAction(OrderScreen.OrderResponseReceiver.SERVER_ORDER_RESPONSE);
        Log.d("RED","IM here");
        sendBroadcast(orderRespondIntent);
        Log.d("RED","IM here");
    }

    private void loadProductList(){

        productList = new ArrayList<>();
        Product potato = new Product();
        potato.setBarcode("123456789012");
        potato.setName("Πατάτα");
        potato.setPrice(1.2);
        potato.setDescription("Η πατάτα είναι ένα λαχανικό πλύσιο σε υδατάνθρακες. Οι πατάτες μας" +
                " μεγαλώνουν σε 100% φυσικό περιβάλλον δίχως παρασιτοκτώνα. Προτιμίστε τις.");
        productList.add(potato);
        Product corn = new Product();
        corn.setBarcode("671860013624");
        corn.setName("Καλαμπόκι");
        corn.setPrice(2.1);
        corn.setDescription("Το καλαμπόκι λέγεται αλλιώς και φυτικός χρυσός. Ένα από τα πιο θρεπτικά" +
                " λαχανικά τόσο για τον άνθρωπο, όσο και για τα οικόσιτα ζώα.");
        productList.add(corn);
        Product tomato = new Product();
        tomato.setBarcode("9501101530003");
        tomato.setName("Ντομάτα");
        tomato.setDescription("Ντοματες Νευροκοπίου. Μεγαλωμένες στο βουνό, μόνο με νερό. Τις τρως" +
                " το καλοκαίρι και σου φτιάχνει η διάθεση.");
        tomato.setPrice(5.0);
        productList.add(tomato);
        Product peach = new Product();
        peach.setName("Ροδάκινο");
        peach.setPrice(4.2);
        peach.setBarcode("416000336108");
        peach.setDescription("Ροδάκινα χωρίς κουκούτσι από ειδικά διαμορφωμένο σπόρο. Ότι καλύτερο" +
                " κυκλοφτορεί.");
        productList.add(peach);
    }
}

