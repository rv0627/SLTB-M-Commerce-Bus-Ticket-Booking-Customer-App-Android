package lk.jiat.sltb;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lk.jiat.sltb.model.DatabaseHelper;

public class today_tickets extends Fragment {

    private RecyclerView recyclerView;
    private TodayticketAdapter adapter;
    private List<Ticket> ticketList;
    private FirebaseFirestore db;
    private String mobileNumber;
    private DatabaseHelper databaseHelper; // To store the mobile number passed via Intent

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today_tickets, container, false);

        recyclerView = view.findViewById(R.id.appointtodayrec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ticketList = new ArrayList<>();
        adapter = new TodayticketAdapter(getContext(), ticketList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        databaseHelper = new DatabaseHelper(getContext());

        // Retrieve mobile number from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            mobileNumber = bundle.getString("EMAIL_PHONE");
            Log.d("TodayAppointments", "Received mobile number: " + mobileNumber);
        }

        if (mobileNumber == null || mobileNumber.isEmpty()) {
            Log.e("TodayAppointments", "Mobile number not found");
            return view;
        }

        // Fetch appointments for the given mobile number
        fetchBookingsByMobileNumber(mobileNumber);

        return view;
    }

    /**
     * Fetch bookings from Firestore filtered by mobile number.
     */
    private void fetchBookingsByMobileNumber(String mobileNumber) {
        databaseHelper.fetchBookingsByMobileNumber(mobileNumber, new DatabaseHelper.OnBookingsFetchedListener() {
            @Override
            public void onBookingsFetched(List<Map<String, Object>> bookings) {
                        ticketList.clear();
                for (Map<String, Object> booking : bookings) {
                            Ticket ticket = new Ticket(
                            ((Long) booking.get("bookingNumber")).intValue(),
                            (String) booking.get("firstName"),
                            (String) booking.get("lastName"),
                            (String) booking.get("email"),
                            (String) booking.get("mobileNumber"),
                            (String) booking.get("travelDate"),
                            (String) booking.get("departureTime"),
                            (String) booking.get("busName"),
                            (String) booking.get("departureStation"),
                            (String) booking.get("arrivalStation"),
                            ((Long) booking.get("baseFare")).intValue(),
                            ((Long) booking.get("stationFee")).intValue(),
                            ((Long) booking.get("serviceCharge")).intValue(),
                            (String) booking.get("status"),
                            (String) booking.get("seatNumber"),
                            (String) booking.get("route")
                            );
                            ticketList.add(ticket);
                        }
                        adapter.notifyDataSetChanged();
                        if (ticketList.isEmpty()) {
                            Log.d("TodayAppointments", "No bookings found for mobile number: " + mobileNumber);
                        }
            }

            @Override
            public void onBookingsFetchFailed(String error) {
                Log.e("Firestore", "Error getting documents: " + error);
                    }
                });
    }
}