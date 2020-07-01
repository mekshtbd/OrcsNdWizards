package com.example.orcsndwizards.gameobjects;

import androidx.annotation.NonNull;

import com.example.orcsndwizards.R;
import com.example.orcsndwizards.recycler.MyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> cards;

    private DatabaseReference remCardsRef;

    public Deck(){
        this.cards = new ArrayList<>();
    }

    public void createDeck(){
        int value = 1;
        String cardName;
        boolean isSpecial = false;
        int idPic;

        for(int i=0;i<40;i++){
            if(i==10 || i == 20 || i == 25 || i == 30 || i == 35) value = 1;
            if(i>=20) isSpecial = true;
            if(i<10){
                cardName = "Orc";
                idPic = R.drawable.orc;
            }
            else if(i < 20){
                cardName = "Wizard";
                idPic = R.drawable.wizard;
            }
            else if(i<25){
                cardName = "Paladin";
                idPic = R.drawable.paladin;
            }
            else if(i<30){
                cardName = "Dragon";
                idPic = R.drawable.dragon;
            }
            else if(i<35){
                cardName = "Archer";
                idPic = R.drawable.archer;
            }
            else{
                cardName = "Priest";
                idPic = R.drawable.priest;
            }
            cards.add(new Card(cardName,String.valueOf(value),isSpecial, idPic));
            value++;
        }
        for(int i=0;i<4;i++) Collections.shuffle(this.cards);
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setRemCardsRef(DatabaseReference remCardsRef) {
        this.remCardsRef = remCardsRef;
    }

    public void drawFirstHand(ArrayList<Card> playerHand, MyAdapter playerAdapter, boolean turn) {
        for (int i=0;i<5;i++){
            if (turn) playerHand.add(cards.get(i));
            else playerHand.add(cards.get(i+5));
        }
        playerAdapter.notifyDataSetChanged();
        cards.subList(0,10).clear();

        remCardsRef.setValue(this.cards.size());
    }

    public void drawOpponentHand(ArrayList<Card> opponentHand, MyAdapter opponentAdapter){
        for (int i=0;i<5;i++){
            opponentHand.add(new Card("",R.drawable.card));
            opponentAdapter.notifyItemInserted(i);
        }
    }

    public void drawCard(ArrayList<Card> playerHand, MyAdapter playerAdapter, boolean turn){
        if(turn) playerHand.add(cards.get(0));
        else playerHand.add(cards.get(1));
        playerAdapter.notifyItemInserted(playerHand.size()-1);
        cards.subList(0,2).clear();
        remCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                remCardsRef.setValue((Long)dataSnapshot.getValue()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
