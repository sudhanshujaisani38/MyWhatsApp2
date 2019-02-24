package com.sudhanshujaisani.mywhatsapp;

public class Users {
    String image;
    String name;
    String status;

    public Users(String image, String name, String status, String thumb) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.thumb = thumb;
    }

    String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Users() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
