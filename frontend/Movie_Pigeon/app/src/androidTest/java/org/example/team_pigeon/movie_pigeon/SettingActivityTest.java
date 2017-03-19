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
public class SettingActivityTest {
    @Rule
    public ActivityTestRule<SettingActivity> mActivityRule = new ActivityTestRule<>(SettingActivity.class);

    @Test
    public void loadToolbar() {
        try {
            onView(withId(R.id.toolbar_setting_page)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadFrameLayout() {
        try {
            onView(withId(R.id.frame_layout_setting_page)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadChangUsernameButton() {
        try {
            onView(withId(R.id.username_setting_page)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadChangePasswordButton() {
        try {
            onView(withId(R.id.password_setting_page)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
