package lk.jiat.sltb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lk.jiat.sltb.model.DatabaseHelper;

public class dashboard extends AppCompatActivity {
    private RecyclerView docdetailsre;
    private CardAdapter cardAdapter;
    private List<Card> cardList; // Original list of all cards
    private List<Card> filteredCardList; // Filtered list for search results
    private static final String COLLECTION_BUS_DETAILS = "buses";

    private ListView listview1;
    private ImageView imageView18, imageView17, imageView12, imageView14, imageView11;
    private ProgressBar progressBar;
    private String currentRoute = ""; // Default: no filter

    private String emailPhone;
    private DatabaseHelper databaseHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize views after setContentView()
        docdetailsre = findViewById(R.id.docdetailsre);
        listview1 = findViewById(R.id.listview1);
        imageView18 = findViewById(R.id.imageView18);
        progressBar = findViewById(R.id.progressBar);
        imageView17 = findViewById(R.id.imageView17);
        imageView12 = findViewById(R.id.imageView12);
        imageView14 = findViewById(R.id.imageView14);
        imageView11 = findViewById(R.id.imageView11);

        // Set up RecyclerView
        docdetailsre.setLayoutManager(new LinearLayoutManager(this));
        cardList = new ArrayList<>();
        filteredCardList = new ArrayList<>();
        cardAdapter = new CardAdapter(this, filteredCardList); // Pass filtered list to adapter
        docdetailsre.setAdapter(cardAdapter);
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        databaseHelper = new DatabaseHelper(this);
        
        Log.d("dash", "Received data: " +
                "\nEmail/Phone: " + emailPhone);
        // Set up ListView
        String[] items = {"Help", "Contact Us", "About Us", "Log Out", "Back"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listview1.setAdapter(adapter);

        listview1.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;
            switch (position) {
                case 0: // "Help"
                    intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                    break;
                case 1: // "Contact Us"
                    intent = new Intent(this, ContactUsActivity.class);
                    startActivity(intent);
                    break;
                case 2: // "About Us"
                    intent = new Intent(this, AboutUsActivity.class);
                    startActivity(intent);
                    break;
                case 3: // "Log Out"
                    intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 4: // "Back"
                    listview1.setVisibility(View.GONE);
                    break;
            }
            listview1.setVisibility(View.GONE);
        });

        // Toggle ListView visibility
        imageView18.setOnClickListener(v -> {
            if (listview1.getVisibility() == View.GONE) {
                listview1.setVisibility(View.VISIBLE);
            } else {
                listview1.setVisibility(View.GONE);
            }
        });

        // Navigate to other activities
        imageView17.setOnClickListener(v -> navigateToActivity(ticketview.class,emailPhone));

        imageView12.setOnClickListener(v ->
                navigateToActivity(notificationpanel.class));

        imageView14.setOnClickListener(v -> {
            Intent intent = new Intent(dashboard.this, userprofile.class);
            intent.putExtra("EMAIL_PHONE", emailPhone);
            startActivity(intent);
        });

        // Reset dashboard on imageView11 click
        imageView11.setOnClickListener(v -> resetDashboard());

        // Load specializations from Firestore and set up chips
        loadSpecializations();

        // Set up SearchView
        setupSearchView();
    }
    private void navigateToActivity(Class<?> activityClass, String emailPhone) {
        Intent intent = new Intent(dashboard.this, activityClass);
        intent.putExtra("EMAIL_PHONE", emailPhone); // Pass emailPhone as an extra
        startActivity(intent);
    }
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Not used for live search
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter cards based on search query
                filterCards(newText);
                return true;
            }
        });
    }

    private void filterCards(String query) {
        filteredCardList.clear();

        if (query.isEmpty()) {
            // If query is empty, show all cards
            filteredCardList.addAll(cardList);
        } else {
            // Filter cards based on query
            for (Card card : cardList) {
                if (card.getDocname().toLowerCase().contains(query.toLowerCase()) ||
                        card.getSpecialization().toLowerCase().contains(query.toLowerCase())) {
                    filteredCardList.add(card);
                }
            }
        }

        // Notify adapter of data change
        cardAdapter.notifyDataSetChanged();
    }

    private void resetDashboard() {
        // Clear current specialization filter
        currentRoute = "";

        // Uncheck all chips
        ChipGroup chipGroup = findViewById(R.id.chip_group_layout);
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }

        // Reload all cards
        loadCards(progressBar);
    }

    private void loadSpecializations() {
        databaseHelper.fetchAllBuses(new DatabaseHelper.OnBusesFetchedListener() {
            @Override
            public void onBusesFetched(List<Map<String, Object>> buses) {
                        List<String> routes = new ArrayList<>();
                for (Map<String, Object> bus : buses) {
                    String route = (String) bus.get("route");
                    if (route != null && !routes.contains(route)) {
                        routes.add(route);
                    }
                }

                // Create chips based on fetched routes
                        setupChips(routes);

                        // Load all cards initially
                        loadCards(progressBar);
            }

            @Override
            public void onBusesFetchFailed(String error) {
                Log.e("Firestore", "Error getting documents: " + error);
                Toast.makeText(dashboard.this, "Failed to load routes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupChips(List<String> route) {
        ChipGroup chipGroup = findViewById(R.id.chip_group_layout);
        chipGroup.removeAllViews(); // Clear existing chips

        for (String routes : route) {
            Chip chip = new Chip(this);
            chip.setText(routes);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            chip.setClickable(true);
            chip.setChipBackgroundColor(getColorStateList(R.color.chip_background));
            chip.setTextColor(getColorStateList(R.color.text_primary_dark));
            chip.setChipStrokeWidth(2f);
            chip.setChipStrokeColor(getColorStateList(R.color.primary_dark));
            chip.setRippleColor(getColorStateList(R.color.primary_dark));
            
            // Set padding and margin
            chip.setChipMinHeight(48);
            chip.setPadding(8, 0, 8, 0);
            
            ChipGroup.LayoutParams params = new ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 4, 4, 4);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                // Uncheck all chips first
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    ((Chip) chipGroup.getChildAt(i)).setChecked(false);
                }

                // Check the clicked chip
                chip.setChecked(true);

                // Load cards filtered by specialization
                currentRoute = routes;
                loadCards(progressBar);
            });

            chipGroup.addView(chip);
        }
    }

    private void uncheckAllChips(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    private void loadCards(ProgressBar progressBar) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Apply filter if a route is selected
        if (!currentRoute.isEmpty()) {
            databaseHelper.fetchBusesByRoute(currentRoute, new DatabaseHelper.OnBusesFetchedListener() {
                @Override
                public void onBusesFetched(List<Map<String, Object>> buses) {
                    progressBar.setVisibility(View.GONE);
                    cardList.clear();
                    for (Map<String, Object> bus : buses) {
                        String imageUrl = (String) bus.get("imageUrl");
                        if (imageUrl == null) imageUrl = "";
                        String busname = (String) bus.get("bus_name");
                        if (busname == null) busname = "Unknown Bus";
                        String route = (String) bus.get("route");
                        if (route == null) route = "Unknown Route";
                        
                        Card card = new Card(imageUrl, busname, route, emailPhone);
                        cardList.add(card);
                    }
                    updateFilteredCards();
                }

                @Override
                public void onBusesFetchFailed(String error) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Firestore", "Error getting documents: " + error);
                    Toast.makeText(dashboard.this, "Failed to load buses", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            databaseHelper.fetchAllBuses(new DatabaseHelper.OnBusesFetchedListener() {
                @Override
                public void onBusesFetched(List<Map<String, Object>> buses) {
                    progressBar.setVisibility(View.GONE);
                        cardList.clear();
                    for (Map<String, Object> bus : buses) {
                        String imageUrl = (String) bus.get("imageUrl");
                        if (imageUrl == null) imageUrl = "";
                        String busname = (String) bus.get("bus_name");
                        if (busname == null) busname = "Unknown Bus";
                        String route = (String) bus.get("route");
                        if (route == null) route = "Unknown Route";
                        
                        Card card = new Card(imageUrl, busname, route, emailPhone);
                            cardList.add(card);
                    }
                    updateFilteredCards();
                }

                @Override
                public void onBusesFetchFailed(String error) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Firestore", "Error getting documents: " + error);
                    Toast.makeText(dashboard.this, "Failed to load buses", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateFilteredCards() {
                        // Update filteredCardList with all cards initially
                        filteredCardList.clear();
                        filteredCardList.addAll(cardList);

                        // Notify adapter of data change
                        cardAdapter.notifyDataSetChanged();

                        if (cardList.isEmpty()) {
                            Toast.makeText(this, "No Buses details available for this Route", Toast.LENGTH_SHORT).show();
                        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(dashboard.this, activityClass);
        startActivity(intent);
    }
}