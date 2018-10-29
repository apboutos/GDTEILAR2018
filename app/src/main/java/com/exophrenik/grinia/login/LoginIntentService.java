package com.exophrenik.grinia.login;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.exophrenik.grinia.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class handles asynchronous login requests from the login srceen activity.
 */
public class LoginIntentService extends IntentService {

    private HttpURLConnection connection;
    private String loginInformation;
    private boolean authenticationResult = false;
    private boolean unreachableError = false;

    public LoginIntentService() {
        super("LoginIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            //formatLoginInformationInJSON(intent);

            createConnectionToServer(intent);

           // sendLoginInformationToServer();

            readAuthenticationResultFromServer();

        }
        catch (Exception e) {
            Log.d("RED","Error: " + e.getMessage());
            unreachableError = true;
        }
        finally {
            broadcastResponse();
            connection.disconnect();
        }

    }

    /*
    private void formatLoginInformationInJSON(Intent intent) throws Exception{
        try {
            String submittedUsername = intent.getStringExtra("submittedUsername");
            String submittedPassword = intent.getStringExtra("submittedPassword");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", submittedUsername);
            jsonObject.put("password", submittedPassword);
            loginInformation = jsonObject.toString();
            Log.d("RED","Login Info: " + loginInformation);
        }
        catch (Exception e) {
            Log.d("RED",e.getMessage());
        }
    }*/

    private void createConnectionToServer(Intent intent)throws Exception {

        String submittedUsername = intent.getStringExtra("submittedUsername");
        String submittedPassword = intent.getStringExtra("submittedPassword");
        connection = (HttpURLConnection) new URL("http://www.masterpaint.gr/login.php" + "/?username=" + submittedUsername.toString() + "&password=" + submittedPassword).openConnection();//getResources().getString()).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.setRequestProperty("Accept","application/json: charset=UTF-8");
        connection.setRequestMethod("GET");
        Log.d("RED","Request method " + connection.getRequestMethod());
    }

    /*
        private void sendLoginInformationToServer() throws Exception{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(loginInformation);
            writer.flush();
            Log.e("RED","LOG: " + loginInformation); // Prints the sent JSON object.
            writer.close();
            Log.d("RED","Response Message " + connection.getResponseMessage());
        }*/

    private void readAuthenticationResultFromServer() throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        Log.d("RED","LOG: " + stringBuilder.toString()); // Prints the received unparsed JSON object.
        String tmp = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(tmp);
        authenticationResult = jsonObject.getBoolean("authenticationResult");
    }

    private void broadcastResponse(){

        Intent authenticationRespondIntent = new Intent();
        authenticationRespondIntent.putExtra("authenticationResult",authenticationResult);
        authenticationRespondIntent.putExtra("unreachableError",unreachableError);
        authenticationRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        authenticationRespondIntent.setAction(LoginScreen.LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        sendBroadcast(authenticationRespondIntent);

    }
}
