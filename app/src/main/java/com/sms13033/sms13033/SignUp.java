package com.sms13033.sms13033;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class  SignUp extends AppCompatActivity implements View.OnClickListener {

    //firebase instance
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    SaveSharedPreferences preference;

    EditText fullNameEditText,emailEditText,passwordEditText;
    Button signUpBtn,loginBtn,exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_sign_up);

        //initializing the firebase instance
        mAuth = FirebaseAuth.getInstance();

        fullNameEditText=(EditText) findViewById(R.id.fullNameEditText);
        emailEditText=(EditText) findViewById(R.id.emailEditText2);
        passwordEditText=(EditText) findViewById(R.id.passwordEditText2);

        signUpBtn=(Button) findViewById(R.id.signUpBtn2);
        loginBtn=(Button) findViewById(R.id.loginBtn2);
        exitBtn=(Button) findViewById(R.id.exitBtn2);

        signUpBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()){
            case R.id.signUpBtn2:

                signUp();

             break;
            case R.id.loginBtn2:

                startActivity(new Intent(this, Login.class));
                finish();
             break;
            case R.id.exitBtn2:

                finish();
                System.exit(0);
            break;
        }

    }

    //sign up  with email and password using firebase as well as fullname that is saved into sharedPreferences
    public void signUp(){

        String fullName=fullNameEditText.getText().toString();
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();



        if(email.equals("") || password.equals("") || fullName.equals("")) {
            //one of the fields is blank
            Toast.makeText(getApplicationContext(),
                    "One or more of the required fields is blank!Please try again.",
                    Toast.LENGTH_LONG).show();

        }else {

            SaveSharedPreferences.setFullname(SignUp.this,fullName);

            mAuth.createUserWithEmailAndPassword(
                    email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


                                Toast.makeText(getApplicationContext(), "SignUp Successful!",
                                        Toast.LENGTH_LONG).show();
                                currentUser = mAuth.getCurrentUser();

                                // user signed out in order to sign in again
                                mAuth.signOut();
                                startActivity(new Intent(SignUp.this, Login.class));
                                finish();

                            } else {

                                // If sign up fails, display a message to the user.
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(), Toast.LENGTH_LONG)
                                        .show();
                                fullNameEditText.getText().clear();
                                emailEditText.getText().clear();
                                passwordEditText.getText().clear();

                            }

                        }
                    });
        }
    }

    public void onBackPressed() {

        startActivity(new Intent(this, Login.class));
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