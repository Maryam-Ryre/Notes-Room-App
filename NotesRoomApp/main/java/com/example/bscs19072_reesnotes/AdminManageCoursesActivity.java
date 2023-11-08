package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminManageCoursesActivity extends AppCompatActivity {

    private Button applyChangesBtn, cDeleteBtn;
    private EditText name, cid, description;
    private ImageView imageView;

    private String courseID = "";
    private DatabaseReference coursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_courses);

        courseID = getIntent().getStringExtra("cid");
        coursesRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseID);

        cDeleteBtn = (Button)findViewById(R.id.delete_course_manage_btn);
        applyChangesBtn = (Button)findViewById(R.id.apply_changes_manage_btn);
        name = (EditText) findViewById(R.id.course_name_manage);
        cid = (EditText) findViewById(R.id.course_id_manage);
        description = (EditText) findViewById(R.id.course_description_manage);
        imageView = findViewById(R.id.course_image_manage);

        displayParticularProductInfo();


        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                applyChanges();

            }
        });

        cDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteProduct();
            }
        });
    }

    private void deleteProduct() {

        coursesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(AdminManageCoursesActivity.this, "Course Deleted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AdminManageCoursesActivity.this, AdminCoursesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void applyChanges() {

        String sName = name.getText().toString();
        String sDescription = description.getText().toString();

        if(sName.equals("")){

            Toast.makeText(this, "Can't be empty", Toast.LENGTH_SHORT).show();
        }
        else if(sDescription.equals("")){
            Toast.makeText(this, "Can't be empty", Toast.LENGTH_SHORT).show();
        }
        else{

            //store using hashmap
            HashMap<String, Object> productMap = new HashMap<>();
            //putting data to it
            productMap.put("courseid", courseID);
            productMap.put("description", sDescription);
            productMap.put("cname", sName);

            //update query

            coursesRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(AdminManageCoursesActivity.this, "Changes Applied", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminManageCoursesActivity.this, AdminCoursesActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

    private void displayParticularProductInfo() {

        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    String cName = snapshot.child("cname").getValue().toString();
                    String cDescription = snapshot.child("description").getValue().toString();
                    String cImage = snapshot.child("image").getValue().toString();

                    name.setText(cName);
                    cid.setText(courseID);
                    description.setText(cDescription);

                    // load and display into field ujname
                    Picasso.get().load(cImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}