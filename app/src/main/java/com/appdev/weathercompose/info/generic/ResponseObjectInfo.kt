package com.appdev.weathercompose.info.generic

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseObjectInfo<T>(
    @SerializedName("error")
    var error: Boolean? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName(value = "user", alternate = ["data"])
    var data: T? = null
) : Serializable
