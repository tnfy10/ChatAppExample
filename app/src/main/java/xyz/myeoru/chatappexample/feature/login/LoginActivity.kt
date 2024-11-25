package xyz.myeoru.chatappexample.feature.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import xyz.myeoru.chatappexample.feature.main.MainActivity
import xyz.myeoru.chatappexample.ui.theme.ChatAppExampleTheme

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    companion object {
        fun startActivityWithClearAllTasks(context: Context) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        }
    }

    private var shouldShowSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            shouldShowSplashScreen
        }

        shouldShowSplashScreen = Firebase.auth.currentUser != null
        if (shouldShowSplashScreen) {
            MainActivity.startActivityWithClearAllTasks(this)
        }

        setContent {
            ChatAppExampleTheme {
                LoginScreen(
                    onNavigateToMain = {
                        MainActivity.startActivityWithClearAllTasks(this)
                    }
                )
            }
        }
    }
}