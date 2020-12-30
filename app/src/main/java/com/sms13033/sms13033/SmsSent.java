package com.sms13033.sms13033;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SmsSent extends AppCompatActivity implements View.OnClickListener {

    Button exitBtn,logOutBtn,backBtn;
    TextView messageTextView;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_sms_sent);

        //initializing the firebase instance
        mAuth = FirebaseAuth.getInstance();

        exitBtn=(Button) findViewById(R.id.exitBtn5);
        logOutBtn=(Button) findViewById(R.id.logOutBtn5);
        backBtn=(Button) findViewById(R.id.backBtn5);
        messageTextView=(TextView) findViewById(R.id.messageTextView);

        exitBtn.setOnClickListener(this);
        logOutBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        Intent intent = getIntent();
        message= intent.getStringExtra("message");

        //display the sms that the user sent to 13033
        messageTextView.setText(message);

    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.exitBtn5:

                finish();
                System.exit(0);
                break;
            case R.id.logOutBtn5:

                // user signed out
                mAuth.signOut();
                startActivity(new Intent(this, Login.class));
                finish();
                break;
            case R.id.backBtn5:

                startActivity(new Intent(this, Sms.class));
                finish();
                break;
        }

    }

    public void onBackPressed() {

        startActivity(new Intent(this, Sms.class));
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