package com.parking.finder.OwnerUser;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parking.finder.R;
import com.parking.finder.classes.ParkingArea;
import com.parking.finder.utils.AppConstants;
import com.parking.finder.utils.services.MyParkingService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainOwnerActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase db;

    AppConstants globalClass;
    Boolean dialogshown=false;

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        Log.d(String.valueOf(MainOwnerActivity.this.getClass()),"Resume verify email");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isMyServiceRunning(MyParkingService.class))
            MainOwnerActivity.this.startService(new Intent(MainOwnerActivity.this, MyParkingService.class));
        startService(new Intent(MainOwnerActivity.this, MyParkingService.class));

        initComponents();
        attachListeners();
    }

    private void initComponents() {
        globalClass=(AppConstants)getApplicationContext();

        db=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
    }

    private void attachListeners() {
        db.getReference().child("ParkingAreas")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int found=0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ParkingArea parkingArea = dataSnapshot.getValue(ParkingArea.class);
                            if(parkingArea.userID.equals(auth.getCurrentUser().getUid())){
                                found=1;
                                break;
                            }
                            Log.d(String.valueOf(MainOwnerActivity.this.getClass()),"Load parking Area: "+String.valueOf(parkingArea));

                        }
                        if(found==1){
                            setContentView(R.layout.activity_main_owner);
                            BottomNavigationView navView = findViewById(R.id.nav_view);
                            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                    R.id.navigation_dashboard, R.id.navigation_add, R.id.navigation_profile)
                                    .build();
                            NavController navController = Navigation.findNavController(MainOwnerActivity.this, R.id.nav_host_fragment);
                            NavigationUI.setupActionBarWithNavController(MainOwnerActivity.this, navController, appBarConfiguration);
                            NavigationUI.setupWithNavController(navView, navController);
                            Intent mIntent = getIntent();
                            int bottomInt= mIntent.getIntExtra("FRAGMENT_NO",0);
                            if(bottomInt==0){
                                navView.setSelectedItemId(R.id.navigation_dashboard);
                            }else if(bottomInt==1){
                                navView.setSelectedItemId(R.id.navigation_add);
                            }else if(bottomInt==2){
                                navView.setSelectedItemId(R.id.navigation_profile);
                            }
                        }else{
                            Intent intent = new Intent(MainOwnerActivity.this, AddPositionActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        dialogshown=true;
        ActivityManager manager = (ActivityManager) MainOwnerActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}