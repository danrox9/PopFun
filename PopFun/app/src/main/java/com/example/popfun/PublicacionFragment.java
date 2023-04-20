package com.example.popfun;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PublicacionFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference funkosRef = db.collection("funko");

    String userId = FirebaseAuth.getInstance().getUid();
    Button subirpubli;
    EditText titulopubli;
    ImageView imagenpubli;
    StorageReference storageReference;
    String storage_path = "funko/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url;
    String photo ="photo";
    String idd;

    Map<String, Object> textoData = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publicacion, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference  = FirebaseStorage.getInstance().getReference();;
        subirpubli = view.findViewById(R.id.subirpublicacion);
        titulopubli = view.findViewById(R.id.titulo);
        imagenpubli = view.findViewById(R.id.image_view);

        subirpublicacion(view);

    }

    public void subirpublicacion(View view){

        subirpubli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //textoData.put("IdUsuario",userId);
                String titulo = titulopubli.getText().toString();
                textoData.put("textos", titulo);


                funkosRef.add(textoData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                            }
                        });
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
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            textoData.put("imagenes",download_uri);

                            Picasso.with(getContext())
                                    .load(download_uri)
                                    .resize(289,393)
                                    .into(imagenpubli);

                        }
                    });
                }
            }
        });
    }

}