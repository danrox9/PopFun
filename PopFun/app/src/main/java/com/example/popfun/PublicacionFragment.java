package com.example.popfun;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PublicacionFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference funkosRef = db.collection("funko");

    String userId = FirebaseAuth.getInstance().getUid();
    String userIdUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    Button subirpubli;
    EditText titulopubli ,descripcionpubli;
    ImageView imagenpubli;
    StorageReference storageReference;
    String storage_path = "funko/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String photo ="photo";
    int contadorPhoto = 0;

    Map<String, Object> textoData = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publicacion, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference  = FirebaseStorage.getInstance().getReference();
        subirpubli = view.findViewById(R.id.subirpublicacion);
        titulopubli = view.findViewById(R.id.titulo);
        descripcionpubli = view.findViewById(R.id.descripcion);
        imagenpubli = view.findViewById(R.id.image_view);

        getNumFunkoObjects();
        subirpublicacion(view);

    }

    public void subirpublicacion(View view){

        subirpubli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.confirmacion);
                Button dialogButton = dialog.findViewById(R.id.dialog_button);
                Button dialogButton2 = dialog.findViewById(R.id.dialog_button2);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.fondo_degradado);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 800; // duplicar el ancho actual
                lp.height = 800; // duplicar la altura actual
                dialog.getWindow().setAttributes(lp);
                String titulo = titulopubli.getText().toString();
                String descripcion = descripcionpubli.getText().toString();

                if(TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descripcion)){
                    Toast.makeText(getActivity(), "El titulo o la descripción estan vacios, rellenalos", Toast.LENGTH_SHORT).show();
                }else {
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String titulo = titulopubli.getText().toString();
                            textoData.put("textos", titulo.toUpperCase());
                            String descripcion = descripcionpubli.getText().toString();
                            textoData.put("descripcion", descripcion);
                            textoData.put("idUsuario",userIdUsuario);
                            textoData.put("fecha", new Date());


                            funkosRef.add(textoData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {


                                        }
                                    });
                            dialog.dismiss();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialogButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }

            }
        });
        subir_foto();
    }

    public void subir_foto(){
        imagenpubli.setOnClickListener(new View.OnClickListener() {
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
                try {
                    subirPhoto(image_url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void subirPhoto(Uri image_url) throws IOException {
        // Obtener un objeto Bitmap de la imagen seleccionada
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), image_url);

        // Comprimir la imagen con una calidad del 50%
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        // Subir la imagen comprimida a Firebase Storage
        String rute_storage_photo = storage_path + "" + photo + "" + userId + "" + contadorPhoto;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            // Obtener el token de la URL de descarga
                            String token = uri.getLastPathSegment();

                            textoData.put("imagenes", download_uri);
                            textoData.put("token", token);

                            Picasso.with(getContext())
                                    .load(download_uri)
                                    .into(imagenpubli);
                        }
                    });
                }
            }
        });
    }


    public void getNumFunkoObjects() {
        db.collection("funko")
                .whereEqualTo("idUsuario", userIdUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numObjects = task.getResult().size();
                            contadorPhoto = numObjects;
                        } else {
                        }
                    }
                });
    }



}