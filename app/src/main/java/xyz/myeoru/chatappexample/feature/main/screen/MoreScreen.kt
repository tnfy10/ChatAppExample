package xyz.myeoru.chatappexample.feature.main.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MoreScreen(
    innerPadding: PaddingValues
) {
    Surface(
        modifier = Modifier.padding(innerPadding)
    ) {
        Text("More")
    }
}