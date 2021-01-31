package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    Button createNewRoomButton;
    EditText code;
    String roomName;
    private FirebaseFirestore db;
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
        db = FirebaseFirestore.getInstance();
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
                        DocumentReference dr = db.collection("rooms").document(roomName);
                        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                startWaitingRoomActivity();
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Room not found.", Toast.LENGTH_LONG);
                            }
                        });


                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, R.string.num_input_error, Toast.LENGTH_LONG);
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, R.string.num_input_error, Toast.LENGTH_LONG);
                    }
                }

            }
        });



    }

    private void startWaitingRoomActivity() {
        Intent intent = new Intent(this, WaitingActivity.class);
        intent.putExtra(NewRoomActivity.ROOM_TAG, roomName);
        startActivity(intent);
    }

    private void startNewRoomActivity() {
        Intent intent = new Intent(this, NewRoomActivity.class);
        startActivity(intent);
    }



}