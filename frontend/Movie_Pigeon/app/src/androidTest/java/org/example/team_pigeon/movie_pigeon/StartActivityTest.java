package org.example.team_pigeon.movie_pigeon;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by Guo Mingxuan on 18/3/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartActivityTest {
    @Rule
    public ActivityTestRule<StartActivity> mActivityRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void loadLoginTable() {
        try {
            onView(withId(R.id.loginTable)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadSignInButton() {
        try {
            onView(withId(R.id.buttonSignIn)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTraktButton() {
        try {
            onView(withId(R.id.buttonTrakt)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadTmdbButton() {
        try {
            onView(withId(R.id.buttonTmdb)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadRegisterButton() {
        try {
            onView(withId(R.id.buttonRegister)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
    @Test
    public void loadForgetPwdButton() {
        try {
            onView(withId(R.id.buttonForgotPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
