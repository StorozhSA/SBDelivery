package ru.storozh.models

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.runner.RunWith
import ru.storozh.models.delivery.database.DeliveryDatabaseService
import ru.storozh.models.delivery.database.domains.User

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ServiceDatabaseTest {

    companion object {
        private lateinit var service: DeliveryDatabaseService
        private lateinit var contextInstrumentation: Context
        private lateinit var contextApplication: Context

        @BeforeClass
        @JvmStatic
        fun setup() {
            // Init contexts
            contextInstrumentation = InstrumentationRegistry.getInstrumentation().context
            contextApplication = ApplicationProvider.getApplicationContext()

            // Включаем режим создания базы в RAM. Передаем в контекст приложения параметр за это отвечающий.
            contextApplication
                .getSharedPreferences(contextApplication.packageName, Context.MODE_PRIVATE)
                .edit()
                .apply {
                    putBoolean("dbIsRAM", true)
                    commit()
                }

            service = DeliveryDatabaseService.getInstance(contextApplication)
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            // clean up after this class, leave nothing dirty behind
        }
    }

    @Before
    @After
    fun clearAlltables() {
        service.userDao().delete()
    }

    @Test
    fun testTags() {
        val entity = User(
            id = "12345",
            accessToken = "qazwsx",
            refreshToken = "qazxsw",
            email = "zzz@zxxx.com",
            firstName = "Ivanov",
            lastName = "Ivan"
        )
        service.userDao().upsert(
            listOf(entity)
        )
        Assert.assertEquals(1, service.userDao().recordsCount())
        Assert.assertEquals(listOf(entity), service.userDao().get("12345"))
    }
}
