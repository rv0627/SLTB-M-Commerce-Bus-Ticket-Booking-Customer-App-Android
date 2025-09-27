package lk.jiat.sltb;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlots; // List of time slots
    private OnItemClickListener listener; // Listener for item clicks

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(TimeSlot timeSlot); // Called when a time slot is clicked
    }

    // Constructor
    public TimeSlotAdapter(List<TimeSlot> timeSlots, OnItemClickListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        // Get the current time slot
        TimeSlot timeSlot = timeSlots.get(position);

        // Set the time slot text (e.g., "09:00 AM - 10:00 AM")
        holder.textViewTimeSlot.setText(timeSlot.getStartTime() + " - " + timeSlot.getEndTime());

        // Always set the availability status to "Available"
        String availabilityText = "Available";
        holder.textViewAvailability.setText(availabilityText);

        // Always use green color for availability
        holder.textViewAvailability.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.ue_green));

        // Make all items fully visible
        holder.itemView.setAlpha(1.0f);

        // Set click listener for the item (all slots are clickable)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(timeSlot); // Pass the clicked time slot to the listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size(); // Return the number of time slots
    }

    // ViewHolder class
    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTimeSlot; // Displays the time range
        TextView textViewAvailability; // Displays "Available"

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTimeSlot = itemView.findViewById(R.id.textViewTimeSlot);
            textViewAvailability = itemView.findViewById(R.id.textViewAvailability);
        }
    }
}