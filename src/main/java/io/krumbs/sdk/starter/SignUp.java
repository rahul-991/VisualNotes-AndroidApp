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

public class SignUp extends AppCompatActivity {
    String username="";
    DatabaseHelper undh = new DatabaseHelper(this);
    Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dropdown = (Spinner)findViewById(R.id.spinner12);
        ArrayList<String> allUserNames = undh.returnUserNames();
        String[] items = new String[allUserNames.size()];
        items = allUserNames.toArray(items);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new YourItemSelectedListener());



    }

    public void goToRegister(View view) {

        Intent i = new Intent(SignUp.this, Register.class);
        startActivity(i);
    }
    public void goToHomePage(View view) {
            if(username == "nothinghasbeenselected"){
                Toast.makeText(SignUp.this, "Please select a class", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(SignUp.this, "Welcome " + username, Toast.LENGTH_LONG).show();


            Intent i = new Intent(SignUp.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            i.putExtras(bundle);
            startActivity(i);
        finish();


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"If you wish to exit \n Press Exit Application", Toast.LENGTH_SHORT).show();
    }

    public void exitApplication(View view) {
        System.exit(0);
    }

    public class YourItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            username = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
            username= "nothinghasbeenselected";

        }
    }
}
