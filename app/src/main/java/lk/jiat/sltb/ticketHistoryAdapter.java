package lk.jiat.sltb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ticketHistoryAdapter extends RecyclerView.Adapter<ticketHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public ticketHistoryAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_tickethistorycard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        // Set passenger name
        holder.textViewPatientName.setText(ticket.getPassengerFirstName() + " " + ticket.getPassengerLastName());

        // Set bus name
        holder.textViewDoctorName.setText("Bus: " + ticket.getBusName());

        holder.textViewAppointmentNumber.setText("Booking #" + ticket.getBookingNumber());


        holder.textViewAppointmentDate.setText("Travel Date: " + ticket.getTravelDate());

        // Set departure time
        holder.textViewAppointmentTime.setText("Departure Time: " + ticket.getDepartureTime());
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPatientName, textViewDoctorName,textViewAppointmentNumber,textViewAppointmentDate, textViewAppointmentTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPatientName = itemView.findViewById(R.id.PatientName);
            textViewDoctorName = itemView.findViewById(R.id.DoctorName);
            textViewAppointmentNumber = itemView.findViewById(R.id.AppointmentNumber);
            textViewAppointmentDate = itemView.findViewById(R.id.AppointmentDate);
            textViewAppointmentTime = itemView.findViewById(R.id.AppointmentTimet);
        }
    }
}