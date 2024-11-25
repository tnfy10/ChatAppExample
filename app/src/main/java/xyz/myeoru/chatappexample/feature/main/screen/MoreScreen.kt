package xyz.myeoru.chatappexample.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import xyz.myeoru.chatappexample.feature.login.LoginActivity
import xyz.myeoru.chatappexample.helper.rememberSocialLoginHelper

@Composable
fun MoreScreen(
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val socialLoginHelper = rememberSocialLoginHelper()

    Surface(
        modifier = Modifier.padding(innerPadding)
    ) {
        Column {
            Text("More")
            Button(
                onClick = {
                    socialLoginHelper.signOut()
                    LoginActivity.startActivityWithClearAllTasks(context)
                }
            ) {
                Text("로그아웃")
            }
        }
    }
}