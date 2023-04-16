package com.example.popfun;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popfun.models.FunkoEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

     TextView emailTV;
     Button botonlogout;
     RecyclerView recyclerView;
     List<String> TextoFunko;
     List<Integer> FunkoImage;

     List<FunkoEntity> funkoslist;
     Adapter adapter;
     QuerySnapshot album;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        emailTV = findViewById(R.id.emailTextView);
        botonlogout = findViewById(R.id.blogout);
        recyclerView = findViewById(R.id.recyclerView);
        TextoFunko = new ArrayList<>();
        FunkoImage = new ArrayList<>();

        setup();

        //TextoFunko.add("Eren Jaeger");
        //TextoFunko.add("Asta");
        //TextoFunko.add("Pikachu");
        //TextoFunko.add("Kurama");

        //FunkoImage.add(R.drawable.aot);
        //FunkoImage.add(R.drawable.astablackclover);
        //FunkoImage.add(R.drawable.pikachupokemon);
        //FunkoImage.add(R.drawable.kuramanaruto);


    }

    public void setup(){
        String emailrecibido = getIntent().getStringExtra("email");
        emailTV.setText(emailrecibido);

        botonlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                onBackPressed();

            }
        });

        db.collection("funko").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        album = task.getResult();
                        funkoslist = album.toObjects(FunkoEntity.class);
                        lanzarAdaptador();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void lanzarAdaptador (){
        adapter = new Adapter(this, funkoslist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}