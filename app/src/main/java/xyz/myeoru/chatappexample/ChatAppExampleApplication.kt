package xyz.myeoru.chatappexample

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp
import xyz.myeoru.chatappexample.util.Logger

@HiltAndroidApp
class ChatAppExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
        )
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        Logger.d("키해시: ${Utility.getKeyHash(applicationContext)}")
    }
}