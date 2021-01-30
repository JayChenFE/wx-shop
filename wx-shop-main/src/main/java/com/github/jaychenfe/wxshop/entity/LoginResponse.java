package com.github.jaychenfe.wxshop.entity;


import com.github.jaychenfe.wxshop.pojo.Users;

public class LoginResponse {
    private boolean login;
    private Users user;

    public static LoginResponse notLogin() {
        return new LoginResponse(false, null);
    }

    public static LoginResponse login(Users user) {
        return new LoginResponse(true, user);
    }

    public LoginResponse() {
    }

    private LoginResponse(boolean login, Users user) {
        this.login = login;
        this.user = user;
    }

    public Users getUser() {
        return user;
    }

    public boolean isLogin() {
        return login;
    }
}
