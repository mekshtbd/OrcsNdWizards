package com.example.orcsndwizards.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.orcsndwizards.objects.Card;
import com.example.orcsndwizards.recycler.MyAdapter;
import com.example.orcsndwizards.R;
import com.example.orcsndwizards.recycler.MyDecorator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference gameRef;

    private RecyclerView playerHand;
    private RecyclerView opponentHand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setup();

        //getMatchmakingIntent();

        setRecyclerView();

    }

    private void setRecyclerView() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card("Orc","20"));
        hand.add(new Card("Orc","20"));
        hand.add(new Card("Orc","20"));
        hand.add(new Card("Orc","20"));
        hand.add(new Card("Orc","20"));
        MyAdapter playerAdapter = new MyAdapter(hand);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        playerHand.setLayoutManager(layoutManager);
        playerHand.setAdapter(playerAdapter);
        MyDecorator decorator = new MyDecorator(10);
        playerHand.addItemDecoration(decorator);

        ArrayList<Card> opponentHand2 = new ArrayList<>();
        opponentHand2.add(new Card("",""));
        opponentHand2.add(new Card("",""));
        opponentHand2.add(new Card("",""));
        opponentHand2.add(new Card("",""));
        opponentHand2.add(new Card("",""));
        MyAdapter opponentAdapter = new MyAdapter(opponentHand2);
        LinearLayoutManager opponenManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        opponentHand.setLayoutManager(opponenManager);
        opponentHand.setAdapter(opponentAdapter);
        opponentHand.addItemDecoration(decorator);

    }

    private void setup() {
        database = FirebaseDatabase.getInstance();
        playerHand = findViewById(R.id.playerHand);
        opponentHand = findViewById(R.id.opponentHand);
    }

    private void getMatchmakingIntent() {
        Intent intent = getIntent();
        String gamePath = intent.getStringExtra("gamePath");
        gameRef = database.getReference(gamePath);
    }

}
