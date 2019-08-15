package com.delong.factory.model.card;

import com.delong.factory.model.db.User;

import java.util.Date;

public class UserCard {
    private String id;
    private String name;
    private String phone;
    private String portrait;
    private String description;
    private int sex = 0;
    private int follws;
    private int following;
    private boolean isFollow;
    private Date modifyAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFollws() {
        return follws;
    }

    public void setFollws(int follws) {
        this.follws = follws;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }
    private transient User user;
    public  User build(){
        if (user==null){
            User user = new User();
            user.setId(id);
            user.setDescription(description);
            user.setName(name);
            user.setFollow(isFollow);
            user.setFollows(follws);
            user.setFollowing(following);
            user.setPhone(phone);
            user.setPortrait(portrait);
            user.setSex(sex);
            user.setModifyAt(modifyAt);
            return user;
        }
        return user;
    }
}
