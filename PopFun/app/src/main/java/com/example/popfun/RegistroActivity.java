package com.example.popfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {
    Button botonregistro;
    EditText firstnamederegistro;
    EditText lastnamederegistro;
    EditText nicknamederegistro;
    EditText emailderegistro;
    EditText passwordderegistro;
    String nuevoemail;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        botonregistro=findViewById(R.id.bregisterregister);
        firstnamederegistro=findViewById(R.id.firstnameregistro);
        lastnamederegistro=findViewById(R.id.lastnameregistro);
        nicknamederegistro=findViewById(R.id.nicknameregistro);
        emailderegistro=findViewById(R.id.emailregistro);
        passwordderegistro=findViewById(R.id.passwordregistro);

        setup();

    }

    public void setup(){
        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailderegistro.toString().isEmpty() && passwordderegistro.toString().isEmpty()){

                }
                else{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailderegistro.getText().toString(),passwordderegistro.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        showHome(task.getResult().getUser().getEmail());
                                        HashMap<String, Object> hashmap = new HashMap<>();
                                        hashmap.put("First Name", firstnamederegistro.getText().toString());
                                        hashmap.put("Last Name", lastnamederegistro.getText().toString());
                                        hashmap.put("Nickname", nicknamederegistro.getText().toString());
                                        nuevoemail = emailderegistro.getText().toString();

                                        db.collection("users").document(nuevoemail).set(hashmap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Write was successful
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Write failed
                                                        Toast.makeText(RegistroActivity.this, "Error al Reqistrar el usuario", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }else {
                                        showAlert();
                                    }
                                }
                            });
                }
            }
        }

        );

    }
    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error autentificando al usuario");
        builder.setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void showHome(String email){
        Intent lanzarhome = new Intent(this, HomeActivity.class);
        lanzarhome.putExtra("email",email);
        startActivity(lanzarhome);
    }
}