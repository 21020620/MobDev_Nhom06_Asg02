import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.mobdev2.ui.screens.book.UserPreferences
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class UserPreferencesTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val userPreferences = UserPreferences(context)

    @Test
    fun testSaveAndRetrieveBackgroundColor() = runBlockingTest {

    }

    @Test
    fun testSaveAndRetrieveTextColor() = runBlockingTest {

    }
}