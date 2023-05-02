package com.example.popfun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.text.TextUtils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.popfun.RegistroActivity;

public class MainActivity extends AppCompatActivity {

    Button botonlogin;
    Button botonregistro;
    EditText emaillogin;
    EditText passwordlogin;
    Button botonviewpass;
    String email;
    FirebaseAuth mAuth;
    ProgressDialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mdialog = new ProgressDialog(this);
        botonregistro=findViewById(R.id.bregister);
        botonlogin=findViewById(R.id.blogin);
        emaillogin=findViewById(R.id.emailEditText);
        passwordlogin=findViewById(R.id.passwordEditText);
        botonviewpass=findViewById(R.id.btpassword);

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean("isLogged", false);
        if (isLogged) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            // El usuario debe iniciar sesión para poder usar la aplicación
        }


        setup();

    }

    public void setup(){
        botonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emaillogin.getText().toString();
                String password = passwordlogin.getText().toString();
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                    Toast.makeText(MainActivity.this, "El email y contraseña no han sido introducidos", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "El email no ha sido introducido", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "La contraseña no ha sido introducida", Toast.LENGTH_SHORT).show();
                } else{
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emaillogin.getText().toString(),passwordlogin.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        showHome(task.getResult().getUser().getEmail());
                                        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putBoolean("isLogged", true);
                                        editor.apply();
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

        botonviewpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.recuperarcontrasena);
                Button dialogButton = dialog.findViewById(R.id.dialog_button);
                EditText editText = dialog.findViewById(R.id.dialog_edittext);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                editText.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.fondo_degradado);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 800; // duplicar el ancho actual
                lp.height = 800; // duplicar la altura actual
                dialog.getWindow().setAttributes(lp);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        email = editText.getText().toString();
                        if(!email.isEmpty()){

                            mdialog.setMessage("Cargando...");
                            mdialog.setCanceledOnTouchOutside(false);
                            mdialog.show();
                            mAuth.setLanguageCode("es");
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Se ha mandado el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "No se pudo mandar el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                    mdialog.dismiss();
                                }
                            });
                        }else {
                            Toast.makeText(MainActivity.this, "Debe ingresar el email", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // Agregar esta línea
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