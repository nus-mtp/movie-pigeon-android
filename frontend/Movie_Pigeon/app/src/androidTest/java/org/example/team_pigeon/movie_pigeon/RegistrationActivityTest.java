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
 * Created by Guo Mingxuan on 19/3/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationActivityTest {
    @Rule
    public ActivityTestRule<RegistrationActivity> mActivityRule = new ActivityTestRule<>(RegistrationActivity.class);

    @Test
    public void loadUsernameTextView() {
        try {
            onView(withId(R.id.rTVUsername)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadUsernameEdittext() {
        try {
            onView(withId(R.id.rETUsername)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadEmailTextView() {
        try {
            onView(withId(R.id.rTVEmail)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadEmailEdittext() {
        try {
            onView(withId(R.id.rETEmail)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPasswordTextView() {
        try {
            onView(withId(R.id.rTVPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPasswordEdittext() {
        try {
            onView(withId(R.id.rETPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmPasswordTextView() {
        try {
            onView(withId(R.id.rTVConfirmPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmPasswordEdittext() {
        try {
            onView(withId(R.id.rETConfirmPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadRegisterButton() {
        try {
            onView(withId(R.id.rpRegisterButton)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadBackButton() {
        try {
            onView(withId(R.id.rpBackButton)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
