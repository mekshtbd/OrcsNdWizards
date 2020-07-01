package com.example.orcsndwizards.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.orcsndwizards.gameobjects.Game;
import com.example.orcsndwizards.gameobjects.Card;
import com.example.orcsndwizards.gameobjects.Deck;
import com.example.orcsndwizards.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity {
    private DatabaseReference gameRef;

    private boolean turn;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getMatchmakingIntent();

        startGame();

    }
    private void startGame() {
        final Deck deck = new Deck();
        gameRef.child("turn").setValue(true);
        if(turn){
            deck.createDeck();
            gameRef.child("deck").setValue(deck.getCards());
            game = new Game(turn, deck, gameRef, this);
            game.start();
        }else{
            DatabaseReference deckRef = gameRef.child("deck");
            deckRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null){
                        ArrayList<Card> deckCards = new ArrayList<>();
                        for(DataSnapshot card : dataSnapshot.getChildren()){
                            deckCards.add(card.getValue(Card.class));
                        }
                        deck.setCards(deckCards);
                        game = new Game(turn, deck, gameRef, GameActivity.this);
                        game.start();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getMatchmakingIntent() {
        Intent intent = getIntent();
        String gamePath = intent.getStringExtra("gamePath");
        turn = intent.getBooleanExtra("turn",false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameRef = database.getReference(gamePath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this.getApplicationContext(), MatchmakingActivity.class);
        startActivity(intent);
        finish();
    }
}
