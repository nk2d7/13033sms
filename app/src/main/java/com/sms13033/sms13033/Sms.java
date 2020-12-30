package com.sms13033.sms13033;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Sms extends AppCompatActivity implements View.OnClickListener,LocationListener {

    SaveSharedPreferences preference;
    Button exit,logOut,addCustomMessageBtn,sendSmsBtn,voiceBtn;
    EditText addressEditText,fullNameEditText;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference messages;

    ListView messagesList;
    ArrayAdapter<String> messagesAdapter;
    ArrayList<String> messagesArrayList;

    LocationManager locationManager;
    Location location;

    double x, y;// latitude,longitude

    // The minimum distance to change Updates in meters
    private static long MIN_DISTANCE_CHANGE_FOR_UPDATES;
    // The minimum time between updates in milliseconds
    private static long MIN_TIME_BW_UPDATES;

    String timeStamp,userId,selectedFromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI();
        setContentView(R.layout.activity_sms);

        mAuth = FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        userId=currentUser.getUid();

        //initializing the firebase instance
        database = FirebaseDatabase.getInstance();
        //creating nodes into realtime db firebase
        messages = database.getReference("messages").child("users").child(userId);


        exit=(Button) findViewById(R.id.exitBtn3);
        logOut=(Button) findViewById(R.id.logOutBtn);
        addCustomMessageBtn=(Button) findViewById(R.id.addCustomMessageBtn);
        sendSmsBtn=(Button) findViewById(R.id.sendSmsBtn);
        voiceBtn=(Button) findViewById(R.id.voiceBtn);
        addressEditText=(EditText) findViewById(R.id.addressEditText);
        fullNameEditText=(EditText) findViewById(R.id.fullNameEditText2);
        messagesList=(ListView) findViewById(R.id.messagesList);

        exit.setOnClickListener(this);
        logOut.setOnClickListener(this);
        addCustomMessageBtn.setOnClickListener(this);
        sendSmsBtn.setOnClickListener(this);
        voiceBtn.setOnClickListener(this);

        //getting the fullname of the user from shared preferences*if it exists*
        fullNameEditText.setText(preference.getFullname(Sms.this), TextView.BufferType.EDITABLE);

        //variables for gps
        MIN_TIME_BW_UPDATES = 1000;//milliseconds
        MIN_DISTANCE_CHANGE_FOR_UPDATES =2;//m

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        DatabaseHelper dataBaseHelper = new DatabaseHelper(this);

        // if the user opens the app for the first time fill the messageslist with the 6 standard sms of 13033
        if(preference.getFirstTimeOrNot(Sms.this).length() == 0){


            //1.Doctor/Pharmacy
            dataBaseHelper.insertRecord("Doctor/Pharmacy");
            //2.Supply Store
            dataBaseHelper.insertRecord("Supply Store");
            //3.Bank
            dataBaseHelper.insertRecord("Bank");
            //4.Help Individuals /Escort to-from school
            dataBaseHelper.insertRecord("Help Individuals /Escort to-from school");
            //5.Ceremonies/Family
            dataBaseHelper.insertRecord("Ceremonies/Family");
            //6.Physical Exercise/Pet Walk
            dataBaseHelper.insertRecord("Physical Exercise/Pet Walk");

            SaveSharedPreferences.setFirstTime(Sms.this);

        }


       // messagesArrayList.clear();
        messagesArrayList=dataBaseHelper.getMessages();
        messagesAdapter=new ArrayAdapter(Sms.this , android.R.layout.simple_list_item_1,messagesArrayList);
        messagesList.setAdapter(messagesAdapter);

        //Default selected item(first one in the list)
        messagesList.setItemChecked(0,true);

        //everytime an item of the list gets selected by the user it's saved in selectedFromList variable
        messagesList.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                selectedFromList = (String) messagesList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        selectedFromList,
                        Toast.LENGTH_LONG).show();

            }
        });

    }



    @Override
    public void onClick(View v) {
        Utils.preventTwoClick(v);
        switch (v.getId()) {
            case R.id.exitBtn3:

                finish();
                System.exit(0);

                break;
            case R.id.logOutBtn:

                // user signed out
                mAuth.signOut();
                startActivity(new Intent(Sms.this, Login.class));
                finish();

                break;
            case  R.id.voiceBtn:

                vcBtn();
                break;
            case R.id.addCustomMessageBtn:

                startActivity(new Intent(Sms.this,AddCustomMessage.class));
                finish();
                break;
            case R.id.sendSmsBtn:

                //calling the function that checks all the parameters needed in order to send the sms
                    Sms();

                break;
        }
    }

    //function that checks all the parameters needed in order to send the sms
    public  void Sms(){

        //checking if gps is enabled or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS is not enabled !!
            Toast.makeText(this, "Please open device's gps and try again!",
                    Toast.LENGTH_LONG).show();

        } else {

            String fullName = fullNameEditText.getText().toString();
            String address = addressEditText.getText().toString();




            if (address.equals("") || fullName.equals("")) {

                //if any of the textfields is blank
                Toast.makeText(getApplicationContext(),
                        "One or more of the required fields is blank or you haven't select a message option from the list!Please try again.",
                        Toast.LENGTH_LONG).show();

            } else {

                      //calling the gps() funtion that eventually gives us latitude and longitude
                       gps();


                        //getting timestamp
                        Calendar theEnd = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        timeStamp = dateFormat.format(theEnd.getTime());

                        //creating a string with the timestamp as well as the latitude and longitude
                        String location =
                                "TimeStamp :" + timeStamp + " , Latitude x : " + x + ", Longitude y :" + y;

                        //saving that string to firebase
                        DatabaseReference locationRef = messages.child("message").push();
                        locationRef.child("Time and coordinates").setValue(location);

                        //saving to shared preferences the fullname the user enter in order to be available next time
                        SaveSharedPreferences
                                .setFullname(Sms.this, fullNameEditText.getText().toString());

                        //creating a string that contains the message that it's going to be sent to 13033
                        // string message structure : destination: ,fullname,address
                        String message= selectedFromList+" : "+fullNameEditText.getText().toString()+" , "+addressEditText.getText().toString();
                        // calling the function sendSms which will eventually send the sms
                        sendSms(message);


            }
        }

    }


    public void  gps(){

        //checking if the app is allowed to use the gps and if not,request user for permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);

    }


    @Override
    public void onLocationChanged(Location location) {
        //getting longitude and latitude
        x = location.getLatitude();
        y = location.getLongitude();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // function sendSms which will eventually send the sms
    public void sendSms(String message){


        //checks for permissions
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},5434);

        }else{

            Toast.makeText(getApplicationContext(),
                    "Sms sent!",
                    Toast.LENGTH_LONG).show();
            SmsManager manager=SmsManager.getDefault();

            manager.sendTextMessage("13033",null,message,null,null );

            Intent j = new Intent(this,SmsSent.class);
            j.putExtra("message",message);
            startActivity(j);
            finish();

        }


    }


    //voice command btn
    public void  vcBtn(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");

        startActivityForResult(intent,742);
    }


    //actions based on the voice command we gave
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null && resultCode==RESULT_OK){
            ArrayList<String>  matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.contains("exit")){
                finish();
                System.exit(0);
            }else if (matches.contains("log out")){
                // user signed out
                mAuth.signOut();
                startActivity(new Intent(Sms.this, Login.class));
                finish();
            }else if (matches.contains("add custom destination")){
                startActivity(new Intent(Sms.this,AddCustomMessage.class));
                finish();
            }else if(matches.contains("send sms")){

                Sms();

            }

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