package edu.neu.madcourse.cs5520_explorer_final_Datinder;

public class Card {
    private String userId;
    private String name;
    private String userImageUrl;
    private String need;
    private String give;
    private String budget;
//    public Card (String userId, String name, String userImageUrl, String need, String give, String budget){
//        this.userId = userId;
//        this.name = name;
//        this.userImageUrl = userImageUrl;
//        this.need = need;
//        this.give = give;
//        this.budget = budget;
//    }
    public Card (String userId, String name, String userImageUrl){
        this.userId = userId;
        this.name = name;
        this.userImageUrl = userImageUrl;

    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getUserImageUrl(){
        return userImageUrl;
    }
    public String getNeed(){
        return need;
    }
    public String getGive(){
        return give;
    }

    public void setNeed(String need) {
        this.need = need;
    }

    public void setGive(String give){
        this.give = give;
    }

    public void setBudget(String budget){
        this.budget = budget;
    }
    public String getBudget(){
        return budget;
    }
    public void setUserImageUrl(String userImageUrl){
        this.userImageUrl = userImageUrl;
    }
}
