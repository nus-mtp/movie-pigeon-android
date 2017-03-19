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
public class ThirdPartySignupActivityTest {
    @Rule
    public ActivityTestRule<ThirdPartySignupActivity> mActivityRule = new ActivityTestRule<ThirdPartySignupActivity>(ThirdPartySignupActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, ThirdPartySignupActivity.class);
            result.putExtra("thirdParty", "The Movie DB");
            return result;
        }
    };

    @Test
    public void loadPlaceholderTextView() {
        try {
            onView(withId(R.id.tpsTitle)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadInputEdittext() {
        try {
            onView(withId(R.id.tpsInput)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPasswordTextView() {
        try {
            onView(withId(R.id.tpsPasswordTitle)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadPasswordEdittext() {
        try {
            onView(withId(R.id.tpsPassword)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadConfirmButton() {
        try {
            onView(withId(R.id.tpsConfirmButton)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadBackButton() {
        try {
            onView(withId(R.id.tpsBackButton)).check(matches(isDisplayed()));
        } catch (AssertionFailedError e) {
            e.printStackTrace();
        }
    }
}
