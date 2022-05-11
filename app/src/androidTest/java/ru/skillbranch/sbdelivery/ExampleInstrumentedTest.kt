package ru.skillbranch.sbdelivery

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.skillbranch.sbdelivery.data.AppError

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext!!

    @Before
    fun method() {
    }

    @Test
    fun test_enum_AppError_and_Resources() {
        repeat(AppError.values().count()) {
            appContext.resources.getIdentifier("it.msgId", "string", appContext.packageName)
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ru.skillbranch.sbdelivery", appContext.packageName)
    }
}
