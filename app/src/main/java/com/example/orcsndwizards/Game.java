package com.example.orcsndwizards;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Game {
    private Player player1;
    private Deck deck;
    private Activity activity;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    // Constructor
    public Game(Activity activity){
        this.player1 = new Player();
        this.activity = activity;
        this.recyclerView = this.activity.findViewById(R.id.recyclerView);
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("game1");
    }

    public void startGame(){
        connectPlayers();
        setRecyclerView();
        //drawFirstHand();
    }

    private void connectPlayers() {
        final DatabaseReference gameRef = database.getReference("game1");

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean play1 = (Boolean)dataSnapshot.child("player1").getValue();
                Boolean play2 = (Boolean)dataSnapshot.child("player2").getValue();
                if(play1 && !play2){
                    gameRef.child("player2").setValue(true);
                }else if (!play1) {
                    gameRef.child("player1").setValue(true);
                    deck = new Deck();
                    gameRef.child("deck").setValue(deck);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setRecyclerView(){
        MyAdapter adapter;
        LinearLayoutManager layoutManager;

        adapter = new MyAdapter(player1.getHand(), this.activity);

        layoutManager= new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void drawFirstHand() {
        this.deck.drawHand(player1.getHand());
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void showDeckLog(){
        for(int i=0;i<deck.getDeck().size();i++){
            Log.e("Deck",deck.getDeck().get(i).toString());
        }
        Log.e("Deck size before draw ",String.valueOf(deck.getDeck().size()));
    }
    public void showHandLog(){
        for(int i=0;i<player1.getHand().size();i++){
            Log.e("Drawed hand",player1.getHand().get(i).toString());
        }
    }
}
