package com.example.orcsndwizards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> deck;

    public Deck(){
        this.deck = new ArrayList<>();
        String name = "Orc";
        int value = 1;
        boolean gone = false;

        for(int i=0;i<40;i++){
            if(!gone && i>=20){
                name = "Wizard";
                value = 1;
                gone = true;
            }
            this.deck.add(new Card(name, value));
            value++;
        }
        Collections.shuffle(this.deck);
    }

    public void drawHand(ArrayList<Card> playerHand){
        for(int i=0;i<5;i++){
            playerHand.add(this.deck.get(i));
            this.deck.remove(i);
        }
    }

    public boolean drawCard(ArrayList<Card> playerHand){
        playerHand.add(this.deck.get(0));
        this.deck.remove(0);
        return this.deck.size() <= 0;
    }
    public ArrayList<Card> getDeck() {
        return this.deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }
}
