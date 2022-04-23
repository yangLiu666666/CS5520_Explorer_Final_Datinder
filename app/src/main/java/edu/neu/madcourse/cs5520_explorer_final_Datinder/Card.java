package edu.neu.madcourse.cs5520_explorer_final_Datinder;

public class Card {
    private String userId;
    private String name;
    private String userImageUrl;
    private String story;
    private String school;

//    public Card (String userId, String name, String userImageUrl, String need, String give, String budget){
//        this.userId = userId;
//        this.name = name;
//        this.userImageUrl = userImageUrl;
//        this.need = need;
//        this.give = give;
//        this.budget = budget;
//    }
    public Card (String userId, String name, String userImageUrl, String story, String school){
        this.userId = userId;
        this.name = name;
        this.userImageUrl = userImageUrl;
        this.story = story;
        this.school = school;

    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
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

    public void setUserImageUrl(String userImageUrl){
        this.userImageUrl = userImageUrl;
    }
}
