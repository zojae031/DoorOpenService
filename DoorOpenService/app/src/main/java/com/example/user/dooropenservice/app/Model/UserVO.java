package com.example.user.dooropenservice.app.Model;

import java.io.Serializable;

public class UserVO implements Serializable {
    private String id;
    private String password;
    private String company;
    private String name;
    public UserVO(String id,String password,String company,String name){
        this.id = id;
        this.password = password;
        this.company = company;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
