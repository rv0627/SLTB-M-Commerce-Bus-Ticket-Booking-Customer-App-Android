package lk.jiat.sltb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodayticketAdapter extends RecyclerView.Adapter<TodayticketAdapter.ViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public TodayticketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_todayticketcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        // Set passenger name
        holder.textViewPatientName.setText(ticket.getPassengerFirstName() + " " + ticket.getPassengerLastName());

        // Set bus name
        holder.textViewDoctorName.setText(ticket.getBusName());

        holder.textViewAppointmentDate.setText("Travel Date: " + ticket.getTravelDate());

        // Set departure time
        holder.textViewAppointmentTime.setText(ticket.getDepartureTime());

        holder.textViewAppointmentNumber.setText("Booking #" + ticket.getBookingNumber());

        // Set appointment status
        String status = ticket.getStatus();
        holder.textViewAppointmentStatus.setText(status);
        if ("approved".equalsIgnoreCase(status)) {
            holder.textViewAppointmentStatus.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.ue_green));
        } else if ("Pending".equalsIgnoreCase(status)) {
            holder.textViewAppointmentStatus.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.text_secondary_dark));
        } else if ("Completed".equalsIgnoreCase(status)) {
            holder.textViewAppointmentStatus.setTextColor(holder.itemView.getResources().getColor(lk.jiat.sltb.R.color.text_primary_dark));
        }

        // Set route
        holder.textViewLocation.setText(ticket.getRoute());

        // Set total fare
        int totalFare = ticket.getTotalFare();
        holder.textViewFees.setText("Total Fare: Rs. " + totalFare);

        // Show or hide the "Today" icon based on whether the appointment is for today
        if (ticket.isToday()) {
            holder.todayIconImageView.setVisibility(View.VISIBLE);
        } else {
            holder.todayIconImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPatientName, textViewDoctorName,textViewAppointmentDate, textViewAppointmentTime, textViewAppointmentStatus, textViewLocation, textViewFees,textViewAppointmentNumber;
        ImageView todayIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPatientName = itemView.findViewById(R.id.PatientNamet);
            textViewDoctorName = itemView.findViewById(R.id.DoctorNamet);
            textViewAppointmentDate = itemView.findViewById(R.id.AppointmentDate);
            textViewAppointmentTime = itemView.findViewById(R.id.AppointmentTimet);
            textViewAppointmentStatus = itemView.findViewById(R.id.AppointmentStatust);
            textViewLocation = itemView.findViewById(R.id.Locationt);
            textViewFees = itemView.findViewById(R.id.Fees);
            textViewAppointmentNumber = itemView.findViewById(R.id.AppointmentNumber);
            todayIconImageView = itemView.findViewById(R.id.todayIconImageView);
        }
    }
}