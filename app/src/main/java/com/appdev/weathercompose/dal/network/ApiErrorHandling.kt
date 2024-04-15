package com.appdev.weathercompose.dal.network

import com.appdev.weathercompose.R
import com.appdev.weathercompose.utils.AppUtils
import com.appdev.weathercompose.constants.AppConstants
import com.appdev.weathercompose.info.generic.Resource
import okhttp3.ResponseBody

object ApiErrorHandling {

    fun <T> handleError(code: Int, error: ResponseBody?, message: String?): Resource<T> {
        if (code == AppConstants.UNAUTHORIZED) {
            // Logout User
        } else if (code == AppConstants.SERVER_ERROR || code == AppConstants.GATEWAY_ERROR) {
            return throwError(AppUtils.getString(R.string.server_error))
        } else if (error != null) {
            return handlerErrorResponseCase()
        }
        return throwError(message)
    }

    fun <T> throwError(message: String?): Resource<T> {
        return Resource.Error(message ?: "")
    }

    // No custom here thrown, sending generic message
    private fun <T> handlerErrorResponseCase(): Resource<T> {
        return throwError(AppUtils.getString(R.string.something_went_wrong_please_try_again))
    }
}