package com.sz1358.transpixel.register;

/**
 * Created by xxx on 18/12/2017.
 */

public interface RegisterView {
    String getUsername();

    void showUsernameError(int resId);

    String getPassword();

    void showPasswordError(int resId);

    String getEmail();

    void showEmailError(int resId);

    void startMainActivity();

    void showRegisterError(int resId);
}
