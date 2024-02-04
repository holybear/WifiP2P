package github.leavesczy.wifip2p




import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ParentActivityTest {

    @get : Rule
    var mActivityRule = ActivityScenarioRule(ParentActivity::class.java)


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("github.leavesczy.wifip2p", appContext.packageName)
    }

    @Test
    fun ClickTest(){

        onView(withId(R.id.btn_player)).perform(click())
    }

    @Test
    fun ClickTest1(){

        onView(withId(R.id.btn_filetransfer)).perform(click())
    }
}