package com.exophrenik.grinia.guest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class GuestScreen extends AppCompatActivity {

    private Button scanButton;
    private IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_screen);

        integrator = new IntentIntegrator(this);
        scanButton = (Button) findViewById(R.id.scanButton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                integrator.initiateScan();
                
            }
        });
        
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            Toast.makeText(this.getApplicationContext(),scanResult.toString(), Toast.LENGTH_LONG).show();

            // The number of the barcode is contained in the scanResult contents.
        }
        // else continue with any other code you need in the method
 
    }
}
