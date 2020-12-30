package com.sms13033.sms13033;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddCustomMessage extends AppCompatActivity implements View.OnClickListener {

    Button exitBtn,logOutBtn,addCustomMessageBtn,backBtn;
    EditText customMessageEditText;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_add_custom_message);

        //initializing the firebase instance
        mAuth = FirebaseAuth.getInstance();

        exitBtn=(Button) findViewById(R.id.exitBtn4);
        logOutBtn=(Button) findViewById(R.id.logOutBtn2);
        addCustomMessageBtn=(Button) findViewById(R.id.addCustomBtn);
        backBtn=(Button) findViewById(R.id.backBtn);

        exitBtn.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
        addCustomMessageBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        customMessageEditText=(EditText) findViewById(R.id.customMessageEditText);

        exitBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.exitBtn4:

                finish();
                System.exit(0);
                break;
            case R.id.logOutBtn2:

                // user signed out
                mAuth.signOut();
                startActivity(new Intent(AddCustomMessage.this, Login.class));
                finish();
                break;
            case R.id.addCustomBtn:

                addCustomMessage();
                customMessageEditText.getText().clear();
                break;
            case R.id.backBtn:

                startActivity(new Intent(AddCustomMessage.this, Sms.class));
                finish();
                break;
        }
    }

    //adds a new message to the list of available messages a user can send to 13033
    public void addCustomMessage(){

        String message=customMessageEditText.getText().toString();

        //if the text field is blank
        if(message.equals("")) {

            Toast.makeText(getApplicationContext(),
                    "The Custom Destination field is blank!Please try again.",
                    Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(getApplicationContext(),
                    "Destination was successfully added.",
                    Toast.LENGTH_LONG).show();
            DatabaseHelper dataBaseHelper = new DatabaseHelper(this);
            dataBaseHelper.insertRecord(message);
        }
    }




    public void onBackPressed() {

        startActivity(new Intent(AddCustomMessage.this, Sms.class));
        finish();

    }

    private void hideSystemUI() {
        // Enables sticky immersive mode.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    public void onResume(){
        super.onResume();
        hideSystemUI();
    }


}