package edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.MainActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.Profile.ProfileActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.TopNavigationBar;

public class MatchActivity extends AppCompatActivity {

    //third icon in the navigation bar
    private static final int ACTIVITY_ITEM = 2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private HashMap<String, Integer> mList = new HashMap<>();
    private String currentUserID, mLastTimeStamp, mLastMessage, lastSeen;
    DatabaseReference mCurrUserIdInsideMatchConnections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchAdapter(getDataSetMatches(), MatchActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        setUpTopNavigationBar();
        getUserMatchId();
        mLastMessage = mLastTimeStamp = lastSeen = "";
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    protected void onStop(){
        super.onStop();
    }

    private void getLastMessageInfo(DatabaseReference userDb){
        //chat id of the current match
        mCurrUserIdInsideMatchConnections = userDb.child("connections").child("matches").child(currentUserID);

        mCurrUserIdInsideMatchConnections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // lastSeen is actually lastsend and is true if other user sent me a message and I have not read it yet.
                if (dataSnapshot.exists()){
                    if(dataSnapshot.child("lastMessage").getValue() != null && dataSnapshot.child("lastTimeStamp").getValue() != null && dataSnapshot.child("lastSend").getValue() != null) {
                        mLastMessage = dataSnapshot.child("lastMessage").getValue().toString();
                        mLastTimeStamp = dataSnapshot.child("lastTimeStamp").getValue().toString();
                        lastSeen = dataSnapshot.child("lastSend").getValue().toString();
                    }
                    else{
                        mLastMessage = "Start Chatting now!";
                        mLastTimeStamp = " ";
                        lastSeen = "true";
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUserMatchId() {
        Query sortedMatchesByLastTimeStamp = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches")
                .orderByChild("lastTimeStamp");

        sortedMatchesByLastTimeStamp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey(), match.child("ChatId").toString());
                        //getChatID(match.child("ChatId").toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void FetchMatchInformation(final String key, final String chatid) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        getLastMessageInfo(userDb);

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String userImageUrl = "";
                    final String lastMessage = "";
                    String lastTimeStamp = "";

                    if(dataSnapshot.child("name").getValue()!=null){
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("userImageUrl").getValue()!=null){
                        userImageUrl = dataSnapshot.child("userImageUrl").getValue().toString();
                    }

                    String milliSec = mLastTimeStamp;
                    Long now;

                    try {
                        now = Long.parseLong(milliSec);
                        lastTimeStamp = convertMilliToRelativeTime(now);
                        String[] arrOfStr = lastTimeStamp.split(",");
                        mLastTimeStamp = arrOfStr[0];
                    } catch (Exception e) {}

                    Match obj = new Match(userId, name, userImageUrl, mLastMessage, mLastTimeStamp, chatid, lastSeen);
                    if(mList.containsKey(chatid)){
                        int key = mList.get(chatid);
                        resultsMatches.set(resultsMatches.size() - key, obj);

                    }
                    else {
                        resultsMatches.add(0, obj);
                        mList.put(chatid, resultsMatches.size());
                    }
                    mMatchesAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public String convertMilliToRelativeTime(Long now) {
        String time = DateUtils.getRelativeDateTimeString(this, now, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        return time;
    }


    private ArrayList<Match> resultsMatches = new ArrayList<Match>();
    private List<Match> getDataSetMatches() {
        return resultsMatches;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }

    /**
     * set up top navigation bar view
     */
    private void setUpTopNavigationBar() {
        BottomNavigationView tvNB = findViewById(R.id.topNavBar);
        TopNavigationBar.logTopNav(tvNB);
        TopNavigationBar.setupTopBar(MatchActivity.this, tvNB);
        Menu menu = tvNB.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_ITEM);
        menuItem.setChecked(true);
    }
}