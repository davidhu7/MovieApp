package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SwipingActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    FirebaseFirestore db;
    private GestureDetector gestureDetector;
    ImageView imageView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static int MIN_DISTANCE = 150;
    private float x1,x2,y1,y2;
    private TextView title;
    private TextView description;
    private TextView year;
    private int counter = 0;
    //final DocumentReference docRef = db.collection("cities").document("SF");
    private DocumentReference docRef;
    private DocumentReference roomRef;
    private String roomName;
    private int activeMembers;
    private int roomSize;
    private List<Long> movieVoteCount;
    private int movieCount;

    public final static String WINNING_INDEX_TAG = "com.example.movieapp.WINNING_INDEX_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        //Initialize gestureDetector
        this.gestureDetector = new GestureDetector(SwipingActivity.this, this);
        //Initialize database
        db = FirebaseFirestore.getInstance();
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        year = findViewById(R.id.year);
        imageView = findViewById(R.id.imgf);
        movieVoteCount = new ArrayList<>();

//        //count num movies
//        db.collection("movies")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            movieCount = 0;
//                            for (DocumentSnapshot document : task.getResult()) {
//                                movieCount++;
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

//        for(int i = 0; i < movieCount; i++) {
//            movieVoteCount.add((long)0);
//        }

        Intent intent = getIntent();
        roomName = intent.getStringExtra(NewRoomActivity.ROOM_TAG); //getting room name from prev activity


        loadData();

        roomRef = db.collection("rooms").document(roomName);
        roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) { //so we want to constantly be updating the votes
                    try {
                        //check if any of the values in the array
                        Object rSize = snapshot.get("roomSize");
                        Object voteCarr = snapshot.get("voteCountArray");

                        roomSize = Integer.parseInt(rSize.toString());
                        movieVoteCount = fromStringToArray(voteCarr.toString());
                        if((boolean) snapshot.get("isEnded")) {
                            //end activity
                            db.collection("rooms").document(roomName)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("TAG", "Error deleting document", e);
                                        }
                                    });
                            startEndActivity();

                        }
                    } catch (Exception ex) {
                        Log.e("ERROR", ex.toString());
                    }

                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });

        /*
        docRef = db.collection("movies").document("movie0");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Current data: " + snapshot.getData());
                    Object description1 = snapshot.get("Description");
                    Object title1 = snapshot.get("Title");
                    Object year1 = snapshot.get("Year");
                    String descriptionO = description1.toString();
                    String titleO = title1.toString();
                    String yearO = year1.toString();
                    title.setText("Synopsis: " + descriptionO);
                    description.setText(titleO);
                    year.setText("Year: " + yearO);
                    Object url1 = snapshot.get("Image");
                    String urlO = url1.toString();
                    Glide.with(SwipingActivity.this).load(urlO).into(imageView);
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });

         */
        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/movieapp-5cf6a.appspot.com/o/Endgame.jpeg?alt=media&token=1edd8184-d583-4cdb-8d8c-7115607e446e").into(imageView);
    }

    ArrayList<Long> fromStringToArray(String s) {
        ArrayList<Long> list = new ArrayList<>();
        s = s.substring(1, s.length() - 1);
        String[] arr = s.split(", ");
        for(int i = 0; i < arr.length; i++) {
            list.add(Long.parseLong(arr[i]));
        }
        return list;
    }


    //override on touch event


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                //Value for horizontal swipe
                float valueX = x2 - x1;
                float valueY = y2 - y1;
                Log.d("TAG", movieVoteCount.toString());
                if(Math.abs(valueX) > MIN_DISTANCE){
                    if(x2 > x1){
//                        movieVoteCount.set(counter, movieVoteCount.get(counter) + 1);
////                        if(movieVoteCount.get(counter) >= roomSize / 2) { //if the number of votes at the current movie counter becomes greater than half the room
////                            roomRef.update("isEnded", true); //the search for a movie ends
//                        }
//                        roomRef.update("voteCountArray", movieVoteCount);
                        //Right swipe
                        //loadData();

                        counter++;
                        loadData();

                        Toast.makeText(this,"Right swipe", Toast.LENGTH_SHORT).show();
                    }
                    else{
//                        if(movieVoteCount.get(counter) >= roomSize / 2) { //if the number of votes at the current movie counter becomes greater than half the room
//                            roomRef.update("isEnded", true); //the search for a movie ends
//                        }
                        //Left swipe
                        counter++;
                        loadData();
                        Toast.makeText(this,"Left swipe", Toast.LENGTH_SHORT).show();
                    }
                }
        }

        return super.onTouchEvent(event);
    }

    public void startEndActivity() {
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra(WINNING_INDEX_TAG, counter);
        startActivity(intent);
        this.finish();
    }

    public void startBackupEndActivity() {

    }


    public void loadData(){
        String collection = "movie"+counter;
        docRef = db.collection("movies").document(collection);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Object description1 = documentSnapshot.get("Description");
                            Object title1 = documentSnapshot.get("Title");
                            Object year1 = documentSnapshot.get("Year");
                            String descriptionO = description1.toString();
                            String titleO = title1.toString();
                            String yearO = year1.toString();
                            title.setText(titleO);
                            description.setText("Synopsis: " + descriptionO);
                            year.setText("Year: " + yearO);
                            Object url1 = documentSnapshot.get("Image");
                            String urlO = url1.toString();
                            Glide.with(SwipingActivity.this).load(urlO).into(imageView);
                        }else{
                            startEndActivity();
                            Toast.makeText(SwipingActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                  }
               });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}