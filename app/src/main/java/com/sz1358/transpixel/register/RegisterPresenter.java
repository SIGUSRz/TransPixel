package com.sz1358.transpixel.register;
import com.sz1358.transpixel.R;

/**
 * Created by xxx on 18/12/2017.
 */

public class RegisterPresenter {
    private RegisterView view;
    private RegisterService service;

    public RegisterPresenter(RegisterView view, RegisterService service) {
        this.view = view;
        this.service = service;
    }

    public void requestRegister() {
        String username = view.getUsername();
        if (username.isEmpty()) {
            view.showUsernameError(R.string.username_error);
            return;
        }
        String password = view.getPassword();
        if (password.isEmpty()) {
            view.showPasswordError(R.string.password_error);
            return;
        }
        String email = view.getEmail();
        if (email.isEmpty()) {
            view.showEmailError(R.string.email_error);
            return;
        }
        boolean registerSucceeded = service.register(username, password, email);
        if (registerSucceeded) {
            view.startMainActivity();
            return;
        }
        view.showRegisterError(R.string.register_failed);
    }
}
