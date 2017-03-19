package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by SHENGX on 2017/3/19.
 */

@RunWith(AndroidJUnit4.class)
public class HomePageActivityTest {
    @Rule
    public ActivityTestRule<HomePageActivity> homePageActivityActivityTestRule = new ActivityTestRule<HomePageActivity>(HomePageActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, HomePageActivity.class);
            result.putExtra("Token", "test token");
            return result;
        }
    };

    @Test
    public void ensureNavigationButtonsAreDisplayed() {
        onView(withId(R.id.rg_tab_bar))
                .check(matches(isDisplayed()));
        onView(withId(R.id.rb_now_showing))
                .check(matches(isDisplayed()));
        onView(withId(R.id.rb_home))
                .check(matches(isDisplayed()));
        onView(withId(R.id.rb_me))
                .check(matches(isDisplayed()));
    }

    @Test
    public void viewPagerDisplayed() {
        onView(withId(R.id.view_pager))
                .check(matches(isDisplayed()));
    }

    @Test
    public void viewPagerSwitchToCinemaPageCorrectly() {
        onView(withText("Cinemas")).perform(click());
        onView(withId(R.id.spinner_cinema_brand)).check(matches(isDisplayed()));
    }

    @Test
    public void viewPagerSwitchToMePageCorrectly() {
        onView(withText("Me")).perform(click());
        onView(withText("My Ratings")).check(matches(isDisplayed()));
        onView(withText("My Bookmarks")).check(matches(isDisplayed()));
        onView(withText("Settings")).check(matches(isDisplayed()));
        onView(withText("Logout")).check(matches(isDisplayed()));
    }

    @Test
    public void viewPagerSwitchToHomePageCorrectly() {
        onView(withText("Home")).perform(click());
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));
        onView(withId(R.id.grid_now_showing)).check(matches(isDisplayed()));
        onView(withId(R.id.grid_recommended)).check(matches(isDisplayed()));
    }
}
