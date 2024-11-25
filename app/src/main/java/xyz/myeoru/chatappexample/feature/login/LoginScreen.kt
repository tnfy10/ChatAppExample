package xyz.myeoru.chatappexample.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import xyz.myeoru.chatappexample.R
import xyz.myeoru.chatappexample.constant.ProviderType
import xyz.myeoru.chatappexample.helper.rememberSocialLoginHelper
import xyz.myeoru.chatappexample.ui.theme.ChatAppExampleTheme
import xyz.myeoru.chatappexample.util.Logger

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit
) {
    val socialLoginHelper = rememberSocialLoginHelper()
    val scope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            text = {
                Text(errMsg)
            }
        )
    }

    LoginContainer(
        onClickLogin = { type ->
            isLoading = true
            scope.launch {
                socialLoginHelper.signIn(type).onCompletion {
                    isLoading = false
                }.catch {
                    Logger.e("로그인 실패", it)
                    errMsg = it.message.toString()
                    showErrorDialog = true
                }.collectLatest {
                    onNavigateToMain()
                }
            }
        },
        isLoading = isLoading
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginContainer(
    onClickLogin: (type: ProviderType) -> Unit,
    isLoading: Boolean
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .shadow(elevation = 1.dp, shape = CircleShape)
                        .background(color = Color(250, 230, 77), shape = CircleShape)
                        .clickable {
                            onClickLogin(ProviderType.KAKAO)
                        }
                        .width(280.dp)
                        .height(38.dp)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_kakao),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "카카오로 로그인",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color(34, 33, 26)
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .shadow(elevation = 1.dp, shape = CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                        .clickable {
                            onClickLogin(ProviderType.GOOGLE)
                        }
                        .width(280.dp)
                        .height(38.dp)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_24),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Google로 로그인",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.Black.copy(0.54f)
                        )
                    )
                }
            }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .pointerInteropFilter { true }
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    ChatAppExampleTheme {
        LoginContainer(
            onClickLogin = {},
            isLoading = false
        )
    }
}