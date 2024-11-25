package xyz.myeoru.chatappexample.helper

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.oAuthCredential
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import xyz.myeoru.chatappexample.BuildConfig
import xyz.myeoru.chatappexample.constant.ProviderType
import xyz.myeoru.chatappexample.util.Logger
import kotlin.coroutines.cancellation.CancellationException

class SocialLoginHelper(
    private val context: Context
) {
    private fun getGoogleIdToken(context: Context): Flow<String> = flow {
        try {
            val credentialManager = CredentialManager.create(context)
            val signInWithGoogleOption =
                GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_SIGN_IN_SERVER_CLIENT_ID).build()
            val getCredRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()
            val result = credentialManager.getCredential(
                context = context,
                request = getCredRequest
            )
            val credential = result.credential as? CustomCredential
            if (credential?.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                emit(googleIdTokenCredential.idToken)
            } else {
                throw Exception("Unexpected type of credential")
            }
        } catch (e: GetCredentialCancellationException) {
            Logger.i("구글 로그인 취소됨")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun signInWithGoogle(context: Context) = getGoogleIdToken(context).flatMapConcat { token ->
        val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
        callbackFlow {
            Firebase.auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                val exception = task.exception
                when {
                    task.isSuccessful -> {
                        trySend(Unit)
                        close()
                    }

                    exception != null -> {
                        close(exception)
                    }
                }
            }
            awaitClose()
        }
    }

    private fun getKakaoIdToken(): Flow<String?> = callbackFlow {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    Logger.i("카카오 로그인 취소됨.")
                }

                error != null -> {
                    cancel(CancellationException("카카오 로그인 실패", error))
                }

                else -> {
                    trySend(token?.idToken)
                }
            }
            channel.close()
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                when {
                    error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                        callback(token, error)
                    }

                    error != null -> {
                        Logger.v("카카오톡으로 로그인 실패", error)
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    }

                    else -> {
                        callback(token, null)
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }

        awaitClose()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun signInWithKakao() = getKakaoIdToken().flatMapConcat { token ->
        val credential = oAuthCredential("oidc.kakao") {
            idToken = token
        }
        callbackFlow {
            Firebase.auth.signInWithCredential(credential).addOnCompleteListener { task ->
                val exception = task.exception
                when {
                    task.isSuccessful -> {
                        trySend(Unit)
                        close()
                    }

                    exception != null -> {
                        close(exception)
                    }
                }
            }
            awaitClose()
        }
    }

    fun signIn(type: ProviderType): Flow<Unit> = when (type) {
        ProviderType.GOOGLE -> signInWithGoogle(context)
        ProviderType.KAKAO -> signInWithKakao()
    }

    fun signOut() {
        Firebase.auth.signOut()
    }
}

@Composable
fun rememberSocialLoginHelper(): SocialLoginHelper {
    val context = LocalContext.current
    return remember {
        SocialLoginHelper(context)
    }
}