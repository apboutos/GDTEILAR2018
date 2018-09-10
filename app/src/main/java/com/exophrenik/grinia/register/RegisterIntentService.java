package com.exophrenik.grinia.register;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.register.RegisterScreen;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegisterIntentService extends IntentService {

    private String registerInformation;
    private HttpURLConnection connection;
    private boolean registrationResult = false;
    private boolean usernameError = false;
    private boolean emailError = false;
    private boolean unreachableError = false;

    public RegisterIntentService() {
        super("RegisterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            //formatRegistrationInformationInJSON(intent);

            createConnectionToServer(intent);

            //sendRegisterInformationToServer();

            readRegistrationResultFromServer();
        }
        catch (Exception e) {
            Log.e("DEBUG",e.getMessage(),e);
            unreachableError = true;
        }
        finally {
            broadcastResponse();
            connection.disconnect();
        }

    }


    private void broadcastResponse(){

        Intent registerRespondIntent = new Intent();
        registerRespondIntent.putExtra("registrationResult",registrationResult);
        registerRespondIntent.putExtra("usernameError",usernameError);
        registerRespondIntent.putExtra("emailError",emailError);
        registerRespondIntent.putExtra("unreachableError",unreachableError);
        registerRespondIntent.addCategory(Intent.CATEGORY_DEFAULT);
        registerRespondIntent.setAction(RegisterScreen.RegisterResponseReceiver.SERVER_REGISTER_RESPONSE);
        sendBroadcast(registerRespondIntent);

    }

    /*
    private void formatRegistrationInformationInJSON(Intent intent) throws Exception{

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username",intent.getStringExtra("username"));
        jsonObject.put("password",intent.getStringExtra("password"));
        jsonObject.put("email",intent.getStringExtra("email"));
        registerInformation = jsonObject.toString();
    }*/

    private void createConnectionToServer(Intent intent)throws Exception {

        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String email    = intent.getStringExtra("email");
        URL url = new URL(getResources().getString(R.string.register_URL) + "/?username=" + username + "&password=" + password + "&email=" + email);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        connection.setRequestProperty("Accept","application/json: charset=UTF-8");
        connection.setRequestMethod("GET");
    }

    /*
    private void sendRegisterInformationToServer() throws Exception{
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(registerInformation);
        writer.flush();
        writer.close();
        Log.e("DEBUG","JSON sent: " + registerInformation); // Prints the sent JSON object as it was sent to the server.
    }*/

    private void readRegistrationResultFromServer() throws Exception{

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.readLine()) != null) {
            stringBuilder.append(buffer);
        }
        buffer = stringBuilder.toString();
        Log.e("DEBUG","JSON received: " + buffer); // Prints the received unparsed JSON object.

        JSONObject jsonObject = new JSONObject(buffer);
        registrationResult = jsonObject.getBoolean("registrationResult");
        usernameError = jsonObject.getBoolean("usernameError");
        emailError = jsonObject.getBoolean("emailError");

    }
}

