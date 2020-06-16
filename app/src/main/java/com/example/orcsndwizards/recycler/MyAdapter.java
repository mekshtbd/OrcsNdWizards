package com.example.orcsndwizards.recycler;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orcsndwizards.R;
import com.example.orcsndwizards.objects.Card;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //Dataset
    private ArrayList<Card> hand;
    private Activity activity;

    // Constructor
    public MyAdapter(ArrayList<Card> hand){
        this.hand = hand;
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

    public ArrayList<Card> getHand() {
        return hand;
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
            hand.remove(hand.get(this.getAdapterPosition()));
            notifyDataSetChanged();
        }
    }
}
