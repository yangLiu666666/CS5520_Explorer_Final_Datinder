package edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches.MatchActivity;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.Notification;
import edu.neu.madcourse.cs5520_explorer_final_Datinder.R;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private TextView schoolText, storyText;
    private EditText mSendEditText;
    private ImageButton mBack;
    private ImageButton mSendButton;

    private String notification;
    private String currentUserID, matchId, chatId;
    private String matchName, matchStory, matchSchool;
    private String lastMessage, lastTimeStamp;
    private String  message, createdByUser, isSeen, messageId, currentUserName, matchIcon;
    private Boolean currentUserBoolean;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat, mDatabaseMatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId = getIntent().getExtras().getString("matchId");
        matchName = getIntent().getExtras().getString("matchName");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //chat id of the current match
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");

        mDatabaseMatch = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("profileImageUrl");

        //reference to all the chats
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();
        getMatchIcon();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);


        mSendEditText = findViewById(R.id.message);
        mBack = findViewById(R.id.chatBack);

        mSendButton = findViewById(R.id.send);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(
                                    mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MatchActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        //toolbar
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);


        // Updating onChat as the current chat id for current user

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat = new HashMap();
        onchat.put("onChat", matchId);
        reference.updateChildren(onchat);

        // Updating lastSend for opposite user to be shown for the current user after he / she opens this chat activity

        DatabaseReference current = FirebaseDatabase.getInstance().getReference("Users").child(matchId).child("connections").child("matches").child(currentUserID);
        Map lastSeen = new HashMap();
        lastSeen.put("lastSend", "false");
        current.updateChildren(lastSeen);

    }

    protected void onPause(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);

        Map onchat = new HashMap();
        onchat.put("onChat", "None");
        reference.updateChildren(onchat);
        super.onPause();
    }
    protected void onStop(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);

        Map onchat = new HashMap();
        onchat.put("onChat", "None");
        reference.updateChildren(onchat);
        super.onStop();
    }
    private void seenMessage(final String text){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(matchId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("onChat").exists()){
                        Log.d("seen",dataSnapshot.child("onChat").getValue().toString() + " " +  message + " ");
                        if(dataSnapshot.child("notificationKey").exists())
                            notification = dataSnapshot.child("notificationKey").getValue().toString();
                        else
                            notification = "";

                        if (!dataSnapshot.child("onChat").getValue().toString().equals(currentUserID)) {
                            // Send notification to the opposite user if he is not on the chat


                            new Notification(text, "New message from: " + currentUserName, notification, "activityToBeOpened", "MatchesActivity");
                        }
                        else {
                            // Mark that the chat has been read and remove notification dot

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId);
                            Map seenInfo = new HashMap();
                            seenInfo.put("lastSend", "false");
                            reference.updateChildren(seenInfo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }
    //shows options for menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        TextView mMatchNameTextView= (TextView) findViewById(R.id.chattoolbartag);
        mMatchNameTextView.setText(matchName);
        ImageView mMatchImage = (ImageView) findViewById(R.id.MatchImage);


        if(matchIcon != null &&  !matchIcon.equals("default")){
            Glide.with(this).load(matchIcon).into(mMatchImage);
        }
        return true;
    }

    public void showProfile(View v){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item, null);
        TextView name = (TextView) popupView.findViewById(R.id.name);
        ImageView image = (ImageView) popupView.findViewById(R.id.image);
        TextView school = (TextView) popupView.findViewById(R.id.schoolText);
        school.setText(matchSchool);

        TextView story = (TextView) popupView.findViewById(R.id.storyText);
        story.setText(matchStory);

        name.setText(matchName);

        if(matchIcon != null &&  !matchIcon.equals("default")){
            Glide.with(this).load(matchIcon).into(image);
        }
        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //dismiss keyboard
        hideSoftKeyBoard();

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //unmatch button pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.unmatch) {
            new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("Unmatch")
                    .setMessage("Are you sure you want to unmatch?")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Unmatch", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMatch(matchId);
                            Intent intent = new Intent(ChatActivity.this, MatchActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(),"Unmatch successful", Toast.LENGTH_LONG).show();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else if(item.getItemId() == R.id.viewProfile){
            showProfile(findViewById(android.R.id.content));
        }


        return super.onOptionsItemSelected(item);
    }
    public void deleteMatch(String matchId) {
        DatabaseReference matchId_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId);
        DatabaseReference userId_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("matches").child(currentUserID);
        DatabaseReference yeps_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("yeps").child(currentUserID);
        DatabaseReference yeps_in_userId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("yeps").child(matchId);

        DatabaseReference matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        //delete the chatId in chat->chatId
        matchId_chat_dbReference.removeValue();
        //delete the matchId in Users->userId->connections->matches->matchId
        matchId_in_UserId_dbReference.removeValue();
        //delete the userId in Users->matchId->connections->matches->matchId
        userId_in_matchId_dbReference.removeValue();
        //delete yeps in matchId
        yeps_in_matchId_dbReference.removeValue();
        //delete yeps in curruserId
        yeps_in_userId_dbReference.removeValue();


    }



    private void sendMessage() {
        final String sendMessageText = mSendEditText.getText().toString();
        long now  = System.currentTimeMillis();
        String timeStamp = Long.toString(now);

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);
            newMessage.put("timeStamp", timeStamp);
            newMessage.put("seen", "false");



            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("name").exists())
                            currentUserName = dataSnapshot.child("name").getValue().toString();
                    }
                }

                // New changes

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //update curr user and match user's last message and last timeStamp
            lastMessage = sendMessageText;
            lastTimeStamp = timeStamp;
            updateLastMessage();
            seenMessage(sendMessageText);
            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void updateLastMessage() {
        DatabaseReference currUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchId);
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId).child("connections").child("matches").child(currentUserID);

        Map lastMessageMap = new HashMap();
        lastMessageMap.put("lastMessage", lastMessage);
        Map lastTimestampMap = new HashMap();
        lastTimestampMap.put("lastTimeStamp", lastTimeStamp);

        Map lastSeen = new HashMap();
        lastSeen.put("lastSend", "true");
        currUserDb.updateChildren(lastSeen);
        currUserDb.updateChildren(lastMessageMap);
        currUserDb.updateChildren(lastTimestampMap);


        matchDb.updateChildren(lastMessageMap);
        matchDb.updateChildren(lastTimestampMap);

    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMatchIcon(){
        mDatabaseMatch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    matchIcon = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    messageId = null;
                    message = null;
                    createdByUser = null;
                    isSeen = null;
                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }

                    if(dataSnapshot.child("seen").getValue()!=null){
                        isSeen = dataSnapshot.child("seen").getValue().toString();
                    }
                    else isSeen = "true";

                    messageId = dataSnapshot.getKey().toString();
                    if(message!=null && createdByUser!=null){
                        currentUserBoolean = false;
                        if(createdByUser.equals(currentUserID)){
                            currentUserBoolean = true;      //current user sent a message
                        }
                        Chat newMessage = null;
                        // If message is not read
                        if(isSeen.equals("false")) {

                            // If current user has no t send this message, then user is reading the message
                            if(!currentUserBoolean){
                                isSeen = "true";

                                // Set current message as read for opposite user
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).child(messageId);
                                Map seenInfo = new HashMap();
                                seenInfo.put("seen", "true");
                                reference.updateChildren(seenInfo);

                                newMessage = new Chat(message, currentUserBoolean,  true, matchIcon);
                            }
                            else {
                                newMessage = new Chat(message, currentUserBoolean, false,  matchIcon);
                            }
                        }
                        else
                            newMessage = new Chat(message, currentUserBoolean,  true, matchIcon);


                        DatabaseReference usersInChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(matchId);

                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        if(mRecyclerView.getAdapter() != null && resultsChat.size() > 0)
                            mRecyclerView.scrollToPosition(resultsChat.size() - 1);
                        else
                            Toast.makeText(getApplicationContext(), "Chat empty", Toast.LENGTH_LONG).show();
                    }
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

    private ArrayList<Chat> resultsChat = new ArrayList<Chat>();
    private List<Chat> getDataSetChat() {

        return resultsChat;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
}
