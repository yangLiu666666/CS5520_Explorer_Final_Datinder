package edu.neu.madcourse.cs5520_explorer_final_Datinder;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.ShowCasePosition;
import ru.dimorinny.showcasecard.radius.Radius;

public class Swip extends AppCompatActivity {

    //second icon in the navigation bar
    private static final int ACTIVITY_ITEM = 1;
    private static final String TAG = "Swip";
    private arrayAdapter arrayAdapter;
    private int i;
    private  String tag;
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    boolean firstStart;
    private String currentUId, notification, sendMessageText;
    private boolean activityStarted;
    private DatabaseReference usersDb;


    ListView listView;
    List<Card> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityStarted = true;
        setContentView(R.layout.activity_swip);
        spinner = (ProgressBar)findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
        setupTopNavigationView();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        tag = "Swip";
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        if(mAuth != null && mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
        else{
            Log.d(tag, "Authorization failed");
            Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                // .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
//        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
//            @Override
//            public void idsAvailable(String userId, String registrationId) {
//                usersDb.child(currentUId).child("notificationKey").setValue(userId);
//            }
//        });

        Log.d(tag, "onCreate " + currentUId);

        checkUserSex();

        rowItems = new ArrayList<Card>();


        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );


        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
//

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d(tag, "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(Swip.this, "Left", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(Swip.this, "Right", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(Swip.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showToolTip_profile(ShowCasePosition position) {
        new ShowCaseView.Builder(Swip.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("Start off by choosing which streaming services you want to borrow and share!")
                .build()
                .show(Swip.this);
    }
    private void showToolTip_matches(ShowCasePosition position) {
        new ShowCaseView.Builder(Swip.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("Find you matches and begin chatting!")
                .build()
                .show(Swip.this);
    }

    public void DislikeBtn(View v) {
        System.out.println("just test: " + rowItems.size());
        if (rowItems.size() != 0) {
            Card card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            Toast.makeText(Swip.this, "Left", Toast.LENGTH_SHORT).show();

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();

            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.noCardsBanner);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }

            Intent btnClick = new Intent(Swip.this, SwipDislike.class);
            btnClick.putExtra("url", card_item.getUserImageUrl());
            startActivity(btnClick);
        }
    }

    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {
            Card card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            //check matches
            usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
            isConnectionMatch(userId);
            Toast.makeText(Swip.this, "Right", Toast.LENGTH_SHORT).show();

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();


            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.noCardsBanner);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }

            Intent btnClick = new Intent(Swip.this, SwipLike.class);
            btnClick.putExtra("url", card_item.getUserImageUrl());
            startActivity(btnClick);

        }
    }


    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        usersDb.child(currentUId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    sendMessageText = dataSnapshot.getValue().toString();
                else
                    sendMessageText = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(!currentUId.equals(userId)) {
            currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(Swip.this, "" +
                                "New Connection", Toast.LENGTH_LONG).show();

                        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                        Map mapLastTimeStamp = new HashMap<>();
                        long now  = System.currentTimeMillis();
                        String timeStamp = Long.toString(now);
                        mapLastTimeStamp.put("lastTimeStamp", timeStamp);

                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).updateChildren(mapLastTimeStamp);

                        usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                        usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).updateChildren(mapLastTimeStamp);

                        notification = " ";

                        DatabaseReference notificationID = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("notificationKey");
                        notificationID.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    notification = snapshot.getValue().toString();
                                    Log.d("sendChat", notification);
//                                    new SendNotification("You have a new match!", "", notification, null, null );
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
    }

//    private String userNeed, userGive;
//    private String oppositeUserNeed, oppositeUserGive;
    private String currentUserSex;
    private String oppositeUserSex;
    public void checkUserSex(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("CardSearch", dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("gender").getValue() != null){
                        // Log.d("CardSearch", "exists coloumn called");
                        currentUserSex = dataSnapshot.child("gender").getValue().toString();
                        switch(currentUserSex) {
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
//                        userNeed = dataSnapshot.child("need").getValue().toString();
//                        userGive = dataSnapshot.child("give").getValue().toString();
                        //  Log.d("CardSearch", "datachange called");

//                        oppositeUserGive = userNeed;
//                        oppositeUserNeed = userGive;
//                        getOppositeSexUsers(oppositeUserGive, oppositeUserNeed);
                        getOppositeSexUsers();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers(){

        usersDb.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("gender").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("gender").getValue().toString().equals(oppositeUserSex)) {
                        String userImageUrl = "default";
                        if (!dataSnapshot.child("userImageUrl").getValue().equals("default")) {
                            userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
                        }
                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), userImageUrl);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
//                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(currentUId)) {
//                    //Log.d("CardSearch", "getOppositeSex called");
//
//                    if (dataSnapshot.child("gender").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && !dataSnapshot.child("gender").getValue().toString().equals(currentUserSex)) {
//                        String userImageUrl = "default";
//                        if (!dataSnapshot.child("userImageUrl").getValue().equals("default")) {
//                            userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
//                        }
//                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), userImageUrl);
//                        rowItems.add(item);
//                        arrayAdapter.notifyDataSetChanged();
//                    }

//                    if (dataSnapshot.child("give").exists() && dataSnapshot.child("need").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("give").getValue().toString().equals(oppositeUserGive) && dataSnapshot.child("need").getValue().toString().equals(oppositeUserNeed)) {
//                        String userImageUrl = "default";
//                        if (!dataSnapshot.child("userImageUrl").getValue().equals("default")) {
//                            userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
//                        }
//                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), userImageUrl, dataSnapshot.child("need").getValue().toString(), dataSnapshot.child("give").getValue().toString(), dataSnapshot.child("budget").getValue().toString());
//                        rowItems.add(item);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                    else if( dataSnapshot.child("give").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("give").getValue().toString().equals(oppositeUserGive)){
//                        String userImageUrl = "default";
//                        if (!dataSnapshot.child("userImageUrl").getValue().equals("default")) {
//                            userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
//                        }
//                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), userImageUrl, dataSnapshot.child("need").getValue().toString(), dataSnapshot.child("give").getValue().toString(),  dataSnapshot.child("budget").getValue().toString());
//                        rowItems.add(item);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
//                    else if( dataSnapshot.child("need").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("need").getValue().toString().equals(oppositeUserNeed)){
//                        String userImageUrl = "default";
//                        if (!dataSnapshot.child("userImageUrl").getValue().equals("default")) {
//                            userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
//                        }
//                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), userImageUrl, dataSnapshot.child("need").getValue().toString(), dataSnapshot.child("give").getValue().toString(),  dataSnapshot.child("budget").getValue().toString());
//                        rowItems.add(item);
//                        arrayAdapter.notifyDataSetChanged();
//                    }

                //spinner.setVisibility(View.GONE);


                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }


            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    /**
     * setup top tool bar
     */
    private void setupTopNavigationView() {
        BottomNavigationView tvNB = findViewById(R.id.topNavBar);
        TopNavigationBar.logTopNav(tvNB);
        TopNavigationBar.setupTopBar(Swip.this, tvNB);
        Menu menu = tvNB.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_ITEM);
        menuItem.setChecked(true);
//        Log.d("", "setupTopNavigationView: setting up TopNavigationView");
//        BottomNavigationViewEx tvEx = findViewById(R.id.topNavBar);
//        TopNavigationBar.logTopNav(tvEx);
//        TopNavigationBar.setupTopBar(Swip.this, tvEx);
//        Menu menu = tvEx.getMenu();
//        MenuItem menuItem = menu.getItem(1);
//        menuItem.setChecked(true);
//        //show tool tip
//        View profile_view = findViewById(R.id.ic_profile);
//        View matches_view = findViewById(R.id.ic_matched);
//
//        if (firstStart) {
//            showToolTip_profile(new ViewPosition(profile_view));
//            //showToolTip_matches(new ViewPosition(matches_view));
//        }
//        SharedPreferences newPref = getSharedPreferences("prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = newPref.edit();
//        editor.putBoolean("firstStart", false);
//        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }

//    public  class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
//        private Context mContext;
//        public NotificationOpenedHandler(Context context) {
//            mContext = context;
//        }
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result)
//        {
//            OSNotificationAction.ActionType actionType = result.action.type;
//            JSONObject data = result.notification.payload.additionalData;
//            String activityToBeOpened;
//            String activity;
//
//            if (data != null)
//            {
//                activityToBeOpened = data.optString("activityToBeOpened", null);
//                if (activityToBeOpened != null && activityToBeOpened.equals("MatchesActivity"))
//                {
//                    try {
//                        Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
//                        intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                        Log.i("OneSignal", "customkey set with value: " + activityToBeOpened);
//                        mContext.startActivity(intent3);
//                    }
//                    catch (Throwable t){
//                        t.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
}