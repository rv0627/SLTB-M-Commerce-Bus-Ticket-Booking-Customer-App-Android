package lk.jiat.sltb;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private List<Card> cardList;


    public CardAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
        
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.activity_ticketdetailscard, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        Log.d("CardAdapter", "Binding card with emailPhone: " + card.getEmailPhone());
        // Load image using Glide
        Glide.with(context)
                .load(card.getImageUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.error_image)      // Error image if loading fails
                .into(holder.docimage);

        // Set text for doctor name and specialization
        holder.docnameid.setText(card.getDocname());
        holder.specializationid.setText(card.getSpecialization());

        // Handle item click to navigate to doctor profile
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, opratorprofile.class);
            intent.putExtra("imageUrl", card.getImageUrl());
            intent.putExtra("bus_name", card.getDocname());
            intent.putExtra("route", card.getSpecialization());
            intent.putExtra("EMAIL_PHONE", card.getEmailPhone());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView docimage;
        TextView docnameid, specializationid;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            docimage = itemView.findViewById(R.id.docimage);
            docnameid = itemView.findViewById(R.id.docnameid);
            specializationid = itemView.findViewById(R.id.specializationid);
        }
    }
}