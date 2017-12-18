package com.sz1358.transpixel;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by xxx on 19/12/2017.
 */
public class UserTest {
    @Test
    public void getId() throws Exception {
        User superman = new User(17, "Superman", "superman@gmail.com", 0);
        int userId = superman.getId();
        assertEquals(17, userId);
    }

    @Test
    public void getUsername() throws Exception {
        User batman = new User(18, "Batman", "batman@gmail.com", 0);
        String userName = batman.getUsername();
        assertEquals("Batman", userName);
    }

    @Test
    public void getEmail() throws Exception {
        User leaderYang = new User(19, "LeaderYang", "leaderyang@gmail.com", 1);
        String email = leaderYang.getEmail();
        assertEquals("leaderyang@gmail.com", email);
    }

    @Test
    public void getLang() throws Exception {
        User duckJr = new User(19, "duckJr", "duckJr@gmail.com", 1);
        int lang = duckJr.getLang();
        assertEquals(1, lang);
    }

}