package me.alexkovrigin.splitthebill.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun CameraScreen(
    qrScanListener: (String) -> Unit,
    returnToHomeScreen: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            Column {
                Text("The camera is important for this app. Please grant the permission.")
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Accept")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = returnToHomeScreen) {
                        Text("Decline")
                    }
                }
            }
        },
        permissionNotAvailableContent = {
            Column {
                Text(
                    "Camera permission denied. " +
                        "We need access to your camera to scan a QR code on your receipt. " +
                        "Please, grant us access on the Settings screen."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {/*FIXME*/}) {
                    Text("Open Settings")
                }
            }
        }
    ) {
        ConstraintLayout {
            SimpleCameraPreview(qrScanListener)
        }
    }
}