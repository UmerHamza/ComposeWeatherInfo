package com.appdev.weathercompose.view.dashboard

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.appdev.weathercompose.R
import com.appdev.weathercompose.ui.theme.*
import com.appdev.weathercompose.utils.AppUtils
import com.appdev.weathercompose.utils.LocationUtils
import com.appdev.weathercompose.utils.OnLifecycleEvent
import com.appdev.weathercompose.viewModel.HomeViewModel
import com.appdev.weathercompose.widgets.AppButton
import com.appdev.weathercompose.widgets.AppText
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeView(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // check if location is already given
    val context = LocalContext.current

    val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            mLastLocation?.let {

            }
        }
    }
    val permissionState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val exitDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }


    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {

            }
            Lifecycle.Event.ON_RESUME -> {
                // if permission is not granted then request permission
                val isGranted = isLocationPermissionGranted(context)
                if (isGranted) {
                    handleLocation(context, mLocationCallback) {
                        // 31.475230737019267, 74.27078186855422
                        viewModel.fetchCurrentWeather(31.475230737019267, 74.27078186855422)
                        println("MainClass => lat lng => $it")
                    }
                    permissionState.value = false
                } else {
                    permissionState.value = true
                }
            }
            Lifecycle.Event.ON_STOP -> {

            }
            else -> {

            }
        }
    }


    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (loader, tvLocationName, tvTemp, tvDescription, ivIcon, clMainInfo, clFeelsInfo) = createRefs()

        if (viewModel.weatherHomeState.isLoading) {
            CircularProgressIndicator(color = DARK_BLUE, modifier = Modifier
                .constrainAs(loader) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                })
        } else if (!viewModel.weatherHomeState.errorMessage.isNullOrEmpty()) {
            AppUtils.showToast(viewModel.weatherHomeState.errorMessage)
        } else if (viewModel.weatherHomeState.weatherInfo != null) {
            val weatherInfo = viewModel.weatherHomeState.weatherInfo
            AppText(fontType = chakraFont, text = weatherInfo?.name, modifier = Modifier
                .constrainAs(tvLocationName) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                })

            AppText(textSize = 45.sp,
                text = AppUtils.convertKelvinToCelsius(weatherInfo?.main?.temp),
                modifier = Modifier
                    .constrainAs(tvTemp) {
                        start.linkTo(tvLocationName.start)
                        top.linkTo(tvLocationName.bottom, 20.dp)
                    })

            AsyncImage(
                model = AppUtils.fetchImageUrl(weatherInfo?.weather),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .constrainAs(ivIcon) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(tvTemp.top)
                    }
            )

            AppText(textSize = 15.sp,
                color = GREY,
                fontType = chakraFont,
                text = AppUtils.fetchWeatherDescription(weatherInfo?.weather),
                modifier = Modifier
                    .constrainAs(tvDescription) {
                        start.linkTo(tvTemp.start)
                        top.linkTo(tvTemp.bottom)
                    })

            Row(modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(color = DARK_BLUE)
                .padding(10.dp)
                .constrainAs(clMainInfo) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(tvDescription.bottom, 20.dp)
                    width = Dimension.fillToConstraints
                }) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.wind),
                        contentDescription = null
                    )
                    AppText(
                        text = "${weatherInfo?.wind?.speed ?: 0} m/s",
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.humidity),
                        contentDescription = null
                    )
                    AppText(
                        text = "${weatherInfo?.main?.humidity ?: 0} %",
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.pressure_gauge),
                        contentDescription = null
                    )

                    AppText(
                        text = "${weatherInfo?.main?.pressure ?: 0} hPa",
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
            Row(modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(color = DARK_BLUE)
                .padding(10.dp)
                .constrainAs(clFeelsInfo) {
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(clMainInfo.bottom, 20.dp)
                    width = Dimension.fillToConstraints
                }) {


            }
        }
    }



    if (permissionState.value) {
        RequestLocationPermission { isGranted ->
            if (isGranted) {
                handleLocation(context, mLocationCallback) {
                    println("MainClass => lat lng => $it")
                }
            } else {
                permissionState.value = false
                exitDialogState.value = true
            }
        }
    }

    if (exitDialogState.value) {
        ShowExitDialogAlert()
    }
}

fun handleLocation(
    context: Context,
    mLocationCallback: LocationCallback,
    callback: (LatLng?) -> Unit?
) {
    LocationUtils.handleLocationRequest(context, mLocationCallback) {
        callback.invoke(it)
    }
}

fun isLocationPermissionGranted(context: Context): Boolean {
    return AppUtils.checkIfPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
}

@Composable
fun RequestLocationPermission(permissionCallback: (Boolean) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionCallback.invoke(isGranted)
    }
    SideEffect {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@Composable
fun ShowExitDialogAlert() {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
        AlertDialog(
            title = {
                AppText(
                    text = stringResource(id = R.string.app_cannot_work_without_permission),
                    modifier = Modifier,
                    color = DARK_BLUE,
                    lines = 4,
                    fontType = chakraFont
                )
            },
            dismissButton = {},
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {
                AppButton(
                    callBack = {
                        showDialog.value = false
                        AppUtils.openPermissionSettings(context = context)
                    },
                    text = stringResource(id = R.string.settings),
                    textColor = OFF_BLACK,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .background(color = DARK_BLUE)
                )
            })
    }
}