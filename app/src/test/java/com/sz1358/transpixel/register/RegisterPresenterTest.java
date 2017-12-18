package com.sz1358.transpixel.register;

import com.sz1358.transpixel.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by xxx on 18/12/2017.
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class RegisterPresenterTest {

    @Mock
    private RegisterView view;
    @Mock
    private RegisterService service;
    private RegisterPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new RegisterPresenter(view, service);
    }

    @Test
    public void shouldShowErrorMessageWhenUsernameIsEmpty() throws Exception {
        when(view.getUsername()).thenReturn("");
        presenter.requestRegister();

        verify(view).showUsernameError(R.string.username_error);
    }

    @Test
    public void shouldShowErrorMessageWhenPasswordIsEmpty() throws Exception {
        when(view.getUsername()).thenReturn("james");
        when(view.getPassword()).thenReturn("");
        presenter.requestRegister();

        verify(view).showPasswordError(R.string.password_error);
    }

    @Test
    public void shouldShowErrorMessageWhenEmailIsEmpty() throws Exception {
        when(view.getUsername()).thenReturn("james");
        when(view.getPassword()).thenReturn("bond");
        when(view.getEmail()).thenReturn("");
        presenter.requestRegister();

        verify(view).showEmailError(R.string.email_error);
    }

    @Test
    public void shouldStartMainActivityWhenUsernameAndPasswordAreCorrect() throws Exception {
        when(view.getUsername()).thenReturn("james");
        when(view.getPassword()).thenReturn("bond");
        when(view.getEmail()).thenReturn("jamesbond@gmail.com");
        when(service.register("james", "bond", "jamesbond@gmail.com")).thenReturn(true);
        presenter.requestRegister();

        verify(view).startMainActivity();
    }

    @Test
    public void shouldShowRegistererrorWhenUsernameAndPasswordAndEmailAreInvalid() throws Exception {
        when(view.getUsername()).thenReturn("james");
        when(view.getPassword()).thenReturn("bond");
        when(view.getEmail()).thenReturn("jamesbond@gmail.com");
        when(service.register("james", "bond", "jamesbond@gmail.com")).thenReturn(false);
        presenter.requestRegister();

        verify(view).showRegisterError(R.string.register_failed);
    }
}