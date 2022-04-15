package edu.neu.madcourse.cs5520_explorer_final_Datinder;

public class User {
    private String uid;
    private String name;
    private String image;
    private String profile;



    public User(){

    }

    public User(String uid, String name, String image, String profile){
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
