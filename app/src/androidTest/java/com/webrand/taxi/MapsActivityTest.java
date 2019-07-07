package com.webrand.taxi;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(JUnit4.class)
public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> mapsActivityActivityTestRule=
            new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void clickOrderButton_opensOrderDialogUI(){
        onView(withId(R.id.btn_order_taxi))
                .perform(click());

        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));
    }
}
