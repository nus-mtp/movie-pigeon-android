package org.example.team_pigeon.movie_pigeon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button BSignIn = (Button) findViewById(R.id.buttonSignIn);
        final Button BRegister = (Button) findViewById(R.id.buttonRegister);

        BSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Check Credentials and respond accordingly

                // for testing purpose
                System.out.println("Sign in button clicked");
            }
        });

        BSignIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Check Credentials and respond accordingly

                // for testing purpose
                System.out.println("Sign in button clicked");
            }
        });
    }
}
