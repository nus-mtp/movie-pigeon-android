package org.example.team_pigeon.movie_pigeon;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StartActivity extends AppCompatActivity {

    private final GlobalReceiver globalReceiver = new GlobalReceiver();
    private final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0;
    private File credential;
    private FileInputStream fis;
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Disable Landscape Mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // set up broadcast globalReceiver to receive request from background thread
        IntentFilter filter = new IntentFilter();
        filter.addAction("startHomePageActivity");
        filter.addAction("automaticSignin");
        registerReceiver(globalReceiver, filter);

        //Check permission for android 6.0+ devices
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //Ask for permission if there is no permission
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Please give us permission to save important in your device, otherwise this app may not able to run.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
        else{
            //Access credential if there is write to external storage permission
            loadCredential();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCredential();
                } else {
                    //Close the app
                    System.exit(0);
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(globalReceiver);
    }

    private void loadCredential(){
        credential = new File(Environment.getExternalStorageDirectory() + "/MoviePigeon/Signin/credential");
        if (credential.exists()) {
            // sign-in automatically
            // read token from file first
            token = getStringFromFile(credential.getAbsolutePath());
            Log.i("StartActivity", "Token read is " + token);

            Intent homePageIntent = new Intent(this, HomePageActivity.class);
            // pass token to the new activity
            homePageIntent.putExtra("Token", token);
            this.startActivity(homePageIntent);
            finish();

        } else {
            setContentView(R.layout.activity_start);

            SigninPage sp = new SigninPage(StartActivity.this, this);
        }
    }

    String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    String getStringFromFile (String filePath) {
        File fl = new File(filePath);
        FileInputStream fin = null;
        String result = null;
        try {
            fin = new FileInputStream(fl);
            result = convertStreamToString(fin);
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
