package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class HostWaitActivity extends AppCompatActivity {
    private TextView codeTv;
    private Button startButton;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private int activeMembers;
    private int roomSize;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_wait);
        Intent intent = getIntent();
        roomName = intent.getStringExtra(NewRoomActivity.PARTICIPANT_COUNT_TAG);
        codeTv = findViewById(R.id.codeTv);
        codeTv.setText(roomName);
        db = FirebaseFirestore.getInstance();
        startButton = findViewById(R.id.startButton);
        startButton.setActivated(false);

        docRef = db.collection("rooms").document(roomName); //we're referencing the roomName in the rooms collection on firestore
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) { //so we want to constantly be updating the number of activeMembers
                    try {
                        activeMembers = Integer.parseInt(snapshot.get("activeMembers").toString());
                        roomSize = Integer.parseInt(snapshot.get("roomSize").toString());
                    } catch (Exception ex) {
                        Log.e("ERROR", ex.toString());
                    }
                    if(activeMembers >= roomSize) { //constantly checking if the amount of active members reaches the room Size
                        startButton.setActivated(true); //activate the start button
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
        //start button will begin the swiping activity for all participants
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSwipeActivity();
            }
        });

    }
    //this will begin the swiping activity
    public void startSwipeActivity() {
        docRef.update("isSwiping", true); //we begin the swiping phase for the room
        Intent intent = new Intent(this, SwipingActivity.class);
        intent.putExtra(NewRoomActivity.PARTICIPANT_COUNT_TAG, roomName);
        startActivity(intent);
        this.finish(); //close current activity, no longer needed
    }

}