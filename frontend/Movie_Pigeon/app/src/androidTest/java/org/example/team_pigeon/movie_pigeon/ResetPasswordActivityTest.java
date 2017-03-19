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
public class ResetPasswordActivityTest {

    @Rule
    public ActivityTestRule<ResetPasswordActivity> mActivityRule = new ActivityTestRule<>(ResetPasswordActivity.class);

    @Test
    public void loadTitleTextView() {
        try {
            onView(withId(R.id.tvResetPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadHintTextView() {
        try {
            onView(withId(R.id.rsHint)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadResetTable() {
        try {
            onView(withId(R.id.resetTable)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadVCodeButton() {
        try {
            onView(withId(R.id.rsBVerificationCode)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadBackButton() {
        try {
            onView(withId(R.id.rsBBack)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
