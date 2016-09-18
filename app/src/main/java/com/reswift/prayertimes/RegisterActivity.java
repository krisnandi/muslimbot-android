package com.reswift.prayertimes;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    String TAG = "testing";

    EditText registerEmail;
    EditText registerPassword1;
    EditText registerPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerPassword1 = (EditText) findViewById(R.id.registerPassword1);
        registerPassword2 = (EditText) findViewById(R.id.registerPassword2);

        Button registerCreate = (Button) findViewById(R.id.registerCreate);
        registerCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = (registerPassword1.getText().toString().equals(registerPassword2.getText().toString()));
                Log.d(TAG, "regisster: " + registerEmail.getText() + " - " + registerPassword1.getText() + " - " + registerPassword2.getText() + " - " + check);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("email", registerEmail.getText().toString());
                returnIntent.putExtra("password", registerPassword1.getText().toString());
                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
