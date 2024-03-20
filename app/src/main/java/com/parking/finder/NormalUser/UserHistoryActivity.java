package com.parking.finder.NormalUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.finder.R;
import com.parking.finder.classes.BookedSlotKey;
import com.parking.finder.classes.BookedSlots;
import com.parking.finder.utils.AppConstants;
import com.parking.finder.utils.BasicUtils;
import com.parking.finder.utils.adapters.BookingHistoryAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;
    BasicUtils utils=new BasicUtils();
    TextView empty_view;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<BookedSlotKey> bookedSlotKeyList=new ArrayList<BookedSlotKey>();

    AppConstants globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        initComponents();
        attachListeners();
        if(!utils.isNetworkAvailable(getApplication())){
            Toast.makeText(UserHistoryActivity.this, "No Network Available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  //slide from left to right
    }

    private void initComponents() {
        globalClass=(AppConstants)getApplicationContext();

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        empty_view=findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.user_history_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserHistoryActivity.this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void attachListeners() {
        db.getReference().child("BookedSlots").orderByChild("userID").equalTo(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            BookedSlots bookedSlot = dataSnapshot.getValue(BookedSlots.class);
                            BookedSlotKey bookedSlotKey=new BookedSlotKey(bookedSlot,dataSnapshot.getKey());
                            bookedSlotKeyList.add(bookedSlotKey);
                        }
                        mAdapter = new BookingHistoryAdapter(bookedSlotKeyList);
                        recyclerView.setAdapter(mAdapter);
                        if(bookedSlotKeyList.isEmpty()){
                            recyclerView.setVisibility(View.GONE);
                            empty_view.setVisibility(View.VISIBLE);
                        }else{
                            recyclerView.setVisibility(View.VISIBLE);
                            empty_view.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

}