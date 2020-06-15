package com.example.orcsndwizards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoggingActivity extends AppCompatActivity {
    private EditText inputUser;
    private EditText inputMail;

    private String email;
    private String pass;

    private Button btnRegister;
    private Button btnLogin;

    private FirebaseAuth auth;

    private DatabaseReference ref;

    private LinearLayout screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);

        setup();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
                if(!email.isEmpty() && !pass.isEmpty()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoggingActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                            }else{
                                showError();
                            }
                        }
                    });
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
                if(!email.isEmpty() && !pass.isEmpty()){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        boolean loged = false;
                                        for(DataSnapshot dps : dataSnapshot.getChildren()){
                                            if(dps.getKey().equals(user.getUid())){
                                                loged = true;
                                            }
                                        }
                                        if(!loged) {
                                            ref.child(user.getUid()).setValue(user.getUid());
                                            startRoomActivity(user.getUid());
                                        }
                                        else
                                            Toast.makeText(LoggingActivity.this, "Usuario ya logeado", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                showError();
                            }
                        }
                    });
                }
            }
        });

        setLayoutVisuals();
    }

    private void getData() {
        email = inputUser.getText().toString();
        pass = inputMail.getText().toString();
        inputUser.getText().clear();
        inputMail.getText().clear();
    }

    private void showError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Error autenticando usuario");
        builder.setPositiveButton("Aceptar",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // Set global variables
    private void setup(){

        // (layout elements)
        inputUser = findViewById(R.id.inputMail);
        inputMail = findViewById(R.id.inputPass);

        btnRegister = findViewById(R.id.registerBut);
        btnLogin = findViewById(R.id.loginBut);

        screen = findViewById(R.id.loginScreen);

        // (other variables)
        email = "";
        pass = "";

        // Set firebase connection
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null) {
            ref.child(currentUser.getUid()).setValue(currentUser.getUid());
            startRoomActivity(currentUser.getUid());
        }
    }

    private void setLayoutVisuals(){
        inputUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) inputUser.setHint("");
                else inputUser.setHint(R.string.mail_hint);
            }
        });
        inputMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) inputMail.setHint("");
                else inputMail.setHint(R.string.pass_hint);
            }
        });
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userText = inputUser.getText().toString();
                String passText = inputMail.getText().toString();

                if(userText.isEmpty() && !passText.isEmpty()){
                    inputUser.setHint(R.string.mail_hint);
                }else if(!userText.isEmpty() && passText.isEmpty()){
                    inputMail.setHint(R.string.pass_hint);
                }else if(userText.isEmpty()){
                    inputUser.setHint(R.string.mail_hint);
                    inputMail.setHint(R.string.pass_hint);
                }
                inputUser.clearFocus();
                inputMail.clearFocus();
            }
        });
    }

    // Starts Room activity
    private void startRoomActivity(String userKey){
        Intent intent = new Intent(getApplicationContext(), MatchmakingActivity.class);
        intent.putExtra("userKey",userKey);
        startActivity(intent);
        finish();
    }

}
