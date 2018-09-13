package com.exophrenik.grinia.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.boot.MainActivity;
import com.exophrenik.grinia.cart.CartScreen;
import com.exophrenik.grinia.login.LoginScreen;
import com.exophrenik.grinia.product.ProductScreen;
import com.exophrenik.grinia.profile.ProfileScreen;
import com.exophrenik.grinia.server.ServerSimulationService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class ScanScreen extends AppCompatActivity {


    private Button scanButton;
    private Button cartButton;
    private Button profileButton;
    private Button logoutButton;
    private IntentIntegrator integrator;
    private Intent nextScreen;
    private IntentFilter filter;
    private ScanResponseReceiver receiver;
    private String barcodeResult;
    private ProgressBar connectionSpinner;
    private boolean onlineMode;
    private String username;


    public class ScanResponseReceiver extends BroadcastReceiver {

        public static final String SERVER_SCAN_RESPONSE = "server";

        @Override
        public void onReceive(Context context, Intent intent) {

            enableInterface();
            if (intent.getBooleanExtra("noBarcodeScanned",false)){
                showError(R.string.no_scan_barcode_error);
            }
            else if(intent.getBooleanExtra("connectionError", false))
            {
                showError(R.string.login_error_server_unreachable);
            }
            // If the barcode Result was correct
            else if(intent.getBooleanExtra("barcodeResult",false))
            {
                String productName = intent.getStringExtra("productName");
                String productDescription = intent.getStringExtra("productDescription");
                double productPrice = intent.getDoubleExtra("productPrice",0);

                nextScreen = new Intent(getApplicationContext(), ProductScreen.class);
                nextScreen.putExtra("productName",productName);
                nextScreen.putExtra("productDescription",productDescription);
                nextScreen.putExtra("productPrice",productPrice);
                nextScreen.putExtra("barcode",barcodeResult);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
            // Product not found error
            else
            {
                showError(R.string.scan_barcode_error);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_screen);

        onlineMode = getIntent().getBooleanExtra("onlineMode", false);
        if ((username = getIntent().getStringExtra("username")) == null){
            username = "root";
        }

        integrator = new IntentIntegrator(this);
        scanButton = (Button) findViewById(R.id.scanButton);
        cartButton = (Button) findViewById(R.id.cartButton);
        profileButton = (Button) findViewById(R.id.settingsButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        connectionSpinner = (ProgressBar) findViewById(R.id.connectionSpinner);
        connectionSpinner.setIndeterminate(true);
        connectionSpinner.setVisibility(View.INVISIBLE);

        /* Create and register the scan receiver with the appropriate filter */
        filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(LoginScreen.LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        receiver = new ScanScreen.ScanResponseReceiver();
        registerReceiver(receiver,filter);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                integrator.initiateScan();
                //disableInterface();

            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(), CartScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username",username);
                startActivity(nextScreen);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(), ProfileScreen.class);
                nextScreen.putExtra("username",username);
                nextScreen.putExtra("onlineMode",onlineMode);
                startActivity(nextScreen);
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextScreen);
                //TODO implement Token for Logout functionality.
            }
        });

    }

    // Handles the result from the Barcode Scanner Application Activity
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            Toast.makeText(this.getApplicationContext(),scanResult.getContents(), Toast.LENGTH_LONG).show();
            barcodeResult = scanResult.getContents();
            // The number of the barcode is contained in the scanResult contents.
            startTheScanIntentService();
        }
        // else continue with any other code you need in the method
    }

    private void startTheScanIntentService(){
        Intent startScanIntentService;
        if (onlineMode == true){
            startScanIntentService = new Intent(ScanScreen.this, ScanIntentService.class);
        }
        else{
            startScanIntentService = new Intent(ScanScreen.this, ServerSimulationService.class);
        }
        startScanIntentService.putExtra("submittedBarcode",barcodeResult);
        startScanIntentService.putExtra("action","scan");
        startService(startScanIntentService);

    }

    private void disableInterface(){

        scanButton.setClickable(false);
        profileButton.setClickable(false);
        cartButton.setClickable(false);
        logoutButton.setClickable(false);
        connectionSpinner.setVisibility(View.VISIBLE);

    }

    private void enableInterface(){

        scanButton.setClickable(true);
        profileButton.setClickable(true);
        cartButton.setClickable(true);
        logoutButton.setClickable(true);
        connectionSpinner.setVisibility(View.INVISIBLE);

    }

    private void showError(int resID){
        //TODO Implement error output
    }

    @Override
    protected void onResume() {
        super.onResume();
        filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(LoginScreen.LoginResponseReceiver.SERVER_LOGIN_RESPONSE);
        receiver = new ScanScreen.ScanResponseReceiver();
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
