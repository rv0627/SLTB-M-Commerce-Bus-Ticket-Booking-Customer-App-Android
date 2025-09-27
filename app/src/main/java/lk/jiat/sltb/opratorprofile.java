package lk.jiat.sltb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class opratorprofile extends AppCompatActivity {
    private ImageView docImageView;
    private TextView docNameTextView, specializationTextView;

    private RatingBar overallRatingBar;
    private RecyclerView ratingsRecyclerView;
    private RatingAdapter ratingAdapter;
    private List<Rating> ratingList;

private String emailPhone;
private String docName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_oparatorprofile);

        docImageView = findViewById(R.id.profileImage);
        docNameTextView = findViewById(R.id.textView33);
        specializationTextView = findViewById(R.id.usernearesttownhere);
        overallRatingBar = findViewById(R.id.ratingBar20);
        ratingsRecyclerView = findViewById(R.id.ratingrec);

        ratingList = new ArrayList<>();

        ratingAdapter = new RatingAdapter(this, ratingList);

        ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingsRecyclerView.setAdapter(ratingAdapter);


        String imageUrl = getIntent().getStringExtra("imageUrl");
        docName = getIntent().getStringExtra("bus_name");
        String specialization = getIntent().getStringExtra("route");
        emailPhone = getIntent().getStringExtra("EMAIL_PHONE");
        Log.d("dp", "Received data: " +
                "\nDoctor: " + docName +
                "\nEmail/Phone: " + emailPhone);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(docImageView);

        docNameTextView.setText(docName);
        specializationTextView.setText(specialization);

        loadRatings(docName);
    }
        private void loadRatings(String doctorName) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("reviews")
                    .whereEqualTo("busName", doctorName)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ratingList.clear();
                            double totalRating = 0.0;
                            int ratingCount = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String username = document.getString("username");
                                double rating = document.getDouble("rating");

                                String comment = document.getString("comment");

                                Timestamp timestamp = document.getTimestamp("timestamp");
                                String formattedTimestamp = "";
                                if (timestamp != null) {

                                    formattedTimestamp = timestamp.toDate().toString();
                                }


                                Rating ratingItem = new Rating(username, rating, comment, formattedTimestamp);
                                ratingList.add(ratingItem);


                                totalRating += rating;
                                ratingCount++;
                            }


                            ratingAdapter.notifyDataSetChanged();


                        } else {
                            Log.e("Firestore", "Error getting ratings: ", task.getException());
                        }
                    });

        ImageView imageView10 = findViewById(R.id.imageView10);
        imageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(opratorprofile.this, dashboard.class);
               startActivity(intent);
            }
        });
        Button bookappointmentbtn =findViewById(R.id.bookappointmentbtn);
            bookappointmentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNext = new Intent(opratorprofile.this, bookticket.class);
                intentNext.putExtra("bus_name", docName); // Changed from "docname" to "bus_name"
                intentNext.putExtra("EMAIL_PHONE", emailPhone);

               startActivity(intentNext);
            }
        });
    }
}