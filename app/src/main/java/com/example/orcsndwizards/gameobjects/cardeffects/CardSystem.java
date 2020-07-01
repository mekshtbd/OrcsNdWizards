package com.example.orcsndwizards.gameobjects.cardeffects;

import android.app.Activity;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.orcsndwizards.R;
import com.example.orcsndwizards.decoration.Animations;
import com.example.orcsndwizards.gameobjects.Card;
import com.example.orcsndwizards.gameobjects.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;


public class CardSystem {
    private Game game;
    private Card myCard;
    private Card opponentCard;
    private boolean dbTurn;
    private boolean turn;
    private DatabaseReference firstCardRef;
    private DatabaseReference secondCardRef;

    public CardSystem(Game game){
        this.game = game;
        myCard = null;
        opponentCard = null;
        firstCardRef = game.getGameRef().child("firstCard");
        secondCardRef = game.getGameRef().child("secondCard");
    }

    public void updateDatabase(Card card) {
        myCard = card;
        turn = game.isTurn();
        DatabaseReference cardRef;
        if(turn){
            cardRef = firstCardRef;
        }else cardRef = secondCardRef;

        cardRef.setValue(card);
        getTurn();
    }

    private void getTurn() {
        DatabaseReference turnRef = game.getTurnRef();
        turnRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    dbTurn = (boolean)dataSnapshot.getValue();
                    if(dbTurn) updateCardVariables(true);
                    else updateCardVariables(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void drawCards() {
        Activity activity = game.getActivity();
        final ImageView firstCardView = activity.findViewById(R.id.firstCardPlayed);
        final ImageView secondCardView = activity.findViewById(R.id.secondCardPlayed);
        final TextView firstCardValueView = activity.findViewById(R.id.firstCardValue);
        final TextView secondCardValueView = activity.findViewById(R.id.secondCardValue);
        setAnimations(firstCardView, secondCardView, firstCardValueView, secondCardValueView);

        Card firstCard, secondCard;
        if(turn){
            firstCard = myCard;
            secondCard = opponentCard;
        }else{
            firstCard = opponentCard;
            secondCard = myCard;
        }
        firstCardView.setImageResource(firstCard.getIdPic());
        secondCardView.setImageResource(secondCard.getIdPic());
        firstCardValueView.setText(firstCard.getValue());
        secondCardValueView.setText(secondCard.getValue());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                firstCardView.setImageResource(R.color.white);
                secondCardView.setImageResource(R.color.white);
                firstCardValueView.setText("");
                secondCardValueView.setText("");
                if(!turn){
                    if(!(game.getDeck().getCards().size()<=0)){
                        game.getDeckView().setClickable(true);
                    }else game.changeTurn();
                    if(game.getPlayerHand().size()<=0) game.getGameRef().child("gameFinished").setValue(true);
                }
            }
        }, 3000);
    }

    private void setAnimations(ImageView firstCardView, ImageView secondCardView, TextView firstCardValueView, TextView secondCardValueView) {
        Animations animations = new Animations(game.getActivity());
        Animation fadein = animations.getFadeInAnim();
        firstCardView.setAnimation(fadein);
        secondCardView.setAnimation(fadein);
        firstCardValueView.setAnimation(fadein);
        secondCardValueView.setAnimation(fadein);
    }

    private void updatePoints(){
        applyEffect();
        if(!turn) applyValue();
    }

    private void applyValue() {
        int myCardValue = Integer.parseInt(myCard.getValue());
        int oppCardValue = Integer.parseInt(opponentCard.getValue());

        if(myCard.isSpecial() && !opponentCard.isSpecial()){
            oppCardValue = arrangeCardValues(oppCardValue);
        }else if(!myCard.isSpecial() && opponentCard.isSpecial()){
            myCardValue = arrangeCardValues(myCardValue);
        }

        String myCardName = myCard.getName();
        String oppCardName = opponentCard.getName();

        if(!((myCardName.equals("Orc") && oppCardName.equals("Wizard")) || (myCardName.equals("Wizard") && oppCardName.equals("Orc"))))
        setResult(myCardValue, oppCardValue);
    }

    private int arrangeCardValues(int cardValue) {
        int temp;
        if(cardValue<=2) temp = 1;
        else if(cardValue <= 4) temp = 2;
        else if(cardValue <= 6) temp = 3;
        else if(cardValue <= 8) temp = 4;
        else temp = 5;
        return temp;
    }

    private void setResult(int myCardValue, int oppCardValue) {
        DatabaseReference pointsRef = null;
        if(myCardValue < oppCardValue){
            pointsRef = game.getPlayer1Points();
        }else if(myCardValue > oppCardValue){
            pointsRef = game.getPlayer2Points();
        }
        sumPoints(pointsRef);
    }

    private void sumPoints(final DatabaseReference pointsRef) {
        if(pointsRef!=null){
            pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long points = (long)dataSnapshot.getValue();
                    pointsRef.setValue(points+1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void applyEffect() {

        switch(myCard.getName()){
            case "Priest" :
                priestEffect();
                break;
            case "Paladin" :
                paladinEffect();
                break;
        }
        switch(opponentCard.getName()){
            case "Archer" :
                archerEffect();
                break;
            case "Dragon" :
                dragonEffect();
                break;
        }
    }

    private void dragonEffect() {
        ArrayList<Card> hand = game.getPlayerHand();
        int handSize = hand.size();
        int randCardPos = 0, cardValue = 0;
        int rndAttack = new Random().nextInt((3 - 1) + 1) + 1;
        boolean chosen = false;
        Card card;

        if(!(hand.size() <= 0) && checkNormalCards(hand)){
            while(!chosen){
                randCardPos = (int) (Math.random() * handSize);
                card = hand.get(randCardPos);
                if(card.getName().equals("Orc") || card.getName().equals("Wizard")){
                    chosen = true;
                    cardValue = Integer.parseInt(card.getValue());
                    if((cardValue-rndAttack)<= 0){
                        hand.get(randCardPos).setValue("0");
                    }else{
                        hand.get(randCardPos).setValue(String.valueOf(cardValue-rndAttack));
                    }
                    game.getPlayerAdapter().notifyDataSetChanged();
                }
            }
        }
    }
    private void priestEffect() {
        final DatabaseReference pointsRef;
        if(turn) pointsRef = game.getPlayer1Points();
        else pointsRef = game.getPlayer2Points();
        sumPoints(pointsRef);
    }
    private void archerEffect() {
        final DatabaseReference pointsRef;
        if(turn) pointsRef = game.getPlayer1Points();
        else pointsRef = game.getPlayer2Points();

        pointsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long points = (long) dataSnapshot.getValue();
                pointsRef.setValue(points-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void paladinEffect() {
        ArrayList<Card> hand = game.getPlayerHand();
        int handSize = hand.size();
        int randCardPos = 0, cardValue = 0;
        int rndHeal = new Random().nextInt((3 - 1) + 1) + 1;
        boolean chosen = false;
        Card card;

        checkNormalCards(hand);
        if((!(hand.size() <= 0)) && checkNormalCards(hand)){
            while(!chosen){
                randCardPos = (int) (Math.random() * handSize);
                card = hand.get(randCardPos);
                if(card.getName().equals("Orc") || card.getName().equals("Wizard")){
                    chosen = true;
                    cardValue = Integer.parseInt(card.getValue());

                    hand.get(randCardPos).setValue(String.valueOf(cardValue + rndHeal));
                    game.getPlayerAdapter().notifyDataSetChanged();
                }
            }
        }
    }
    private boolean checkNormalCards(ArrayList<Card> hand) {
        for(int i=0;i<hand.size();i++){
            if(hand.get(i).getName().equals("Orc") || hand.get(i).equals("Wizard"))
                return true;
        }
        return false;
    }

    private void updateCardVariables(final boolean order) {
        final DatabaseReference oppCardRef;
        if (order) {
            oppCardRef = secondCardRef;
        }else{
            oppCardRef = firstCardRef;
        }
        oppCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    oppCardRef.removeEventListener(this);
                    opponentCard = dataSnapshot.getValue(Card.class);
                    updatePoints();
                    drawCards();
                    firstCardRef.removeValue();
                    secondCardRef.removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

