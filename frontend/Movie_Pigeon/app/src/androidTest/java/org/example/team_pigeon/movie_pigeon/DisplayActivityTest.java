package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.example.team_pigeon.movie_pigeon.models.Movie;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by SHENGX on 2017/3/19.
 */
@RunWith(AndroidJUnit4.class)
public class DisplayActivityTest {
    @Rule
    public ActivityTestRule<DisplayActivity> displayActivityActivityTestRule = new ActivityTestRule<DisplayActivity>(DisplayActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            ArrayList<Movie> testMovieList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                testMovieList.add(new Movie(String.valueOf(i), "id"));
            }
            Bundle argument = new Bundle();
            argument.putSerializable("movieList", testMovieList);
            argument.putString("title", "TestBar");
            argument.putString("type", "test");
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, DisplayActivity.class);
            result.putExtra("bundle",argument);
            return result;
        }
    };

    @Test
    public void showMovieListProperly() {
        onView(withId(R.id.toolbar_display_page)).check(matches(isDisplayed()));
        onView(withId(R.id.list_movies)).check(matches(isDisplayed()));
        for(int i = 0;i<10;i++) {
            onData(withText(String.valueOf(i))).inAdapterView(withId(R.id.list_movies)).atPosition(i);
        }
    }

}
