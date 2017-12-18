package com.sz1358.transpixel.register;

/**
 * Created by xxx on 19/12/2017.
 */

public class RegisterService {
    public boolean register(String username, String password, String email) {
        return "james".equals(username) && "bond".equals(password) && "jamesbond@gmail.com".equals(email);
    }
}
