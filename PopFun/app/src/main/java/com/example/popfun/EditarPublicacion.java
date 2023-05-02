package com.example.popfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditarPublicacion extends AppCompatActivity {

    ImageView publicacion;
    EditText tituloE, descripcionE;
    Button actualizar,borrar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String storage_path = "funko/*";
    String photo ="photo";
    String userId = FirebaseAuth.getInstance().getUid();


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
                Dialog dialog = new Dialog(EditarPublicacion.this);
                dialog.setContentView(R.layout.confirmareditar);
                Button dialogButton = dialog.findViewById(R.id.dialog_button);
                Button dialogButton2 = dialog.findViewById(R.id.dialog_button2);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.fondo_degradado);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 800; // duplicar el ancho actual
                lp.height = 800; // duplicar la altura actual
                dialog.getWindow().setAttributes(lp);
                dialogButton.setOnClickListener(new View.OnClickListener() {
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
                                                updates.put("textos", nuevotitulo.toUpperCase());
                                                updates.put("descripcion", nuevodescripcion);
                                                docRef.update(updates);
                                                Intent intent = new Intent(EditarPublicacion.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }
                });
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();

                    }
                });
                dialog.show();
            }

        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(EditarPublicacion.this);
                dialog.setContentView(R.layout.confirmarborrado);
                Button dialogButton = dialog.findViewById(R.id.dialog_button);
                Button dialogButton2 = dialog.findViewById(R.id.dialog_button2);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.fondo_degradado);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 800; // duplicar el ancho actual
                lp.height = 800; // duplicar la altura actual
                dialog.getWindow().setAttributes(lp);
                dialogButton.setOnClickListener(new View.OnClickListener() {
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
                                                // Obtener el token de la imagen
                                                String token = document.getString("token");

                                                // Obtener una referencia al archivo en Firebase Storage
                                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                                StorageReference fileRef = storageRef.child(token);

                                                // Borrar el archivo
                                                fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Actualizar el documento para borrar el campo "imagenes"
                                                        DocumentReference docRef = db.collection("funko").document(document.getId());
                                                        Map<String, Object> updates = new HashMap<>();
                                                        updates.put("imagenes", "");
                                                        docRef.update(updates);

                                                        Intent intent = new Intent(EditarPublicacion.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Manejar el error
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                        dialog.dismiss();
                    }
                });
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

    }
}