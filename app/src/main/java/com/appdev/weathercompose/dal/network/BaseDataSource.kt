package com.appdev.weathercompose.dal.network

import com.appdev.weathercompose.R
import com.appdev.weathercompose.info.generic.Resource
import com.appdev.weathercompose.info.generic.ResponseObjectInfo
import com.appdev.weathercompose.utils.AppUtils
import retrofit2.Response
import java.net.UnknownHostException

open class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<ResponseObjectInfo<T>>): Resource<T> {
        val returnResponse: Resource<T>?
        try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful) {
                return Resource.Success(body?.data, body?.message ?: "")
            }
            return ApiErrorHandling.handleError(
                response.code(),
                response.errorBody(),
                response.message()
            )
        } catch (ex: Exception) {
            returnResponse = when (ex) {
                is UnknownHostException -> {
                    ApiErrorHandling.throwError(AppUtils.getString(R.string.no_internet_connection))
                }
                else -> {
                    ApiErrorHandling.throwError(ex.message ?: ex.toString())
                }
            }
        }
        return returnResponse ?: ApiErrorHandling.throwError("")
    }
}
