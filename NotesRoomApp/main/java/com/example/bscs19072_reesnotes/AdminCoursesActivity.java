package com.example.bscs19072_reesnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminCoursesActivity extends AppCompatActivity {

    private Button LogoutBtn, manageCoursesBtn, addCourseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_courses);

        LogoutBtn = (Button)findViewById(R.id.admin_logout_btn);
        manageCoursesBtn = (Button)findViewById(R.id.admin_manage_course_btn);
        addCourseBtn = (Button)findViewById(R.id.admin_add_course_btn);

        manageCoursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCoursesActivity.this, HomeActivity.class); // HomeActivity
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCoursesActivity.this, MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCoursesActivity.this, AdminAddNewCourse.class);
                startActivity(intent);
            }
        });


    }
}