package com.example.popfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditarPublicacion extends AppCompatActivity {

    ImageView publicacion;
    EditText tituloE, descripcionE;
    Button actualizar,borrar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_publicacion);
        publicacion = findViewById(R.id.image_view);
        tituloE = findViewById(R.id.titulo);
        descripcionE = findViewById(R.id.descripcion);
        actualizar = findViewById(R.id.actualizar);
        borrar = findViewById(R.id.borrarb);


        // Recuperar los extras del intent
        String texto = getIntent().getStringExtra("texto");
        String descripcion = getIntent().getStringExtra("descripcion");
        String imagen = getIntent().getStringExtra("imagen");

        Glide.with(this)
                .load(imagen)
                .into(publicacion);




        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la referencia al documento que deseas actualizar
                db.collection("funko")
                        .whereEqualTo("imagenes", imagen)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Iterar sobre los resultados de la consulta
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Obtener la referencia al documento que deseas actualizar
                                        DocumentReference docRef = db.collection("funko").document(document.getId());

                                        // Actualizar el contenido del EditText en la base de datos
                                        String nuevotitulo = tituloE.getText().toString();
                                        String nuevodescripcion = descripcionE.getText().toString();
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("textos", nuevotitulo);
                                        updates.put("descripcion", nuevodescripcion);
                                        docRef.update(updates);
                                        Intent intent = new Intent(EditarPublicacion.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });


            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("funko")
                        .whereEqualTo("imagenes", imagen)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // Iterar sobre los resultados de la consulta
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Obtener la referencia al documento que deseas actualizar
                                        DocumentReference docRef = db.collection("funko").document(document.getId());

                                        docRef.delete();
                                        Intent intent = new Intent(EditarPublicacion.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
            }
        });

    }
}