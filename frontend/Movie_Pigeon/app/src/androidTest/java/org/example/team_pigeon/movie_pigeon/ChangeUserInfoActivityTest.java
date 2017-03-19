package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
public class ChangeUserInfoActivityTest {

    @Rule
    public ActivityTestRule<ChangeUserInfoActivity> mActivityRule = new ActivityTestRule<ChangeUserInfoActivity>(ChangeUserInfoActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, ThirdPartySignupActivity.class);
            result.putExtra("type", "username");
            return result;
        }
    };

    @Test
    public void loadToolbar() {
        try {
            onView(withId(R.id.toolbar_user_activity)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadFrameLayout() {
        try {
            onView(withId(R.id.fl_user_activity)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadUsernameTextView() {
        try {
            onView(withId(R.id.setting_fragment_username_tv)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadUsernameEdittext() {
        try {
            onView(withId(R.id.setting_fragment_username_et)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadCurrentPasswordTextView() {
        try {
            onView(withId(R.id.setting_fragment_now_password_tv)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadCurrentPasswordEdittext() {
        try {
            onView(withId(R.id.setting_fragment_now_password_et)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadNewPasswordTextView() {
        try {
            onView(withId(R.id.setting_fragment_password_tv)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadNewPasswordEdittext() {
        try {
            onView(withId(R.id.setting_fragment_password_et)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmPasswordTextView() {
        try {
            onView(withId(R.id.setting_fragment_repeat_password_tv)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmPasswordEdittext() {
        try {
            onView(withId(R.id.setting_fragment_repeat_password_et)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmButton() {
        try {
            onView(withId(R.id.setting_fragment_confirm)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadCancelButton() {
        try {
            onView(withId(R.id.setting_fragment_cancel)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
