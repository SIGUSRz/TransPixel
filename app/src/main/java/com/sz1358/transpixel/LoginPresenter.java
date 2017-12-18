package com.sz1358.transpixel;

/**
 * Created by xxx on 18/12/2017.
 *
 */

public class LoginPresenter {
    private LoginView view;
    private LoginService service;

    public LoginPresenter(LoginView View, LoginService Service) {
        this.view = View;
        this.service = Service;
    }

    public void requestLogin() {
        String username = view.getUsername();
        if(username.isEmpty()) {
            view.showUsernameError(R.string.username_error);
            return;
        }
        String password = view.getPassword();
        if(password.isEmpty()){
            view.showPasswordError(R.string.password_error);
            return;
        }

        boolean loginSucceeded = service.login(username, password);
        if(loginSucceeded) {
            view.startMainActivity();
            return;
        }
        view.showLoginError(R.string.login_failed);
    }
}
