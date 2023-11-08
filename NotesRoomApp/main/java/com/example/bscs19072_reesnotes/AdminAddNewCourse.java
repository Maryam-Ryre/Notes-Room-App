package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewCourse extends AppCompatActivity {

    private String Description, cID, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewCourseBtn;
    private ImageView InputCourseImage;
    private EditText InputCourseName, InputCourseDescription, InputCourseID;
    private static final int GalleryPick = 1;
    private Uri ImageUri;

    private String courseRandomKey, downloadImageUrl;
    private ProgressDialog loadingBar;

    private StorageReference CourseImagesRef;
    private DatabaseReference CoursesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_course);

        CourseImagesRef = FirebaseStorage.getInstance().getReference().child("Course Images");
        //CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        CoursesRef = FirebaseDatabase.getInstance().getReference();

        AddNewCourseBtn = (Button) findViewById(R.id.add_new_course);
        InputCourseImage = (ImageView) findViewById(R.id.select_product_image);
        InputCourseName = (EditText) findViewById(R.id.course_name);
        InputCourseDescription = (EditText) findViewById(R.id.course_description);
        InputCourseID = (EditText) findViewById(R.id.course_ID);

        loadingBar = new ProgressDialog(this);


        InputCourseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenGallery();
            }
        });

        AddNewCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidateCourseData();

            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            //display image uri on image view
            InputCourseImage.setImageURI(ImageUri);
        }
    }

    private void ValidateCourseData(){

        Description = InputCourseDescription.getText().toString();
        cID = InputCourseID.getText().toString();
        Pname = InputCourseName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this,"Course Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)) {
            Toast.makeText(this,"Course Description is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cID)) {
            Toast.makeText(this,"Course ID is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname)) {
            Toast.makeText(this,"Course Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else{ // store these in database

            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Adding New Course");
        loadingBar.setMessage("Admin Please Wait, While New Course is Being Added");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        // random key combine time and date
        ////////courseRandomKey = saveCurrentDate + saveCurrentTime;

        courseRandomKey = cID;

        // CourseImagesRef = FirebaseStorage.getInstance().getReference().child("Course Images");
        // CoursesRef = FirebaseDatabase.getInstance().getReference();

        StorageReference filePath = CourseImagesRef.child(ImageUri.getLastPathSegment()
                + " " + courseRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddNewCourse.this, "Error: "+message, Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // to firebase storage

                //Toast.makeText(AdminAddNewProductActivity.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        //return null;

                        if (!task.isSuccessful()){
                            // loadingBar.dismiss();
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            /*Toast.makeText(AdminAddNewProductActivity.this, "Got Product Image" +
                                    " successfully ", Toast.LENGTH_SHORT).show();*/

                            SaveProductInfoToDatabase();

                        }
                    }
                });

            }
        });
    }

    private void SaveProductInfoToDatabase() {

        //CoursesRef = FirebaseDatabase.getInstance().getReference();

        CoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!(snapshot.child("Courses").child(cID).exists())){ //prim key if doesn't exist

                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("courseid", cID);
                    //productMap.put("date", saveCurrentDate);
                    //productMap.put("time", saveCurrentTime);
                    productMap.put("description", Description);
                    productMap.put("image", downloadImageUrl); //link is in downloadImageUrl
                    productMap.put("cname", Pname);

                   CoursesRef.child("Courses").child(cID).updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(AdminAddNewCourse.this,
                                                "Course Successfully Added", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AdminAddNewCourse.this, AdminCoursesActivity.class);
                                        startActivity(intent);
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AdminAddNewCourse.this,
                                                "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(AdminAddNewCourse.this, "This "+ cID +" already exists",
                            Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();

                    Toast.makeText(AdminAddNewCourse.this, "Please try again using another Course ID",
                            Toast.LENGTH_SHORT).show();

                    //Intent intent = new Intent(AdminAddNewCourse.this, MainActivity2.class);
                    //startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}