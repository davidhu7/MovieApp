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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.DocumentType;
import org.w3c.dom.Text;

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
    private TextView title;
    private TextView description;
    private TextView year;
    private int counter = 0;
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
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        year = findViewById(R.id.year);
        imageView = findViewById(R.id.imgf);

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

        loadData();

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
                        counter++;
                        loadData();
                        Toast.makeText(this,"Right swipe", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Left swipe
                        counter++;
                        loadData();
                        Toast.makeText(this,"Left swipe", Toast.LENGTH_SHORT).show();
                    }
                }
        }

        return super.onTouchEvent(event);
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