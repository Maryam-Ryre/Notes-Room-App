package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Courses;
import Model.Lectures;
import Prevalent.Prevalent;
import ViewHolder.CourseViewHolder;
import ViewHolder.LectureNumViewHolder;

public class LecturesHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private String courseID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures_home);

        courseID = getIntent().getStringExtra("cid");

        recyclerView = findViewById(R.id.lectures_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Log.d("********", "**********");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LecturesHomeActivity.this, AddNewLecActivity.class); // CartActivity.class
                intent.putExtra("cid", courseID);
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onStart() {

        super.onStart();

        final DatabaseReference LecturessRef = FirebaseDatabase.getInstance().getReference().child("Lecturess");


        FirebaseRecyclerOptions<Lectures> options = new FirebaseRecyclerOptions.Builder<Lectures>()
                .setQuery(LecturessRef.child(courseID), Lectures.class).build();

        //Toast.makeText(LecturesHomeActivity.this, "HEHEHEHEHE", Toast.LENGTH_SHORT).show();


        FirebaseRecyclerAdapter<Lectures, LectureNumViewHolder> adapter = new
                FirebaseRecyclerAdapter<Lectures, LectureNumViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull LectureNumViewHolder holder, int position, @NonNull Lectures model) {

                        holder.txtLecNo.setText("Lecture # " + model.getId() + "   ");

                        //l21
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(LecturesHomeActivity.this, AuthorsHomeActivity.class); // AddNotesActivity
                                intent.putExtra("cid", courseID);
                                intent.putExtra("lid", model.getId());
                                startActivity(intent);
                                //finish();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public LectureNumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //return null;
                        // access products item layout
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.lectures_items_layout, parent, false);

                        LectureNumViewHolder holder = new LectureNumViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }




   /* @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference lecListRef = FirebaseDatabase.getInstance().getReference().child("Lecturess");

        FirebaseRecyclerOptions<Lectures> options = new FirebaseRecyclerOptions.Builder<Lectures>()
                .setQuery(lecListRef.child(courseID), Lectures.class).build();


        FirebaseRecyclerAdapter<Lectures, LectureNumViewHolder> adapter
                = new FirebaseRecyclerAdapter<Lectures, LectureNumViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LectureNumViewHolder holder, int position, @NonNull Lectures model) {

                Toast.makeText(LecturesHomeActivity.this, "HEHEHEHEHE", Toast.LENGTH_SHORT).show();



                holder.txtLecNo.setText("Lecture # " + model.getId() + "   ");

                //adapter gets products line by line
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(LecturesHomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public LectureNumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.lectures_items_layout, parent, false);

                LectureNumViewHolder holder = new LectureNumViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }*/
}