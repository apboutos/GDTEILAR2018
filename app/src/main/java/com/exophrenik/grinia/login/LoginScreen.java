package com.exophrenik.grinia.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.retrieval.RetrievalScreen;
import com.exophrenik.grinia.server.ServerSimulationService;
import com.exophrenik.grinia.utilities.Network;

public class LoginScreen extends AppCompatActivity {

    private TextView usernameBox;
    private TextView passwordBox;
    private TextView errorBox;
    private Intent newScreen;
    private LoginResponseReceiver receiver;
    private IntentFilter filter;
    private ProgressBar connectionSpinner;
    private Button loginButton;
    private Button forgotButton;
    private boolean onlineMode;
    /*
        Captures the broadcasted intent that either confirms or denies the user's
        login attempt.
    */
    public class LoginResponseReceiver extends BroadcastReceiver {

        public static final String SERVER_LOGIN_RESPONSE = "server";

        @Override
        public void onReceive(Context context, Intent intent) {

            enableInterface();
            if (intent.getBooleanExtra("unreachableError",false))
            {
                showError(R.string.login_error_server_unreachable);
            }
            else if (intent.getBooleanExtra("authenticationResult",false))
            {
                newScreen = new Intent(getApplicationContext(), ScanScreen.class);
                startActivity(newScreen);
            }
            else
            {
                showError(R.string.login_error_unexplained);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        onlineMode = savedInstanceState.getBoolean("onlineMode");

        usernameBox = (TextView) findViewById(R.id.username);
        passwordBox = (TextView) findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButtonS);
        forgotButton = (Button) findViewById(R.id.forgotCodesButton);
        errorBox = (TextView) findViewById(R.id.errorBox);
        errorBox.setVisibility(View.INVISIBLE);
        connectionSpinner = (ProgressBar) findViewById(R.id.register_connectionSpinner);
        connectionSpinner.setVisibility(View.INVISIBLE);
        connectionSpinner.setIndeterminate(true);

        /* Create and register the login receiver with the appropriate filter */
        filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        receiver = new LoginResponseReceiver();
        registerReceiver(receiver,filter);

        /* Login Button functionality
           When the button is clicked the text contained in the username and password box
           is passed down to an IntentService via an Intent in order for them to be sent to
           the server for confirmation.
        */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usernameBox.getText().length() == 0 || passwordBox.getText().length() == 0)
                {
                    showError(R.string.login_error_empty_fields);
                }
                else if(Network.isNetworkAvailable(LoginScreen.this))
                {
                    startTheLoginIntentService();
                    disableInterface();
                }
                else
                {
                    showError(R.string.login_error_no_internet_connection);
                }

            }
        });

        /* Forgot Button functionality
           Transports the user to a new credential retrieval activity.
        */
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    newScreen = new Intent(getApplicationContext(),RetrievalScreen.class);
                    startActivity(newScreen);
            }
        });
    }

    private void showError(int resID){
        errorBox.setText(resID);
        errorBox.setVisibility(View.VISIBLE);
    }

    private void clearError(){
        errorBox.setVisibility(View.INVISIBLE);
    }

    private void enableInterface(){
        loginButton.setClickable(true);
        forgotButton.setClickable(true);
        connectionSpinner.setVisibility(View.INVISIBLE);
    }

    private void disableInterface(){
        loginButton.setClickable(false);
        forgotButton.setClickable(false);
        connectionSpinner.setVisibility(View.VISIBLE);
    }

    private void startTheLoginIntentService(){
        Intent startLoginIntentService;
        if (onlineMode == true){
            startLoginIntentService = new Intent(LoginScreen.this, LoginIntentService.class);
        }
        else {
            startLoginIntentService = new Intent(LoginScreen.this, ServerSimulationService.class);
        }
        startLoginIntentService.putExtra("submittedUsername",usernameBox.getText().toString());
        startLoginIntentService.putExtra("submittedPassword",passwordBox.getText().toString());
        startLoginIntentService.putExtra("action","login");
        startService(startLoginIntentService);


    }

    @Override
    protected void onPause(){
        super.onPause();

        unregisterReceiver(receiver);

    }

    @Override
    protected void onResume(){
        super.onResume();

        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        unregisterReceiver(receiver);
    }
}
