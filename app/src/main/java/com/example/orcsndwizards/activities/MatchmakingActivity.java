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
    private DatabaseReference matchRef;
    private DatabaseReference player1Ref;
    private DatabaseReference player2Ref;
    private Button playBtn;
    private Button cancelBtn;
    private Button logOutBtn;
    private ProgressBar searchingAnim;
    private String logedUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);

        setup();

        setMatchRef();

        setButtonClickers();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        logedUserKey = currentUser.getUid();
    }

    private void setup(){
        database = FirebaseDatabase.getInstance();
        matchRef = database.getReference("Matchmaking");
        player1Ref = matchRef.child("player1");
        player2Ref = matchRef.child("player2");
        playBtn = findViewById(R.id.playBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        logOutBtn = findViewById(R.id.logOutBtn);
        searchingAnim = findViewById(R.id.searchingView);
    }
    private void setButtonClickers() {

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setLayoutChanges(true);

                matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String player1 = getPlayerState(dataSnapshot)[0];
                        String player2 = getPlayerState(dataSnapshot)[1];

                        if(!player1.equals("") && player2.equals("")){
                            player2Ref.setValue(logedUserKey);
                            startGameActivity("");
                        }
                        else if(player1.equals("")){
                            player1Ref.setValue(logedUserKey);
                            player2Ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.getValue().equals("")){
                                        startGameActivity((String)dataSnapshot.getValue());
                                    }
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

                matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String player1 = getPlayerState(dataSnapshot)[0];
                        String player2 = getPlayerState(dataSnapshot)[1];

                        if(player1.equals(logedUserKey)){
                            player1Ref.setValue("");
                        }else if(player2.equals(logedUserKey)){
                            player2Ref.setValue("");
                        }
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

    private void setMatchRef(){

        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String player1 = getPlayerState(dataSnapshot)[0];
                String player2 = getPlayerState(dataSnapshot)[1];

                if(player1 == null) player1Ref.setValue("");
                if(player2 == null ) player2Ref.setValue("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setLayoutChanges(boolean playButton) {
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
            logoutColor.setSpan(new ForegroundColorSpan(Color.RED),0,13,0);
            logOutBtn.setText(logoutColor);
            searchingAnim.setVisibility(View.INVISIBLE);
        }
    }

    private String[] getPlayerState(DataSnapshot dataSnapshot){

        String[] players = new String[2];

        int counter = 0;
        for(DataSnapshot dps : dataSnapshot.getChildren()){
            if(counter<=0) players[0] = (String) dps.getValue();
            else players[1] = (String) dps.getValue();
            counter++;
        }

        return players;
    }

    private void startGameActivity(String player2Key){

        DatabaseReference gameRef = database.getReference("Games");
        String gameRefPath = "";

        // Player 2 - Player 1
        if(player2Key.equals("")){
            gameRefPath = "Games/"+logedUserKey;
            gameRef.child(logedUserKey+"/"+logedUserKey).setValue(logedUserKey);
        }else{
            gameRefPath = "Games/"+player2Key;
            gameRef.child(player2Key+"/"+logedUserKey).setValue(logedUserKey);
        }

        player1Ref.setValue("");
        player2Ref.setValue("");

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("gamePath",gameRefPath);
        startActivity(intent);
        finish();
    }

}