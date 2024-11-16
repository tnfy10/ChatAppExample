package xyz.myeoru.chatappexample.feature.main

import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable
import xyz.myeoru.chatappexample.R

data class TopRoute<T : Any>(
    val name: String,
    val route: T,
    @DrawableRes val iconResId: Int
)

@Serializable
object FriendRoute

@Serializable
object ChatRoute

@Serializable
object MoreRoute

val topLevelRoutes = listOf(
    TopRoute(
        name = "친구",
        route = FriendRoute,
        iconResId = R.drawable.ic_person_24
    ),
    TopRoute(
        name = "채팅",
        route = ChatRoute,
        iconResId = R.drawable.ic_chat_bubble_24
    ),
    TopRoute(
        name = "더보기",
        route = MoreRoute,
        iconResId = R.drawable.ic_more_horiz_24
    )
)