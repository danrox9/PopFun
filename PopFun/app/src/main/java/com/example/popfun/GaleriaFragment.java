package com.example.popfun;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popfun.models.FunkoEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class GaleriaFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    List<FunkoEntity> funkoslist;
    RecyclerView recyclerView;
    Adapter adapter;
    QuerySnapshot album;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_galeria, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        setup();

    }

    public void setup() {
        db.collection("funko").whereEqualTo("idUsuario", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            album = task.getResult();
                            funkoslist = album.toObjects(FunkoEntity.class);
                            if (funkoslist != null && !funkoslist.isEmpty()) {
                                lanzarAdaptador();
                            }
                        } else {
                            // handle error
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // handle error
                    }
                });
    }

    public void lanzarAdaptador() {
        adapter = new Adapter(getContext(), funkoslist);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        //recyclerView.setLayoutManager(gridLayoutManager);
        //recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}