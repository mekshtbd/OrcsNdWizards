package com.example.orcsndwizards.gameobjects;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orcsndwizards.R;
import com.example.orcsndwizards.activities.MatchmakingActivity;
import com.example.orcsndwizards.audio.MusicPlayer;

import com.example.orcsndwizards.gameobjects.cardeffects.CardSystem;
import com.example.orcsndwizards.recycler.MyAdapter;

import com.example.orcsndwizards.recycler.MyDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;


public class Game {

    private boolean turn;
    private boolean firstTurn;
    private Deck deck;

    private boolean droppedIn;

    private RecyclerView playerHandView;
    private RecyclerView opponentHandView;

    private ArrayList<Card> playerHand;
    private ArrayList<Card> opponentHand;

    private MyAdapter playerAdapter;
    private MyAdapter opponentAdapter;

    private DatabaseReference gameRef;
    private DatabaseReference turnRef;

    private DatabaseReference player1Points;
    private DatabaseReference player2Points;

    private TextView player1PointsView;
    private TextView player2PointsView;

    private ImageView firstCardView;
    private ImageView secondCardView;

    private TextView firstCardValueView;
    private TextView secondCardValueView;

    private Activity activity;
    private TextView cardCount;
    private ConstraintLayout deckView;

    private boolean gameFinished;
    private CardSystem cardSystem;

    private MusicPlayer musicPlayer;


    public Game(boolean turn, final Deck deck, DatabaseReference gameRef, Activity activity) {
        this.activity = activity;
        this.turn = turn;
        this.deck = deck;
        this.gameRef = gameRef;

        setup();

        startListeners();
    }

    public void start() {

        setDrawClickEvent();

        turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    boolean databaseTurn = (boolean) dataSnapshot.getValue();
                    if (turn) {
                        // turn == true
                        if (databaseTurn) {
                            // player 1 plays his turn
                            if (firstTurn) {
                                firstTurn = false;
                                deck.drawFirstHand(playerHand, playerAdapter, turn);
                                deck.drawOpponentHand(opponentHand, opponentAdapter);
                                cardCount.setText(String.valueOf(deck.getCards().size()));
                            }
                            playerAdapter.setClickable(true);
                            lightTurnButton(true);
                        }else{
                            lightTurnButton(false);
                        }
                    } else {
                        // turn == false
                        if (databaseTurn) {
                            if (firstTurn) {
                                firstTurn = false;
                                deck.drawFirstHand(playerHand, playerAdapter, turn);
                                deck.drawOpponentHand(opponentHand, opponentAdapter);
                                cardCount.setText(String.valueOf(deck.getCards().size()));
                            }
                            lightTurnButton(false);
                        } else {
                            playerAdapter.setClickable(true);
                            lightTurnButton(true);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lightTurnButton(boolean activate) {
        ImageView lightBtn = activity.findViewById(R.id.turnLight);
        if(activate){
            lightBtn.setImageResource(R.color.green);
        }else{
            lightBtn.setImageResource(R.color.red);
        }
    }

    private void setup() {
        setRecyclerView();

        gameRef.child("player1Points").setValue(0);
        gameRef.child("player2Points").setValue(0);

        this.turnRef = gameRef.child("turn");
        turnRef.setValue(true);
        gameRef.child("gameFinished").setValue(false);

        deck.setRemCardsRef(gameRef.child("remainingCards"));

        cardCount = activity.findViewById(R.id.deckCount);
        deckView = activity.findViewById(R.id.deck);

        firstTurn = true;

        player1Points = gameRef.child("player1Points");
        player2Points = gameRef.child("player2Points");
        player1PointsView = activity.findViewById(R.id.player1PointsView);
        player2PointsView = activity.findViewById(R.id.player2PointsView);
        player1PointsView.setText("0");
        player2PointsView.setText("0");
        gameFinished = false;
        firstCardView = activity.findViewById(R.id.firstCardPlayed);
        secondCardView = activity.findViewById(R.id.secondCardPlayed);
        firstCardValueView = activity.findViewById(R.id.firstCardValue);
        secondCardValueView = activity.findViewById(R.id.secondCardValue);
        musicPlayer = new MusicPlayer(activity,R.raw.orcsndwizardssong);
        cardSystem = new CardSystem(this);
    }
    private void setRecyclerView() {
        playerHandView = activity.findViewById(R.id.playerHand);
        playerHand = new ArrayList<>();
        playerAdapter = new MyAdapter(playerHand, this);
        MyDecorator decorator = new MyDecorator(30);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        playerHandView.setLayoutManager(layoutManager);
        playerHandView.addItemDecoration(decorator);
        playerHandView.scheduleLayoutAnimation();
        playerHandView.setAdapter(playerAdapter);

        opponentHandView = activity.findViewById(R.id.opponentHand);
        opponentHand = new ArrayList<>();
        opponentAdapter = new MyAdapter(opponentHand, this);
        LinearLayoutManager opponenManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        opponentHandView.setLayoutManager(opponenManager);
        opponentHandView.addItemDecoration(decorator);
        opponentHandView.scheduleLayoutAnimation();
        opponentHandView.setAdapter(opponentAdapter);

        playerAdapter.setClickable(false);
        opponentAdapter.setClickable(false);
    }
    private void reportResult(boolean result, Card card, ImageView tempView, TextView tempValueView) {
        if (!droppedIn) {
            droppedIn = result;
            if (result) {
                Animation fadeIn = AnimationUtils.loadAnimation(activity,R.anim.fadein);
                tempView.setAnimation(fadeIn);
                tempValueView.setAnimation(fadeIn);
                tempView.setImageResource(card.getIdPic());
                tempValueView.setText(card.getValue());
                playerAdapter.setClickable(false);
                cardSystem.updateDatabase(card);
                ArrayList<Card> hand = playerAdapter.getHand();
                int position = playerAdapter.getHandPosition();
                hand.remove(hand.get(position));
                playerAdapter.notifyDataSetChanged();
                if(turn){
                    if(!(deck.getCards().size()<=0)){
                        deckView.setClickable(true);
                    }else changeTurn();
                }
            } else tempView.setImageResource(R.color.white);
        }
    }
    public void changeTurn(){
        if(turn) turnRef.setValue(false);
        else turnRef.setValue(true);
    }
    private void startListeners() {

        ConstraintLayout tempLayout;
        final ImageView tempView;
        final TextView tempValueView;

        if(turn){
            tempLayout = activity.findViewById(R.id.firstCardLayout);
            tempView = firstCardView;
            tempValueView = firstCardValueView;
        }else{
            tempLayout = activity.findViewById(R.id.secondCardLayout);
            tempView = secondCardView;
            tempValueView = secondCardValueView;
        }
        tempLayout.setOnDragListener(new View.OnDragListener() {
            private boolean mInView = false;
            @Override
            public boolean onDrag(View v, DragEvent dragEvent) {
                DragData object = (DragData) dragEvent.getLocalState();
                Card card = object.getCard();
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        droppedIn = false;
                        tempValueView.setText("");
                        tempView.setImageResource(R.color.red);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        mInView = true;
                        tempView.setImageResource(card.getIdPic());
                        tempValueView.setText(card.getValue());
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        mInView = false;
                        tempView.setImageResource(R.color.red);
                        tempValueView.setText("");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        reportResult(dragEvent.getResult(), card, tempView, tempValueView);
                    case DragEvent.ACTION_DROP:
                        return mInView;
                }
                return true;
            }
        });
        gameRef.child("gameFinished").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && (boolean) dataSnapshot.getValue()) {
                    gameFinished = true;
                    musicPlayer.stop();
                    gameRef.child("gameFinished").removeEventListener(this);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            showDialogWin();
                        }
                    }, 1000);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
            private void showDialogWin() {
                String title;
                String message;
                int points1 = Integer.parseInt(player1PointsView.getText().toString());
                int points2 = Integer.parseInt(player2PointsView.getText().toString());
                if (turn) {
                    if (points1 < points2) title = "Has perdido :C";
                    else if (points1 > points2) title = "Has ganado :)";
                    else title = "Habéis empatado";
                    message = "Tus puntos : " + points1 + "\n" +
                            "Puntos rival : " + points2;
                } else {
                    if (points1 > points2) title = "Has perdido :C";
                    else if (points1 < points2) title = "Has ganado :)";
                    else title = "Habéis empatado :|";

                    message = "Tus puntos : " + points2 + "\n" +
                            "Puntos rival : " + points1;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() == null){
                                    gameRef.removeValue();
                                }else{
                                    gameRef.setValue(null);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent intent = new Intent(activity.getApplicationContext(), MatchmakingActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        player1Points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(gameFinished){
                    player1Points.removeEventListener(this);
                }else{
                    if(dataSnapshot.getValue()!=null){
                        long points = (long) dataSnapshot.getValue();
                        player1PointsView.setText(String.valueOf(points));
                        player1PointsView.setBackgroundResource(R.color.green);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                player1PointsView.setBackgroundResource(R.color.white);
                            }
                        }, 3000);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        player2Points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    if(gameFinished){
                        if(player2Points!=null) player2Points.removeEventListener(this);
                    }else{
                        long points = (long) dataSnapshot.getValue();
                        player2PointsView.setText(String.valueOf(points));
                        player2PointsView.setBackgroundResource(R.color.green);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                player2PointsView.setBackgroundResource(R.color.white);
                            }
                        }, 3000);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        gameRef.child("gameStopped").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!= null && (boolean)dataSnapshot.getValue()) showDialogStop();
            }

            private void showDialogStop() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Game Finished");
                builder.setMessage("Opponent has disconnected");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity.getApplicationContext(), MatchmakingActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        gameRef.child("remainingCards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long remCards = (long) dataSnapshot.getValue();
                if(dataSnapshot.getValue() != null) {
                    if(remCards >= 0)  {
                        cardCount.setText(String.valueOf(dataSnapshot.getValue()));
                        if(remCards == 0){
                            startOpponentDraw();
                        }
                    }
                }
            }

            private void startOpponentDraw() {
                turnRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null){
                            if(!(boolean)dataSnapshot.getValue()){
                                opponentHand.remove(opponentHand.get(0));
                                opponentAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setDrawClickEvent() {
        deckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDeck();
                deck.drawCard(playerHand, playerAdapter, turn);
                changeTurn();
                deckView.setClickable(false);
            }

            private void animateDeck() {
                int xPos = playerHandView.getRight();
                int yPos = playerHandView.getTop();
                ObjectAnimator translateX = ObjectAnimator.ofFloat(deckView, "translationX", xPos);
                ObjectAnimator translateY = ObjectAnimator.ofFloat(deckView, "translationY", yPos);
                translateX.setDuration(1000);
                translateY.setDuration(1000);
                translateX.start();
                translateY.start();
                translateX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator translateX = ObjectAnimator.ofFloat(deckView, "translationX", 0);
                        translateX.setDuration(0);
                        translateX.start();
                    }
                });
                translateY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator translateY = ObjectAnimator.ofFloat(deckView, "translationY", 0);
                        translateY.setDuration(0);
                        translateY.start();
                    }
                });
            }
        });
        deckView.setClickable(false);
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public boolean isTurn() {
        return turn;
    }

    public DatabaseReference getTurnRef() {
        return turnRef;
    }

    public MyAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public DatabaseReference getGameRef() {
        return gameRef;
    }

    public Activity getActivity() {
        return activity;
    }

    public Deck getDeck() {
        return deck;
    }

    public ConstraintLayout getDeckView() {
        return deckView;
    }

    public DatabaseReference getPlayer1Points() {
        return player1Points;
    }

    public DatabaseReference getPlayer2Points() {
        return player2Points;
    }
}
