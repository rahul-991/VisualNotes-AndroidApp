package io.krumbs.sdk.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



import java.util.ArrayList;

import io.krumbs.sdk.starter.R;

public class Register extends AppCompatActivity {
    DatabaseHelper db = new DatabaseHelper(this);

    EditText passWord;

    String username;
    String password;

    Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String[] items = {"AERO ENG", "AFAM STUDIES", "ANTHRO", "ART", "ART HISTORY","ASAM STUDIES"," BIO CHEM",
        "BIO","BUSINESS", "CHEM", "CHINSES STUDIES", "CIVIL ENGINEERING", "CLASSICS","COG SCI","LIT", "CGS","CS","CSE",
        "CRIM", "DANCE", " DATA SCIENCE","DRAMA", "ECON", "EDUCATION","ENG","ENGLISH","EECS","ENVIROMENTAL SCI","EUROPEAN STD","FILM","FRENCH",
        "GEETICS","GERMAN STDS","GLOBAL CLULTURES","HIS","INF","MATH","MUSIC","PHIL","PHYSICS",
        "POLI SCI","PSYC","SOCIOLOGY","SPANISH","SE","SPANISH"};
        passWord = (EditText) findViewById(R.id.editTextCourseNum);
        dropdown = (Spinner) findViewById(R.id.spinnerUsername1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new YourItemSelectedListener2());
    }


    public void buttonRegister(View view) {

        if(username.equals("")){
            Toast.makeText(this, "Course name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        password = passWord.getText().toString();
        if(password.equals("")){
            Toast.makeText(this, "Course Number cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }
        username = username+password;
        boolean test = db.insertData(username);
        if(test){
            Toast.makeText(this, "Congrats, Class Information is inserted", Toast.LENGTH_SHORT).show();
        }



    }

    public void buttonHomePage(View view) {

        Intent i = new Intent(Register.this,SignUp.class);
        startActivity(i);
    }
    public class YourItemSelectedListener2 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            username = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
            username= "nothinghasbeenselected";

        }
    }
}
