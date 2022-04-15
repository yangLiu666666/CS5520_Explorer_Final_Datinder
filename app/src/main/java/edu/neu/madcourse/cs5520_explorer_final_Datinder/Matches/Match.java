package edu.neu.madcourse.cs5520_explorer_final_Datinder.Matches;

import java.util.ArrayList;

import edu.neu.madcourse.cs5520_explorer_final_Datinder.User;

public class Match {
    private String userId;
    private String name;
    private String iconUrl;
    private String lastMessage;
    private String lastTimeStamp;
    private String chatId;
    private String lastSeen;
    private ArrayList<User> userArrayList = new ArrayList<>();

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Match(String userId, String name, String iconUrl, String lastMessage, String lastTimeStamp, String lastSeen, String chatId){
        this.userId = userId;
        this.name = name;
        this.iconUrl = iconUrl;
        this.lastMessage = lastMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.chatId = chatId;
        this.lastSeen = lastSeen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public void setUserArrayList(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }
}
