package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EndActivity extends AppCompatActivity {
    int winningIndex;
    private DocumentReference docRef;
    FirebaseFirestore db;
    private TextView title;
    private TextView description;
    private TextView year;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        title = findViewById(R.id.Endtitle);
        description = findViewById(R.id.Enddescription);
        year = findViewById(R.id.Endyear);
        imageView = findViewById(R.id.endImg);
        winningIndex = intent.getIntExtra(SwipingActivity.WINNING_INDEX_TAG,0);
        displayData();
    }

    public void displayData(){
        String collection = "movie"+winningIndex;
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
                            title.setText("Synopsis: " + descriptionO);
                            description.setText(titleO);
                            year.setText("Year: " + yearO);
                            Object url1 = documentSnapshot.get("Image");
                            String urlO = url1.toString();
                            Glide.with(EndActivity.this).load(urlO).into(imageView);
                        }else{
                            Toast.makeText(EndActivity.this,"Document does not exist",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }




}