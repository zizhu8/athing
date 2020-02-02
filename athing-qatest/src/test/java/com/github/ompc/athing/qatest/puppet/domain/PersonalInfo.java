package com.github.ompc.athing.qatest.puppet.domain;

import java.util.Date;

public class PersonalInfo {

    private String idCard;
    private Date birthday;

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

}
