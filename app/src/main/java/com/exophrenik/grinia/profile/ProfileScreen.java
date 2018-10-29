package com.exophrenik.grinia.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.utilities.Profile;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ProfileScreen extends AppCompatActivity {

    private Profile profile;
    private String username;
    private  boolean onlineMode;

    private TextView firstNameBox;
    private TextView middleNameBox;
    private TextView lastNameBox;
    private TextView addressBox;
    private TextView postalCodeBox;
    private TextView creditCardNumberBox;
    private TextView expirationDateBox;
    private TextView cvsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        username = getIntent().getStringExtra("username");
        onlineMode = getIntent().getBooleanExtra("onlineMode",false);

        firstNameBox = (TextView) findViewById(R.id.firstNameBox);
        middleNameBox = (TextView) findViewById(R.id.middleNameBox);
        lastNameBox = (TextView) findViewById(R.id.lastNameBox);
        addressBox = (TextView) findViewById(R.id.addressBox);
        postalCodeBox = (TextView) findViewById(R.id.postalCodeBox);
        creditCardNumberBox = (TextView) findViewById(R.id.creditCardNumberBox);
        expirationDateBox = (TextView) findViewById(R.id.creditCardExpirationDate);
        cvsBox = (TextView) findViewById(R.id.creditCardCVSBox);

        Button saveButton = (Button) findViewById(R.id.saveProfileButton);
        Button cancelButton = (Button) findViewById(R.id.cancelProfileButton);

        readProfileFromFile();

        firstNameBox.setText(profile.getFirstName());
        middleNameBox.setText(profile.getMiddleName());
        lastNameBox.setText(profile.getLastName());
        addressBox.setText(profile.getAddress());
        postalCodeBox.setText(profile.getPostalCode());
        creditCardNumberBox.setText(profile.getCreditCartNumber());
        expirationDateBox.setText(profile.getCreditExpirationDate());
        cvsBox.setText(profile.getCreditCV());




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveProfileToFile();
                Toast.makeText(getApplicationContext(),"Profile saved",Toast.LENGTH_LONG);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }


    private void readProfileFromFile(){

        File file = new File(getFilesDir(),"profile" + username);
        if (file.exists()){

            try {
                FileInputStream fis = new FileInputStream(file.getPath());
                ObjectInputStream ois = new ObjectInputStream(fis);
                profile = (Profile) ois.readObject();
                ois.close();
                fis.close();
            }
            catch ( Exception e){
                Log.d("RED", e.getMessage());
            }
        }
        else
        {
            profile = new Profile();
        }
    }

    private void saveProfileToFile(){

        File file = new File(getFilesDir(),"profile" + username);

        profile.setFirstName(firstNameBox.getText().toString());
        profile.setMiddleName(middleNameBox.getText().toString());
        profile.setLastName(lastNameBox.getText().toString());
        profile.setAddress(addressBox.getText().toString());
        profile.setPostalCode(postalCodeBox.getText().toString());
        profile.setCreditCartNumber(creditCardNumberBox.getText().toString());
        profile.setCreditExpirationDate(expirationDateBox.getText().toString());
        profile.setCreditCV(cvsBox.getText().toString());
        if(file.exists()){
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(file.getPath());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(profile);
            oos.close();
            fos.close();
        }
        catch ( Exception e ){
            Log.d("RED",e.getMessage());
        }

    }
}
