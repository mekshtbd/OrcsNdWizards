package com.example.orcsndwizards;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //Dataset
    private ArrayList<Card> hand;
    private Activity activity;
    private boolean playTurn;

    // Constructor
    public MyAdapter(ArrayList<Card> hand, Activity activity){
        this.hand = hand;
        this.activity = activity;
        this.playTurn = true;
    }
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Card card = this.hand.get(position);
        holder.imageView.setImageResource(card.getIdPic());
        holder.textValue.setText(String.valueOf(card.getValue()));
    }

    @Override
    public int getItemCount() {
        return this.hand.size();
    }

    public void setTurn(boolean turn){
        this.playTurn = turn;
    }
    public ArrayList<Card> getHand() {
        return hand;
    }
    public Activity getActivity(){
        return this.activity;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private TextView textValue;

        // MyViewHolder constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textValue = itemView.findViewById(R.id.valueView);
            itemView.setOnClickListener(this);
        }

        // On Card clicked
        @Override
        public void onClick(View v) {
            if(playTurn){
                playCard(v);

                int position = this.getLayoutPosition();
                Card card = hand.get(position);
                ImageView imageCard = getActivity().findViewById(R.id.firstCardPlayed);
                TextView textValue = getActivity().findViewById(R.id.textValue);
                imageCard.setImageResource(card.getIdPic());
                textValue.setText(String.valueOf(card.getValue()));
                hand.remove(position);
                //hand.add(position,new Card("Wizard",15));
                notifyDataSetChanged();
                playTurn = false;
            }
        }

        private void playCard(View v) {

        }
    }
}
