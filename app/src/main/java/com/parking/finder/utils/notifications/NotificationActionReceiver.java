package com.parking.finder.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.parking.finder.R;
import com.parking.finder.utils.AppConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class NotificationActionReceiver extends BroadcastReceiver {

    FirebaseAuth auth;
    FirebaseDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void performMarkAsReadCheckout(Context context, Intent intent){
        NotificationHelper notificationHelper=new NotificationHelper(context);
        Log.e("MarkAsReadCheckout", Objects.requireNonNull(intent.getStringExtra("readID")));
        db.getReference("BookedSlots").child(Objects.requireNonNull(intent.getStringExtra("readID"))).child("readNotification").setValue(1);
        notificationHelper.cancelNotification(intent.getIntExtra("notificationID",1));
        int notificationCount=notificationHelper.countNotificationGroup(context.getString(R.string.notification_group_id_1));
        if(notificationCount<=1){
            notificationHelper.cancelNotification(AppConstants.NOTIFICATION_GROUP_REQUEST_CODE);
        }
        Log.i(String.valueOf(this.getClass()),"Notifications Count: ".concat(String.valueOf(notificationCount)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void performMarkAsReadBooking(Context context, Intent intent){
        NotificationHelper notificationHelper=new NotificationHelper(context);
        Log.e("MarkAsReadBooking", Objects.requireNonNull(intent.getStringExtra("readID")));
        db.getReference("BookedSlots").child(Objects.requireNonNull(intent.getStringExtra("readID"))).child("readBookedNotification").setValue(1);
        notificationHelper.cancelNotification(intent.getIntExtra("notificationID",1));
        int notificationCount=notificationHelper.countNotificationGroup(context.getString(R.string.notification_group_id_1));
        if(notificationCount<=1){
            notificationHelper.cancelNotification(AppConstants.NOTIFICATION_GROUP_REQUEST_CODE);
        }
        Log.i(String.valueOf(this.getClass()),"Notifications Count: ".concat(String.valueOf(notificationCount)));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        String action=intent.getStringExtra("action");
        if(action.equals("MarkAsReadCheckout")){
            performMarkAsReadCheckout(context, intent);
        }else if(action.equals("MarkAsReadBooking")){
            performMarkAsReadBooking(context, intent);
        }
    }
}