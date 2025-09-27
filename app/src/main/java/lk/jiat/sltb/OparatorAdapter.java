package lk.jiat.sltb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OparatorAdapter extends RecyclerView.Adapter<OparatorAdapter.HospitalViewHolder> {

    private ArrayList<Operator> operatorList;
    private int selectedPosition = -1; // Tracks the selected position

    public OparatorAdapter(ArrayList<Operator> operatorList) {
        this.operatorList = operatorList;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_oparatorcard, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Operator operator = operatorList.get(position);

        // Bind data to views
        holder.hospitalNameTextView.setText(operator.getName());
        holder.hospitalAddressTextView.setText(operator.getAddress());

        // Update RadioButton state
        holder.radioButton.setChecked(position == selectedPosition);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition; // Store the previous position
            selectedPosition = position; // Update the selected position

            // Notify only the previously selected and newly selected items
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return operatorList.size();
    }

    // Get the selected hospital
    public Operator getSelectedHospital() {
        if (selectedPosition != -1) {
            return operatorList.get(selectedPosition);
        }
        return null;
    }

    static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalNameTextView;
        TextView hospitalAddressTextView;
        RadioButton radioButton;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalNameTextView = itemView.findViewById(R.id.hospitalNameTextView);
            hospitalAddressTextView = itemView.findViewById(R.id.hospitalAddressTextView);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}