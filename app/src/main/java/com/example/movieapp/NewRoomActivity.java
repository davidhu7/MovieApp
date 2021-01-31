package com.example.movieapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewRoomActivity extends AppCompatActivity {
    private Button createButton;
    private EditText numParticipants;
    private int participantCount;
    private String roomName;
    FirebaseFirestore db;
    CollectionReference rooms;
    public final String PARTICIPANT_COUNT_TAG = "com.example.movieapp.PARTICIPANT_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        createButton = findViewById(R.id.createRoomButton);
        numParticipants = findViewById(R.id.editTextParticipants);
        createButton.setActivated(false); //false by default
        db = FirebaseFirestore.getInstance();
        rooms = db.collection("rooms"); //we're creating documents in the "rooms" collection


        numParticipants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = numParticipants.getText().toString();
                try {
                    int num =  Integer.parseInt(text);
                    createButton.setActivated(true); //activate the button, since the text is a button
                    participantCount = num;

                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });



    }

    //this should start the waiting activity for the host
//    private void startWaitActivity() {
//        Intent intent = new Intent(this, HostWaitActivity.class);
//        intent.putExtra(PARTICIPANT_COUNT_TAG, roomName);
//        startActivity(intent);
//    }

    //this method creates all the data we need for a new room on FireStore.
    private void createNewRoomOnDatabase() {
        int code = new Random().nextInt(999999); //generate random 6-digit user room key
        roomName = String.valueOf(code); //transform into a string for use
        Map<String, Object> data = new HashMap<>();
        data.put("roomSize", participantCount);
        data.put("activeMembers", 0);
        //TODO: Implement the movies index into the room
        rooms.document(roomName).set(data);
    }
}