package com.example.popfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.popfun.RegistroActivity;

public class MainActivity extends AppCompatActivity {

    private Button botonlogin;
    private Button botonregistro;
    private EditText emaillogin;
    private EditText passwordlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botonregistro=findViewById(R.id.bregister);
        botonlogin=findViewById(R.id.blogin);
        emaillogin=findViewById(R.id.emailEditText);
        passwordlogin=findViewById(R.id.passwordEditText);


        setup();
    }

    public void setup(){
        botonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emaillogin.toString().isEmpty() && passwordlogin.toString().isEmpty()){

                }
                else{
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emaillogin.getText().toString(),passwordlogin.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        showHome(task.getResult().getUser().getEmail());
                                    }else {
                                        showAlert();
                                    }
                                }
                            });
                }
            }
        });

        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lanzarregistro(view);
            }
        });



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

    public void Lanzarregistro(View view){
        Intent lanzarregistro = new Intent(this, RegistroActivity.class);
        startActivity(lanzarregistro);
    }

}