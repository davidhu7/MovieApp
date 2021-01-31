package com.example.movieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MainActivity extends AppCompatActivity {

    Button createNewRoomButton;
    EditText code;
    String roomName;
    private FireBaseFireStore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNewRoomButton = findViewById(R.id.createNewRoomButton);
        createNewRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewRoomActivity();
            }
        });

        code=findViewById(R.id.editTextPin);
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                roomName = code.getText().toString();
                if(roomName.length() == 6) {
                    try {
                        int n = Integer.parseInt(roomName);


                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, R.string.num_input_error, Toast.LENGTH_LONG);
                    }
                }

            }
        });



    }

    private void startNewRoomActivity() {
        Intent intent = new Intent(this, NewRoomActivity.class);
        startActivity(intent);
    }



}