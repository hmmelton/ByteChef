package com.hmmelton.bytechef.data.model.remote

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class RemoteInstruction(
    val description: String,
    @get:PropertyName("step_num")
    @set:PropertyName("step_num")
    var stepNum: Int
)
