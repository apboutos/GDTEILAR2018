package com.exophrenik.grinia.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.login.LoginScreen;
import com.exophrenik.grinia.server.ServerSimulationService;
import com.exophrenik.grinia.utilities.Network;


public class RegisterScreen extends AppCompatActivity {

    private Button registerButton;
    private EditText usernameBox;
    private EditText passwordBox;
    private EditText passwordRetypeBox;
    private EditText emailBox;
    private TextView errorMessageBox;
    private ProgressBar connectionSpinner;
    private Boolean onlineMode;

    private RegisterResponseReceiver responseReceiver;
    private IntentFilter filter;

    public class RegisterResponseReceiver extends BroadcastReceiver {

        public static final String SERVER_REGISTER_RESPONSE = "";
        @Override
        public void onReceive(Context context, Intent intent) {

            enableInterface();
            if (intent.getBooleanExtra("registrationResult",false))
            {
                clearError();
                goToLoginScreen();
            }
            else
            {
                showRegistrationError(intent);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        onlineMode = savedInstanceState.getBoolean("onlineMode");

        registerButton    = (Button)      findViewById(R.id.register_button);
        usernameBox       = (EditText)    findViewById(R.id.register_username);
        passwordBox       = (EditText)    findViewById(R.id.register_password);
        passwordRetypeBox = (EditText)    findViewById(R.id.register_password_retype);
        emailBox          = (EditText)    findViewById(R.id.register_email);
        errorMessageBox   = (TextView)    findViewById(R.id.register_error);
        errorMessageBox.setVisibility(View.INVISIBLE);
        connectionSpinner = (ProgressBar) findViewById(R.id.register_connectionSpinner);
        connectionSpinner.setIndeterminate(true);
        connectionSpinner.setVisibility(View.INVISIBLE);

        /* Create and register the register receiver with the appropriate filter */
        filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(RegisterResponseReceiver.SERVER_REGISTER_RESPONSE);
        responseReceiver = new RegisterResponseReceiver();
        registerReceiver(responseReceiver,filter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Checking for client side errors before starting communication
                   with the server through an IntentService. */
                if (someFieldIsEmpty())
                {
                    showError(R.string.login_error_empty_fields);
                }
                else if (passwordAndPasswordRetypeDontMatch())
                {
                    showError(R.string.register_error_password_missmatch);
                }
                else if (!passwordIsValid())
                {
                    showError(R.string.register_error_invalid_password);
                }
                else if (!emailIsValid())
                {
                    showError(R.string.register_error_invalid_email);
                }
                else if (!Network.isNetworkAvailable(RegisterScreen.this))
                {
                    showError(R.string.login_error_no_internet_connection);
                }
                else
                {
                    disableInterface();
                    startRegisterIntentService();
                }
            }
        });
    }

    private void goToLoginScreen(){
        Toast registerComplete = Toast.makeText(getApplicationContext(),R.string.register_complete_message,Toast.LENGTH_SHORT);
        registerComplete.setGravity(Gravity.CENTER,0,0);
        registerComplete.show();

        Intent intent = new Intent(getApplicationContext(),LoginScreen.class);
        startActivity(intent);

    }

    private boolean passwordIsValid(){

        String password = passwordBox.getText().toString();
        if(password.length() < 8)
        {
            return false;
        }
        //If password doesn't contain a number.
        if(!password.matches(".*\\d.*"))
        {
            Log.d("DEBUG","Error: Password does not contain numbers.");
            return false;
        }
        //If password doesn't contain a letter.
        if(!password.matches(".*[a-zA-Z].*"))
        {
            Log.d("DEBUG","Error: Password does not contain letters.");
            return false;
        }
        return true;
    }

    private boolean emailIsValid(){
        String email = emailBox.getText().toString();
        if (email.contains("@"))
        {
            String emailSubstringAfterAt = email.substring(email.lastIndexOf("@"));
            if (emailSubstringAfterAt.contains("."))
            {
                return true;
            }
        }
        return false;
    }

    private void showRegistrationError(Intent intent){

        if(intent.getBooleanExtra("usernameError",false))
        {
            showError(R.string.register_error_username_taken);
        }
        else if (intent.getBooleanExtra("emailError",false))
        {
            showError(R.string.register_error_email_taken);
        }
        else if (intent.getBooleanExtra("unreachableError",false))
        {
            showError(R.string.login_error_server_unreachable);
        }
    }

    private boolean someFieldIsEmpty(){

        return usernameBox.getText().length() == 0 ||
               passwordBox.getText().length() == 0 ||
               passwordRetypeBox.getText().length() == 0 ||
               emailBox.getText().length() == 0;
    }

    private boolean passwordAndPasswordRetypeDontMatch(){

        return !passwordBox.getText().toString().equals(passwordRetypeBox.getText().toString());
    }

    private void startRegisterIntentService(){

        Intent registerIntent;
        if (onlineMode == true){
            registerIntent = new Intent(RegisterScreen.this, RegisterIntentService.class);
        }
        else {
            registerIntent = new Intent(RegisterScreen.this, ServerSimulationService.class);
        }
        registerIntent.setAction(Intent.ACTION_SEND);
        registerIntent.putExtra("username", usernameBox.getText().toString());
        registerIntent.putExtra("password", passwordBox.getText().toString());
        registerIntent.putExtra("email", emailBox.getText().toString());
        registerIntent.putExtra("action","register");
        startService(registerIntent);
    }

    private void showError(int resID){
        errorMessageBox.setText(resID);
        errorMessageBox.setVisibility(View.VISIBLE);
    }

    private void clearError(){
        errorMessageBox.setVisibility(View.INVISIBLE);
    }

    private void disableInterface(){
        connectionSpinner.setVisibility(View.VISIBLE);
        registerButton.setClickable(false);
    }

    private void enableInterface(){
        connectionSpinner.setVisibility(View.INVISIBLE);
        registerButton.setClickable(true);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(responseReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(responseReceiver, filter);
    }

    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(responseReceiver);
    }
}
