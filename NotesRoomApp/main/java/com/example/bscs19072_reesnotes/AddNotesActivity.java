package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import Prevalent.Prevalent;

public class AddNotesActivity extends AppCompatActivity {

    private String saveCurrentDate, saveCurrentTime, courseRandomKey, fileName, currUser;

    private Button AddBtn;
    private ImageView InsertNotesPdf, InsertNotesLink, InsertNotesImage;
    private TextView InputLectureNum;
    private ProgressDialog loadingBar;
    private EditText InputNoteName;
    private static final int GalleryPickIm = 2;
    private static final int GalleryPickPDF = 1;

    private Uri ImageUri, pdfUri;
    private String downloadImageUrl;

    private DatabaseReference CoursesRef;
    private StorageReference LecturesImagesRef;

    private String courseID = "", lecID;
    private String docsCount = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        courseID = getIntent().getStringExtra("cid");
        lecID = getIntent().getStringExtra("lid");
        currUser =  Prevalent.currentOnlineUser.getName();

        LecturesImagesRef = FirebaseStorage.getInstance().getReference().child("Lectures Content");
        //CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        CoursesRef = FirebaseDatabase.getInstance().getReference();


        InputNoteName = (EditText) findViewById(R.id.in_file_name);

        InputLectureNum = (TextView) findViewById(R.id.selected_lecture_num);
        InsertNotesPdf = (ImageView) findViewById(R.id.insert_pdf_icon2);
        InsertNotesLink = (ImageView) findViewById(R.id.insert_link_icon2);
        InsertNotesImage = (ImageView) findViewById(R.id.insert_image_icon2);
        AddBtn = (Button) findViewById(R.id.add_notes_lecture);

        DrawableCompat.setTint( InsertNotesPdf.getDrawable(), Color.BLACK);
        DrawableCompat.setTint(InsertNotesImage.getDrawable(), Color.BLACK);
        DrawableCompat.setTint( InsertNotesLink.getDrawable(), Color.BLACK);

        loadingBar = new ProgressDialog(this);

        InputLectureNum.setText("Add notes for Lecture # " + lecID);

        InsertNotesPdf.setOnClickListener(new View.OnClickListener() {  // InsertNotesImage
            @Override
            public void onClick(View view) {

                OpenGalleryForImage();
            }
        });

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidateLectureData();

            }
        });
    }

    private void OpenGalleryForImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf"); //"image/*"
        startActivityForResult(galleryIntent, GalleryPickIm);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPickIm && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();

            //InsertNotesImage.setImageURI(ImageUri); //display image uri on image view
            //DrawableCompat.setTint(InsertNotesImage.getDrawable(), Color.GREEN);
            DrawableCompat.setTint(InsertNotesPdf.getDrawable(), Color.GREEN);
        }
    }

    private void ValidateLectureData(){

        fileName = InputNoteName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this,"Attaching Notes is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fileName )) {
            Toast.makeText(this,"File Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else{ // store these in database

            StoreLectureInformation();
        }
    }


    private void StoreLectureInformation() {

        loadingBar.setTitle("Adding New Note");
        loadingBar.setMessage("Please Wait, While New Lecture is Being Added");
        loadingBar.setCanceledOnTouchOutside(false); //if the user clicks on screen dialog box will not disappear until it completes the process
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        courseRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = LecturesImagesRef.child(ImageUri.getLastPathSegment() + " " + courseRandomKey + ".pdf"); // ".jpg"

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AddNotesActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // to firebase storage


                Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        //return null;

                        if (!task.isSuccessful()){
                            // loadingBar.dismiss();
                            throw task.getException();
                        }

                        // get image url
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString(); // to get link or the url

                            Save();

                        }
                    }
                });

            }
        });
    }


    private void Save() {

        //Log.d("********", "**********");

        CoursesRef.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if((snapshot.child(courseID).child("Lectures").child(lecID).child(currUser).exists()))
                {
                    docsCount = snapshot.child(courseID).child("Lectures")
                            .child(lecID).child(currUser).child("docCount").getValue().toString();

                    int dCount = Integer.parseInt(docsCount);
                    dCount++;

                    docsCount = String.valueOf(dCount);

                    CoursesRef.child("Courses").child(courseID).child("Lectures").
                            child(lecID).child(currUser).child("docCount").setValue(docsCount);

                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("pdf", downloadImageUrl); //link is in downloadImageUrl
                    productMap.put("name", fileName); //link is in downloadImageUrl

                    CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser).
                            child("docs").child(docsCount)
                            .updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(AddNotesActivity.this,
                                                "Notes Successfully Added", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AddNotesActivity.this, Notes.class); //LecturesHomeActivity
                                        intent.putExtra("cid", courseID);
                                        intent.putExtra("lid", lecID);
                                        intent.putExtra("authname", currUser);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNotesActivity.this,
                                                "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("author", currUser); //link is in downloadImageUrl
                    productMap.put("docCount", docsCount);

                    CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser)
                            .updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        //Toast.makeText(AddNewLecActivity.this, "Lectures Successfully Added", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNotesActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    HashMap<String, Object> productMap2 = new HashMap<>();
                    productMap2.put("pdf", downloadImageUrl); //link is in downloadImageUrl
                    productMap2.put("name", fileName); //link is in downloadImageUrl

                    CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser).
                            child("docs").child(docsCount)
                            .updateChildren(productMap2)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(AddNotesActivity.this,
                                                "Notes Successfully Added", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AddNotesActivity.this, Notes.class); //LecturesHomeActivity
                                        intent.putExtra("cid", courseID);
                                        intent.putExtra("lid", lecID);
                                        intent.putExtra("authname", currUser);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNotesActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    //Toast.makeText(AddNotesActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}