package com.example.bscs19072_reesnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.NotesC;
import ViewHolder.DocsViewHolder;

public class Notes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String courseID = "";
    private String lectureID = "";
    private String Aname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        courseID = getIntent().getStringExtra("cid");
        lectureID = getIntent().getStringExtra("lid");
        Aname = getIntent().getStringExtra("authname");

        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    protected void onStart() {

        super.onStart();

        final DatabaseReference CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");


        FirebaseRecyclerOptions<NotesC> options = new FirebaseRecyclerOptions.Builder<NotesC>()
                .setQuery(CoursesRef.child(courseID).
                        child("Lectures").child(lectureID).child(Aname).child("docs"), NotesC.class).build();

        FirebaseRecyclerAdapter<NotesC, DocsViewHolder> adapter = new
                FirebaseRecyclerAdapter<NotesC, DocsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DocsViewHolder holder, int position, @NonNull NotesC model) {

                        holder.txtDocName.setText(model.getName());

                        //l21
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(Notes.this, oneNoteView.class); //ViewAllNotes
                                intent.putExtra("pdf_url", model.getPdf());
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DocsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //return null;
                        // access products item layout
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.notes_items_layout, parent, false);

                        DocsViewHolder holder = new DocsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}