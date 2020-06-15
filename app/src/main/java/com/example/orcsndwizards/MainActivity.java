package com.example.orcsndwizards;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To initialize a game we need a deck and the activity context (in order to get/set views)
        // Deck needed to start game (creates deck of 40 cards)(shuffled)

        // Game class starter
       // Game game = new Game(this);

        // Calls method startGame()
        //game.startGame();

    }

}
