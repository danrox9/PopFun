package com.example.popfun;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AjustesFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    Button eliminarcuenta;
    Button recuperarcuenta;
    String email;
    FirebaseAuth mAuth;
    ProgressDialog mdialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mdialog = new ProgressDialog(getActivity());
        eliminarcuenta = view.findViewById(R.id.eliminarcuenta);
        recuperarcuenta = view.findViewById(R.id.recuperarcontrasena);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        Drawable customBackground = getResources().getDrawable(R.drawable.fondo_degradado,activity.getTheme());
        actionBar.setBackgroundDrawable(customBackground);

        borrarusuarioaut(eliminarcuenta);
        recuperarcontrasena(recuperarcuenta);


    }

    public void recuperarcontrasena(Button boton){
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
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
                                        Toast.makeText(getActivity(), "Se ha mandado el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "No se pudo mandar el correo de restablecer contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                    mdialog.dismiss();
                                }
                            });
                        }else {
                            Toast.makeText(getActivity(), "Debe ingresar el email", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // Agregar esta línea
            }
        });
    }
    public void borrarusuarioaut(Button boton){
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Borrar el usuario actual y todos sus datos
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DocumentReference usuariosPhotoRef = db.collection("users").document(userId);
                                usuariosPhotoRef.delete();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Log.d("TAG", "Borrado exitoso");
                            } else {
                                Log.d("TAG", "Error al borrar");
                            }
                        }
                    }
                    );
                }
            }
        });
    }
}
