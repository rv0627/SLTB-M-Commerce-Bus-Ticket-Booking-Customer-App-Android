package lk.jiat.sltb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private Context context;
    private List<Rating> ratingList;

    public RatingAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_ratingcard, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating Rating = ratingList.get(position);

        holder.usernameTextView.setText(Rating.getUsername());
        holder.datetimeTextView.setText(Rating.getTimestamp());
        holder.commentTextView.setText(Rating.getComment());
        holder.ratingBar.setRating((float) Rating.getRating());

    }

    @Override
    public int getItemCount() {
        return ratingList != null ? ratingList.size() : 0;
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, datetimeTextView,commentTextView;
        RatingBar ratingBar;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            datetimeTextView = itemView.findViewById(R.id.datetimerating);
            ratingBar = itemView.findViewById(R.id.ratingBar20);
        }
    }
}