package com.example.orcsndwizards.objects;

import java.util.ArrayList;

public class Player {
    private int points;
    private ArrayList<Card> hand;

    public Player(){
        this.points = 0;
        hand = new ArrayList<>();
    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    @Override
    public String toString() {
        return "Player{" +
                "points=" + points +
                ", hand=" + hand +
                '}';
    }
}
