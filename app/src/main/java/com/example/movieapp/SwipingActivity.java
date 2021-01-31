package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SwipingActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    FirebaseFirestore db;
    private GestureDetector gestureDetector;
    ImageView imageView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private static int MIN_DISTANCE = 150;
    private float x1,x2,y1,y2;
    private TextView textViewData;
    //final DocumentReference docRef = db.collection("cities").document("SF");
    private DocumentReference docRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        //Initialize gestureDetector
        this.gestureDetector = new GestureDetector(SwipingActivity.this, this);

        //Initialize database
        db = FirebaseFirestore.getInstance();
        textViewData = findViewById(R.id.text_view_data);

        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        data1.put("regions", Arrays.asList("west_coast", "norcal"));
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        data2.put("regions", Arrays.asList("west_coast", "socal"));
        cities.document("LA").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Washington D.C.");
        data3.put("state", null);
        data3.put("country", "USA");
        data3.put("capital", true);
        data3.put("population", 680000);
        data3.put("regions", Arrays.asList("east_coast"));
        cities.document("DC").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Tokyo");
        data4.put("state", null);
        data4.put("country", "Japan");
        data4.put("capital", true);
        data4.put("population", 9000000);
        data4.put("regions", Arrays.asList("kanto", "honshu"));
        cities.document("TOK").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Beijing");
        data5.put("state", null);
        data5.put("country", "China");
        data5.put("capital", true);
        data5.put("population", 21500000);
        data5.put("regions", Arrays.asList("jingjinji", "hebei"));
        cities.document("BJ").set(data5);


        docRef = db.collection("cities").document("BJ");
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
                    Object s = snapshot.get("country");
                    Object arr = snapshot.get("regions");
                    Log.d("TAG", "this is arr.toString: " + arr.toString());
                    Log.d("TAG", "this is s.tostring: " + s.toString());
                    String text = s.toString();
                    textViewData.setText(text);

                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });

        imageView = findViewById(R.id.imgf);
        Glide.with(this).load("gs://movieapp-5cf6a.appspot.com/Endgame.jpeg").into(imageView);
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

                if(Math.abs(valueX) > MIN_DISTANCE){
                    if(x2 > x1){
                        //Right swipe
                        //loadData();
                        Toast.makeText(this,"Right swipe", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Left swipe
                        Toast.makeText(this,"Left swipe", Toast.LENGTH_SHORT).show();
                    }
                }
        }

        return super.onTouchEvent(event);
    }

//    public void loadData(){
//        docRef.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot.exists()){
//                            //String title = documentSnapshot.getString("country");
//                            //textViewData.setText("Country: " + title);
//                        }else{
//                            Toast.makeText(SwipingActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }

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