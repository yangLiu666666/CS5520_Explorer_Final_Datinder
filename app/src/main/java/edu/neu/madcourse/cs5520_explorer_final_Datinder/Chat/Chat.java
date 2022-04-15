package edu.neu.madcourse.cs5520_explorer_final_Datinder.Chat;

public class Chat {
    private String message;
    private String receiverImageUrl;
    private Boolean currentUser;
    private Boolean isSeen;


    public Chat(String message, Boolean currentUser, Boolean isSeen, String receiverImageUrl){
        this.message = message;
        this.currentUser = currentUser;
        this.isSeen = isSeen;
        this.receiverImageUrl = receiverImageUrl;
    }



    public String getMessage(){
        return message;
    }
    public void setMessage(String userID){
        this.message = message;
    }

    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }

    public Boolean getSeen() {
        return isSeen;
    }
    public void setSeen(Boolean seen) {
        isSeen = seen;
    }
    public String getReceiverImageUrl() {
        return receiverImageUrl;
    }
    public void setReceiverImageUrl(String receiverImageUrl) {
        this.receiverImageUrl = receiverImageUrl;
    }
}
