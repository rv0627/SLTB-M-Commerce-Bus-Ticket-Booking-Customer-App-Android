package lk.jiat.sltb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.textViewTitle.setText(notification.getTitle());
        holder.textViewContent.setText(notification.getContent());

        if (notification.isRead()) {
            holder.textViewStatus.setText("Read");
            holder.textViewStatus.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.ue_green));
        } else {
            holder.textViewStatus.setText("Unread");
            holder.textViewStatus.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.text_secondary_dark));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewContent, textViewStatus;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}