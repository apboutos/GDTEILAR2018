package com.exophrenik.grinia.retrieval;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exophrenik.grinia.R;
import com.exophrenik.grinia.boot.MainActivity;


//TODO this class

public class RetrievalScreen extends AppCompatActivity {

    private Button retrieveButton;
    private EditText emailBox;
    private TextView resultBox;
    private String serverEmailResponse;
    private Boolean retrieveButtonFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieval_screen);

        retrieveButton = (Button)    findViewById(R.id.retrieve_button);
        emailBox       = (EditText)  findViewById(R.id.retrieve_email);
        resultBox      = (TextView)  findViewById(R.id.retrieve_result);

        /* Test Purposes */
        serverEmailResponse = "OK";

        retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (retrieveButtonFlag == true)
                {
                    Intent nextScreen = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(nextScreen);
                }
                else
                {
                    if(serverEmailResponse.compareTo("OK") == 0)
                    {
                        resultBox.setText(R.string.retrieve_instructions_result);
                        resultBox.setVisibility(View.VISIBLE);
                        retrieveButton.setText("OK");
                        retrieveButtonFlag = true;
                }
                    else
                    {
                        resultBox.setText(R.string.retrieve_instructions_result_error);
                        resultBox.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }
}
