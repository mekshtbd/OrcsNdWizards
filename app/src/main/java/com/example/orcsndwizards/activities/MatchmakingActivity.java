package com.example.orcsndwizards.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.orcsndwizards.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MatchmakingActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference player1Ref;
    private DatabaseReference player2Ref;
    private DatabaseReference gameRef;
    private Button playBtn;
    private Button cancelBtn;
    private Button logOutBtn;
    private String logedUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);

        setup();

        setGameRef();

        checkGameContinues();

        setMatchRef();

        setButtonClickers();

    }

    private void setGameRef() {
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    gameRef.setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkGameContinues() {
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    for(DataSnapshot dps : dataSnapshot.getChildren()){
                        if(dps.getKey().contains(logedUserKey)){
                            gameRef.child(dps.getKey()+"/gameStopped").setValue(true);
                            gameRef.child(dps.getKey()).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        logedUserKey = currentUser.getUid();
    }

    private void setup(){

        database = FirebaseDatabase.getInstance();
        player1Ref = database.getReference("Matchmaking/player1");
        player2Ref = database.getReference("Matchmaking/player2");
        gameRef = database.getReference("Games");
        playBtn = findViewById(R.id.playBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        logOutBtn = findViewById(R.id.logOutBtn);
    }
    private void setButtonClickers() {

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLayoutChanges(true);
                player1Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot firstSnap) {
                        if(firstSnap.getValue().equals("")){
                            player1Ref.removeEventListener(this);
                            player1Ref.setValue(logedUserKey);
                            player2Ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot secondSnap) {
                                    if(!(secondSnap.getValue().equals(""))){
                                        player2Ref.removeEventListener(this);
                                        startGameActivity((String)secondSnap.getValue(), true);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else if(!(firstSnap.getValue().equals(logedUserKey))){
                            player1Ref.removeEventListener(this);
                            player2Ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot thirdSnap) {
                                    player2Ref.setValue(logedUserKey);
                                    player2Ref.removeEventListener(this);
                                    startGameActivity((String)firstSnap.getValue(), false);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLayoutChanges(false);

                player1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        player1Ref.removeEventListener(this);
                        player1Ref.setValue("");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                player2Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        player2Ref.removeEventListener(this);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference userRef = database.getReference("Users/"+currentUser.getUid());
                userRef.removeValue();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), LoggingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void startGameActivity(String playerKey, boolean turn){

        player1Ref.setValue("");
        player2Ref.setValue("");

        String gameRefPath = "";

        if(turn){
            gameRefPath = "Games/"+logedUserKey+playerKey;
        }
        else gameRefPath = "Games/"+playerKey+logedUserKey;

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("gamePath",gameRefPath);
        intent.putExtra("turn",turn);
        startActivity(intent);
        finish();
    }
    private void setMatchRef(){
        player1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    player1Ref.setValue("");
                    player2Ref.setValue("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setLayoutChanges(boolean playButton) {
        ProgressBar searchingAnim = findViewById(R.id.searchingView);
        if(playButton){
            playBtn.setVisibility(View.INVISIBLE);
            playBtn.setEnabled(false);
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setEnabled(true);
            logOutBtn.setEnabled(false);
            Spannable logoutColor = new SpannableString(logOutBtn.getText().toString());
            logoutColor.setSpan(new ForegroundColorSpan(Color.GRAY),0,13,0);
            logOutBtn.setText(logoutColor);
            searchingAnim.setVisibility(View.VISIBLE);
        }else{
            cancelBtn.setVisibility(View.INVISIBLE);
            cancelBtn.setEnabled(false);
            playBtn.setVisibility(View.VISIBLE);
            playBtn.setEnabled(true);
            logOutBtn.setEnabled(true);
            Spannable logoutColor = new SpannableString(logOutBtn.getText().toString());
            logoutColor.setSpan(new ForegroundColorSpan(Color.BLACK),0,13,0);
            logOutBtn.setText(logoutColor);
            searchingAnim.setVisibility(View.INVISIBLE);
        }
    }
}
