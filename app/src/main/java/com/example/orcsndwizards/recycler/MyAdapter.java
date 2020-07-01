package com.example.orcsndwizards.recycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orcsndwizards.gameobjects.Game;
import com.example.orcsndwizards.R;
import com.example.orcsndwizards.gameobjects.Card;
import com.example.orcsndwizards.gameobjects.DragData;
import com.example.orcsndwizards.gameobjects.cardeffects.CardSystem;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //Dataset
    private ArrayList<Card> hand;
    private boolean isClickable;
    private Game game;
    private int handPosition;

    // Constructor
    public MyAdapter(ArrayList<Card> hand, Game game){
        this.game = game;
        this.hand = hand;
        this.isClickable = true;
    }


    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }
    public ArrayList<Card> getHand() {
        return hand;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.MyViewHolder holder, final int position) {

        final Card card = this.hand.get(position);

        holder.imageView.setImageResource(card.getIdPic());
        holder.textValue.setText(String.valueOf(card.getValue()));
        holder.layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                if(isClickable){
                    if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                        View.DragShadowBuilder shadow = new View.DragShadowBuilder(holder.layout);
                        DragData state = new DragData(card, holder.layout.getWidth(), holder.layout.getHeight());
                        ViewCompat.startDragAndDrop(holder.layout, null, shadow, state, 0);
                        setHandPosition(position);
                    }
                }
               return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.hand.size();
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public int getHandPosition() {
        return handPosition;
    }

    public void setHandPosition(int handPosition) {
        this.handPosition = handPosition;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textValue;
        private ConstraintLayout layout;

        // MyViewHolder constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textValue = itemView.findViewById(R.id.valueView);
            this.layout = itemView.findViewById(R.id.layout);
        }
    }
}
