package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.content.Intent;
import android.location.GnssAntennaInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class Register extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText InputName, InputEmail, InputPassword;
    private ProgressDialog loadingBar;

    Button SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBtn = (Button) findViewById(R.id.register_btn);

        InputName = (EditText) findViewById(R.id.register_username_input);
        InputEmail = (EditText) findViewById(R.id.register_email_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);

        loadingBar = new ProgressDialog(this);


        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateAccount();

            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void CreateAccount() {

        String name = InputName.getText().toString();
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please Write your Name ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Write your Email ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Write your Password ", Toast.LENGTH_SHORT).show();
        }
        /*
        else if(!isEmailValid(emll))
        {
            Toast.makeText(this, "Invalid email ", Toast.LENGTH_SHORT).show();
        }
         */
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait, we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false); //if the user clicks on screen dialog box will not disappear until it completes the process
            loadingBar.show();

            //validating eml if not already available in database create account
            ValidateEmail(name, email, password);
        }
    }

    private void ValidateEmail(String name, String email, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!(snapshot.child("Users").child(name).exists())){ //prim key phone if doesn't exist

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    //create a parent node for all the users,for every user data will be inside his phone num
                    RootRef.child("Users").child(name).updateChildren(userdataMap).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Congartulations " +
                                                "your account has been created", Toast.LENGTH_SHORT).show();

                                        loadingBar.dismiss();

                                        //send user to login activity to login into his account
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(Register.this, "Error Account Not Created Please " +
                                                "Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(Register.this, "This "+ name +" already exists",
                            Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();

                    Toast.makeText(Register.this, "Please try again using another user name",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Register.this, MainActivity2.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Register.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }


        });
    }

}