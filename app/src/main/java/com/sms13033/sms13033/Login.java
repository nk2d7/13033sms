 package com.sms13033.sms13033;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

 public class Login extends AppCompatActivity implements View.OnClickListener {

    //firebase instance
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    EditText emailEditText,passwordEditText;
    Button loginBtn,signUpBtn,exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_login);

        //initializing the firebase instance
        mAuth = FirebaseAuth.getInstance();

        emailEditText=(EditText) findViewById(R.id.emailEditText);
        passwordEditText=(EditText) findViewById(R.id.passwordEditText);

        loginBtn=(Button) findViewById(R.id.loginBtn);
        signUpBtn=(Button) findViewById(R.id.signUpBtn);
        exitBtn=(Button) findViewById(R.id.exitBtn);
        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()){
            case R.id.loginBtn:

                login();

            break;
            case R.id.signUpBtn:

                startActivity(new Intent(this, SignUp.class));
                finish();

             break;
            case R.id.exitBtn:

                finish();
                System.exit(0);

            break;
        }
    }


    // login with email and password using firebase
    public void login(){

       String email=emailEditText.getText().toString();
       String password=passwordEditText.getText().toString();



        if(email.equals("") || password.equals("")) {

            //if one of the fields is blank
            Toast.makeText(getApplicationContext(),
                    "One or more of the required fields is blank!Please try again.",
                    Toast.LENGTH_LONG).show();

        }else {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    currentUser = mAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "Login Successful!",
                                            Toast.LENGTH_LONG).show();
                                    updateUI(currentUser);

                                } else {

                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(),
                                            task.getException().getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                    emailEditText.getText().clear();
                                    passwordEditText.getText().clear();
                                }

                            }
                        });

        }

    }


    public void updateUI(FirebaseUser user) {

        if(user!=null) {

            startActivity(new Intent(this, Sms.class));
            finish();

        }else{

            Toast.makeText(getApplicationContext(), "Something went wrong!Please try again.",
                    Toast.LENGTH_LONG).show();
            emailEditText.getText().clear();
            passwordEditText.getText().clear();
        }

    }


    public void onBackPressed() {

        finish();
        System.exit(0);
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