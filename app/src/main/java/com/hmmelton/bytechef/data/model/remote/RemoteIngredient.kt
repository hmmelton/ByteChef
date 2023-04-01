package com.hmmelton.bytechef.data.model.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class RemoteIngredient(
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    @get:PropertyName("order_num")
    @set:PropertyName("order_num")
    var orderNum: Int = 0
)

