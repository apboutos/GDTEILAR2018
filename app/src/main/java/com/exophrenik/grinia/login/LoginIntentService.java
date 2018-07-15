package com.exophrenik.grinia.login;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.login.LoginScreen;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

            formatLoginInformationInJSON(intent);

            createConnectionToServer();

            sendLoginInformationToServer();

            readAuthenticationResultFromServer();

        }
        catch (Exception e) {
            Log.d("RED",e.getMessage());
            unreachableError = true;
        }
        finally {
            broadcastResponse();
            connection.disconnect();
        }

    }

    private void formatLoginInformationInJSON(Intent intent) throws Exception{
        try {
            String submittedUsername = intent.getStringExtra("submittedUsername");
            String submittedPassword = intent.getStringExtra("submittedPassword");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", submittedUsername);
            jsonObject.put("password", submittedPassword);
            loginInformation = jsonObject.toString();
            Log.d("RED",loginInformation);
        }
        catch (Exception e) {
            Log.d("RED",e.getMessage());
        }
    }

    private void createConnectionToServer()throws Exception {

        connection = (HttpURLConnection) new URL(getResources().getString(R.string.login_URL)).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.setRequestProperty("Accept","application/json: charset=UTF-8");
        connection.setRequestMethod("POST");
        Log.d("DEBUG","Request method " + connection.getRequestMethod());
    }

    private void sendLoginInformationToServer() throws Exception{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(loginInformation);
        writer.flush();
        Log.e("DEBUG","LOG: " + loginInformation); // Prints the sent JSON object.
        writer.close();
        Log.d("DEBUG","Response Message " + connection.getResponseMessage());
    }

    private void readAuthenticationResultFromServer() throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        Log.d("RED","LOG: " + stringBuilder.toString()); // Prints the received unparsed JSON object.
        String tmp = stringBuilder.toString();
        /*TODO
          There is a bug here. The PHP server does not return a properly formatted JSON string
          and it cannot be converted to a JSON object. Possible but not working solution is
          to substring the server's response contained in tmp like this ----->
          tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1));
         */
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
