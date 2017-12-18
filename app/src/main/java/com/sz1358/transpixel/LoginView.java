package com.sz1358.transpixel;

/**
 * Created by xxx on 18/12/2017.
 *
 */

public interface LoginView {
    String getUsername();

    void showUsernameError(int resId);

    String getPassword();

    void showPasswordError(int resId);

    void startMainActivity();

    void showLoginError(int resId);
}
