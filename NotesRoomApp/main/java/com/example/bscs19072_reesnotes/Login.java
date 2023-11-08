package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;

public class Login extends AppCompatActivity {

    private EditText InputName , InputPassword;
    private Button LoginBtn;
    private ProgressDialog loadingBar;

    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginBtn = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputName  = (EditText) findViewById(R.id.login_username_input);
        loadingBar = new ProgressDialog(this);

        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginUser();

            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginBtn.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins"; //parent name for all admins inside database
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginBtn.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users"; //parent name for all admins inside database
            }
        });

    }

    private void loginUser() {

        String name = InputName.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please Write your name ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Write your Password ", Toast.LENGTH_SHORT).show();
        }
        else //allow user to login
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please Wait, we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false); //if the user clicks on screen dialog box will not disappear until it completes the process
            loadingBar.show();

            AllowAccessToAccount(name, password);
        }
    }

    private void AllowAccessToAccount(String name, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        //to check if user available or not
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDbName).child(name).exists()) {

                    Users usersData = snapshot.child(parentDbName).child(name).getValue(Users.class);

                    //retrieve users data using setter..
                    if(usersData.getName().equals(name)){

                        if(usersData.getPassword().equals(password)){ //password entered in edit text equals that in database

                            if(parentDbName.equals("Admins")){

                                Toast.makeText(Login.this, "Admin Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                // intent = new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
                                Intent intent = new Intent(Login.this, AdminCoursesActivity.class); //AdminActivity
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){

                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(Login.this, Degree.class); // Degree
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{
                    Toast.makeText(Login.this,"Account with this user name does " +
                            "not exist", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();

                    //Toast.makeText(LoginActivity.this,"Create an Account First", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}