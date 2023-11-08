package com.example.bscs19072_reesnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


public class Degree extends AppCompatActivity {

    Spinner spinner1;
    Spinner spinner2;
    CheckBox cb_bach;
    CheckBox cb_mast;
    Button done;
    boolean Ba=false;
    boolean Ma=false;
    int case_n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_degree);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        cb_bach = findViewById(R.id.bach);
        cb_mast = findViewById(R.id.mast);
        done = findViewById(R.id.Done);
        String[] bach = new String[]{"BSCS", "BSEDS", "BSMT", "BSEE", "BSCE"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bach);
        spinner1.setAdapter(adapter1);
        String[] mast = new String[]{"MSCS", "MSDS", "MSDEVS", "MSEE"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mast);
        spinner2.setAdapter(adapter2);


        //for checking the level of education//
        cb_bach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_b();
            }
        });

        cb_mast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_m();
            }
        });
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                        Ba=true;
                        case_n=i;
                        break;
                    case 1:
                        Ba=true;
                        case_n=i;
                        break;
                    case 2:
                        Ba=true;
                        case_n=i;
                        break;
                    case 3:
                        Ba=true;
                        case_n=i;
                        break;
                    case 4:
                        Ba=true;
                        case_n=i;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i)
                {
                    case 0:
                        Ma=true;
                        case_n=i;
                        break;
                    case 1:
                        Ma=true;
                        case_n=i;
                        break;
                    case 2:
                        Ma=true;
                        case_n=i;
                        break;
                    case 3:
                        Ma=true;
                        case_n=i;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_bach.isChecked()&& Ba == true)
                {
                    openactivityb(case_n);

                } else if (cb_mast.isChecked()&& Ma==true) {
                    openactivitym(case_n);
                }
            }

        });

    }
    public void validate_b() {
        if (cb_bach.isChecked()) {
            if (cb_mast.isChecked()) {
                cb_mast.setChecked(false);
            }

        }
    }
    public void validate_m() {
        if (cb_mast.isChecked()) {
            if (cb_bach.isChecked()) {
                cb_bach.setChecked(false);
            }

        }
    }
    public void openactivityb(int i)
    {
        switch(i)
        {
            case 0:
                Ba=false;
                Intent intent0 = new Intent(Degree.this, HomeActivity.class);  // BSCS.Class
                startActivity(intent0);
                break;
            case 1:
                Ba=false;
                Intent intent1 = new Intent(Degree.this, HomeActivity.class ); // BSEDS.class
                startActivity(intent1);
                break;
            case 2:
                Ba=false;
                Intent intent2 = new Intent(Degree.this, HomeActivity.class ); // BSMT.class
                startActivity(intent2);
                break;
            case 3:
                Ba=false;
                Intent intent3 = new Intent(Degree.this, HomeActivity.class); // BSCE.class
                startActivity(intent3);
                break;
            case 4:
                Ba=false;
                Intent intent4 = new Intent(Degree.this, HomeActivity.class ); // BSEE.class
                startActivity(intent4);
                break;
        }
    }
    public void openactivitym(int i)
    {
        switch(i)
        {
            case 0:
                Ma=false;
                Intent intent0 = new Intent(Degree.this, HomeActivity.class); // MSCS.class
                startActivity(intent0);
                break;
            case 1:
                Ma=false;
                Intent intent1 = new Intent(Degree.this, HomeActivity.class ); // MSDS.class
                startActivity(intent1);
                break;
            case 2:
                Ma=false;
                Intent intent2 = new Intent(Degree.this, HomeActivity.class ); // MSDEVS.class
                startActivity(intent2);
                break;
            case 3:
                Ma=false;
                Intent intent3 = new Intent(Degree.this, HomeActivity.class); //  MSEE.class
                startActivity(intent3);
                break;
        }
    }
}