package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class AddNewLecActivity extends AppCompatActivity {

    private String Pname, lecID, saveCurrentDate, saveCurrentTime, courseRandomKey, fileName, currUser;
    private Button AddNewLectureBtn;
    private ImageView InsertNotesPdf, InsertNotesLink, InsertNotesImage;
    private EditText InputLectureNum, InputNoteName;
    private ProgressDialog loadingBar;

    private static final int GalleryPickIm = 2;
    private static final int GalleryPickPDF = 1;

    private Uri ImageUri, pdfUri;
    private String downloadImageUrl;

    private DatabaseReference CoursesRef;
    private StorageReference LecturesImagesRef;

    private String courseID = "";
    private String docsCount = "1";
    static int LecCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_lec);

        courseID = getIntent().getStringExtra("cid");

        LecturesImagesRef = FirebaseStorage.getInstance().getReference().child("Lectures Content");
        //CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        CoursesRef = FirebaseDatabase.getInstance().getReference();


        InputNoteName = (EditText) findViewById(R.id.input_file_name);

        InsertNotesPdf = (ImageView) findViewById(R.id.insert_pdf_icon);
        InsertNotesLink = (ImageView) findViewById(R.id.insert_link_icon);
        InsertNotesImage = (ImageView) findViewById(R.id.insert_image_icon);

        DrawableCompat.setTint( InsertNotesPdf.getDrawable(), Color.BLACK);
        DrawableCompat.setTint(InsertNotesImage.getDrawable(), Color.BLACK);
        DrawableCompat.setTint( InsertNotesLink.getDrawable(), Color.BLACK);

        AddNewLectureBtn = (Button) findViewById(R.id.add_new_lecture);
        InputLectureNum = (EditText) findViewById(R.id.lecture_num);
        loadingBar = new ProgressDialog(this);

        currUser =  Prevalent.currentOnlineUser.getName();

        InsertNotesPdf.setOnClickListener(new View.OnClickListener() {  // InsertNotesImage
            @Override
            public void onClick(View view) {

                OpenGalleryForImage();
            }
        });


        AddNewLectureBtn.setOnClickListener(new View.OnClickListener() {
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

        Pname =InputLectureNum.getText().toString();
        fileName = InputNoteName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this,"Attaching Notes is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pname)) {
            Toast.makeText(this,"Lecture Number is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fileName)) {
            Toast.makeText(this,"File Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else{ // store these in database

            StoreLectureInformation();
        }
    }


    private void StoreLectureInformation() {

        loadingBar.setTitle("Adding New Lecture");
        loadingBar.setMessage("Please Wait, While New Lecture is Being Added");
        loadingBar.setCanceledOnTouchOutside(false); //if the user clicks on screen dialog box will not disappear until it completes the process
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("h:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        courseRandomKey = saveCurrentDate + saveCurrentTime;

        //fileName = ImageUri.getLastPathSegment();

        StorageReference filePath = LecturesImagesRef.child(ImageUri.getLastPathSegment() + " " + courseRandomKey + ".pdf"); // ".jpg"

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AddNewLecActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
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

                            getSetLectureCount();

                        }
                    }
                });

            }
        });
    }



    private void getSetLectureCount() {

        CoursesRef.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                {
                    if (snapshot.child(courseID).child("LecturesCount").exists())
                    {
                        String slectCount = snapshot.child(courseID).child("LecturesCount").getValue().toString();

                            LecCount = Integer.parseInt(slectCount);
                            LecCount++;
                            lecID = String.valueOf(LecCount);

                            CoursesRef.child("Courses").child(courseID).child("LecturesCount").setValue(lecID);

                            int temp = LecCount - 1;
                            String temps = String.valueOf(temp);

                           /* docsCount = snapshot.child(courseID).child("Lectures")
                                    .child(temps).child(currUser).child("docCount").getValue().toString();

                            docCount = Integer.parseInt(docsCount);
                            docCount++;

                            docsCount = String.valueOf(docCount);*/

                           /* CoursesRef.child("Courses").child(courseID).child("Lectures").
                                        child(temps).child(currUser).child("docCount").setValue(docsCount);*/
                        Save();

                    }
                    else
                    {
                        lecID = "1";

                        HashMap<String, Object> productMap = new HashMap<>();
                        productMap.put("LecturesCount", lecID); //link is in downloadImageUrl

                        //CoursesRef.child("Courses").child(courseID).child("Lectures").child("LecturesCount").setValue(lecID);

                        CoursesRef.child("Courses").child(courseID).updateChildren(productMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            //Toast.makeText(AddNewLecActivity.this,"Lecture count added", Toast.LENGTH_SHORT).show();

                                            Save();
                                        } else {

                                            String message = task.getException().toString();Toast.makeText(AddNewLecActivity.this,
                                                    "Error: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Save() {

        //Log.d("********", "**********");

        CoursesRef.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!(snapshot.child(courseID).child("Lectures").child(lecID).exists()))
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
                                        Toast.makeText(AddNewLecActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
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
                                        //Toast.makeText(AddNewLecActivity.this, "Lectures Successfully Added", Toast.LENGTH_SHORT).show();

                                        SaveLecturessNode();
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNewLecActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(AddNewLecActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void SaveLecturessNode() {

        //Log.d("********", "**********");

        String lkey = "l" + lecID;

        CoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!(snapshot.child("Lecturess").child(courseID).child(lkey).exists()))
                {
                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put("id", lecID); //link is in downloadImageUrl

                    CoursesRef.child("Lecturess").child(courseID).child(lkey).updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(AddNewLecActivity.this, "Lecturesss Successfully Added", Toast.LENGTH_SHORT).show();

                                        finish();
                                        /*Intent intent = new Intent(AddNewLecActivity.this, LecturesHomeActivity.class); //HomeActivity
                                        intent.putExtra("cid", courseID);
                                        startActivity(intent);
                                        finish();*/
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNewLecActivity.this,
                                                "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(AddNewLecActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    private void SaveOrg() {


        //Log.d("********", "**********");

        CoursesRef.child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(courseID).child("Lectures").exists())
                {
                    Toast.makeText(AddNewLecActivity.this, "Lectures Exists", Toast.LENGTH_SHORT).show();
                }
                else {

                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put(fileName, downloadImageUrl); //link is in downloadImageUrl

                    //CoursesRef.child("Courses").child(courseID).child("Lectures").child("LecturesCount").setValue(lecID);

                    if(snapshot.child(courseID).child("Lectures").child(lecID).exists())
                    {
                        Toast.makeText(AddNewLecActivity.this, "Lec id Exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(snapshot.child(courseID).child("Lectures").child(lecID).child(currUser).exists())
                        {
                            Toast.makeText(AddNewLecActivity.this, "user Exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser)
                                    .updateChildren(productMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                loadingBar.dismiss();
                                                Toast.makeText(AddNewLecActivity.this,
                                                        "Lectures Successfully Added", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(AddNewLecActivity.this, HomeActivity.class); //LecHomeActivity
                                                startActivity(intent);
                                            }
                                            else{

                                                loadingBar.dismiss();
                                                String message = task.getException().toString();
                                                Toast.makeText(AddNewLecActivity.this,
                                                        "Error: "+ message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SaveProductInfoToDatabase() {

        //CoursesRef = FirebaseDatabase.getInstance().getReference();

        CoursesRef.child("Courses").child(courseID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!(snapshot.child("Lectures").child(lecID).child(currUser).exists()))
                { //prim key if doesn't exist

                    HashMap<String, Object> productMap = new HashMap<>();
                    productMap.put(fileName, downloadImageUrl); //link is in downloadImageUrl

                    //CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser).setValue(productMap);

                    CoursesRef.child("Courses").child(courseID).child("Lectures").child(lecID).child(currUser).
                            updateChildren(productMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        loadingBar.dismiss();
                                        Toast.makeText(AddNewLecActivity.this,
                                                "Lecture + Notes Successfully Added", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AddNewLecActivity.this, AddNewLecActivity.class); //LecHomeActivity
                                        startActivity(intent);
                                    }
                                    else{

                                        loadingBar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(AddNewLecActivity.this,
                                                "Error: "+ message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(AddNewLecActivity.this, "This "+ lecID +" already exists",
                            Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();

                    Toast.makeText(AddNewLecActivity.this, "Please try again using another Lecture ID",
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