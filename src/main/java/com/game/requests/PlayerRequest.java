package com.game.requests;

import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.Date;

public class PlayerRequest {
    private String name;

    private String title;
    private Race race;
    private Profession profession;
    private Integer experience;
    private Date birthday;
    private Boolean banned;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Long getBirthday() {
        if(birthday!=null) {
            return birthday.getTime();}
        return null;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
