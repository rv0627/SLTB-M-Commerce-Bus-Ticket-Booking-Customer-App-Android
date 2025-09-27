package lk.jiat.sltb;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class routedetailscard extends AppCompatActivity {

    private ImageView docimage;
    private TextView docnameid, specializationid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_ticketdetailscard);



            docimage = findViewById(R.id.docimage);
            docnameid = findViewById(R.id.docnameid);
            specializationid = findViewById(R.id.specializationid);

            String imageUrl = getIntent().getStringExtra("imageUrl");
            String docname = getIntent().getStringExtra("docname");
            String specialization = getIntent().getStringExtra("specialization");

            // Display the data
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_image)
                    .into(docimage);

            docnameid.setText(docname);
            specializationid.setText(specialization);



    }
}