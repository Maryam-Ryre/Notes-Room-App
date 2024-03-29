package com.example.bscs19072_reesnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bscs19072_reesnotes.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Courses;
import Prevalent.Prevalent;
import ViewHolder.CourseViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference CoursesRef;

    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){

            userType = getIntent().getExtras().get("Admin").toString();
        }

        CoursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Courses");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(userType.equals("Admin")) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(HomeActivity.this, AdminAddNewCourse.class); // CartActivity.class
                    startActivity(intent);
                }
            });
        }
        else
        {
            fab.setVisibility(View.INVISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);

        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if(!userType.equals("Admin")){

            userNameTextView.setText(Prevalent.currentOnlineUser.getName());

            //Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Courses> options = new FirebaseRecyclerOptions.Builder<Courses>()
                .setQuery(CoursesRef, Courses.class).build();

        FirebaseRecyclerAdapter<Courses, CourseViewHolder> adapter = new
                FirebaseRecyclerAdapter<Courses, CourseViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull Courses model) {

                        holder.txtCourseName.setText(model.getCname());
                        holder.txtCourseDescription.setText(model.getDescription());
                        holder.txtCourseID.setText(model.getCourseid());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        //l21
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(userType.equals("Admin")){

                                    Intent intent = new Intent(HomeActivity.this, AdminManageCoursesActivity.class);
                                    intent.putExtra("cid", model.getCourseid());
                                    startActivity(intent);
                                }
                                else{

                                    Intent intent = new Intent(HomeActivity.this, LecturesHomeActivity.class); //ProductDetailsActivity.class
                                    intent.putExtra("cid", model.getCourseid());
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        //return null;
                        // access products item layout
                        View view = LayoutInflater.from(parent.getContext()).
                                inflate(R.layout.course_item_layout, parent, false);

                        CourseViewHolder holder = new CourseViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.nav_settings)
        {
            if(!userType.equals("Admin")){

                // for now empty add for later use
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class); //SettingsActivity
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(HomeActivity.this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}