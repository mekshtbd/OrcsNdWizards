package com.example.orcsndwizards.gameobjects;

public class DragData {

    private Card card;
    private int width;
    private int height;

    public DragData(Card card, int width, int height) {
        this.card = card;
        this.width = width;
        this.height = height;
    }

    public Card getCard() {
        return card;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
