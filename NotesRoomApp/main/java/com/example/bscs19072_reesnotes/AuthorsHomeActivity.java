package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.AuthorsC;
import Model.Lectures;
import ViewHolder.AuthoreViewHolder;
import ViewHolder.LectureNumViewHolder;

public class AuthorsHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String courseID = "";
    private String lectureID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors_home);

        courseID = getIntent().getStringExtra("cid");
        lectureID = getIntent().getStringExtra("lid");

        recyclerView = findViewById(R.id.authors_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AuthorsHomeActivity.this, AddNotesActivity.class); // CartActivity.class
                intent.putExtra("cid", courseID);
                intent.putExtra("lid", lectureID );

                startActivity(intent);
            }
        });
    }


    protected void onStart() {

        super.onStart();

        final DatabaseReference CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");


        FirebaseRecyclerOptions<AuthorsC> options = new FirebaseRecyclerOptions.Builder<AuthorsC>()
                .setQuery(CoursesRef.child(courseID).
                        child("Lectures").child(lectureID), AuthorsC.class).build();

        //Toast.makeText(AuthorsHomeActivity.this, "HEHEHEHEHE", Toast.LENGTH_SHORT).show();


        FirebaseRecyclerAdapter<AuthorsC, AuthoreViewHolder> adapter = new
                FirebaseRecyclerAdapter<AuthorsC, AuthoreViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AuthoreViewHolder holder, int position, @NonNull AuthorsC model) {

                        holder.txtAuthName.setText(model.getAuthor());

                        //l21
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(AuthorsHomeActivity.this, Notes.class); //ViewAllNotes
                                intent.putExtra("cid", courseID);
                                intent.putExtra("lid", lectureID);
                                intent.putExtra("authname", model.getAuthor());
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AuthoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //return null;
                        // access products item layout
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.authors_items_layout, parent, false);

                        AuthoreViewHolder holder = new AuthoreViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}