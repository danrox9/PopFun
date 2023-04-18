package com.example.popfun;

import static android.app.Activity.RESULT_OK;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class UsuarioFragment extends Fragment {

    Button botonGuardarCambios;

    ImageView imgperfil, subirfoto, borrarfoto;
    EditText firstname;
    EditText lastname;
    EditText nickname;
    EditText email;
    TextView texto;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    StorageReference storageReference;
    String storage_path = "perfil/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String photo ="photo";
    String idd;

    HomeActivity homeactivity = new HomeActivity();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usuario, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        imgperfil=view.findViewById(R.id.imagePerfil);
        subirfoto=view.findViewById(R.id.ivcamara);
        borrarfoto=view.findViewById(R.id.ivbasura);
        botonGuardarCambios=view.findViewById(R.id.buser);
        firstname=view.findViewById(R.id.firstnameuser);
        lastname=view.findViewById(R.id.lastnameuser);
        nickname=view.findViewById(R.id.nicknameuser);
        email=view.findViewById(R.id.emailuser);
        email.setEnabled(false);
        texto=view.findViewById(R.id.textView);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        Drawable customBackground = getResources().getDrawable(R.drawable.fondo_degradado,activity.getTheme());
        actionBar.setBackgroundDrawable(customBackground);

        setup();
        eliminar_foto();
        subir_foto();
        subir_foto2();
        getUser();
        pintar_imagen_camara(view);
        pintar_imagen_basura(view);


    }

    public void setup() {

        // Obtener la referencia de la colección "usuarios" en Firestore
        CollectionReference usuariosRef = db.collection("users");

        // Obtener el documento del usuario actual utilizando su ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuariosRef.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Obtener los datos del documento

                        String Nickname = document.getString("Nickname");
                        String FirstName = document.getString("First Name");
                        String LastName = document.getString("Last Name");

                        // Mostrar los datos en los EditText
                        texto.setText(FirstName+" "+LastName);
                        nickname.setText(Nickname);
                        firstname.setText(FirstName);
                        lastname.setText(LastName);
                        email.setText(userId);


                    } else {
                        // El documento no existe
                    }
                } else {
                    // Error al leer el documento
                }
            }
        });

        botonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la referencia de la colección "usuarios" en Firestore
                CollectionReference usuariosRef = db.collection("users");

                // Obtener el documento del usuario actual utilizando su ID
                String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                usuariosRef.document(userId).update("Nickname", nickname.getText().toString());
                usuariosRef.document(userId).update("Last Name", lastname.getText().toString());
                usuariosRef.document(userId).update("First Name", firstname.getText().toString());
                usuariosRef.document(userId).update("Email", email.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }


    public void getUser(){
        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String photoUser = documentSnapshot.getString("photo");

                try {
                    if (!photoUser.equals("")){
                        Picasso.with(getContext())
                                .load(photoUser)
                                .resize(150,150)
                                .into(imgperfil);
                    }
                }catch (Exception e){
                    Log.v("Error", "0: " + e);
                }
            }
        });
    }

    public void eliminar_foto() {
        borrarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference usuariosPhotoRef = db.collection("users").document(userId);
                usuariosPhotoRef.update("photo", FieldValue.delete());
                Drawable drawable = getResources().getDrawable(R.drawable.persona);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                imgperfil.setImageBitmap(bitmap);
            }
        });

    }

    public void subir_foto(){
        subirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegir_foto();
            }
        });
    }
    public void subir_foto2(){
        imgperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elegir_foto();
            }
        });
    }
    public void elegir_foto(){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,COD_SEL_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                image_url = data.getData();
                subirPhoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void subirPhoto(Uri image_url){
        String rute_storage_photo = storage_path + "" + photo + "" + userId + "" + idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("photo",download_uri);
                            db.collection("users").document(userId).update(map);
                            getUser();

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    public void pintar_imagen_camara(View view){
        ImageView myImageView = view.findViewById(R.id.ivcamara);
        Drawable drawable = myImageView.getDrawable();
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        myImageView.setImageDrawable(drawable);
    }
    public void pintar_imagen_basura(View view){
        ImageView myImageView = view.findViewById(R.id.ivbasura);
        Drawable drawable = myImageView.getDrawable();
        drawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));
        myImageView.setImageDrawable(drawable);
    }

}