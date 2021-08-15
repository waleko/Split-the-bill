package me.alexkovrigin.splitthebill.ui.views

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import me.alexkovrigin.splitthebill.services.QRCodeAnalyzer
import java.util.concurrent.ExecutorService

/* FIXME: either fix camerascreen lifecycle + permissions, or connect cameraactivity to navcontroller

 */
@Composable
fun CameraScreen(
    componentActivity: ComponentActivity,
    cameraProviderFutureFunc: ListenableFuture<ProcessCameraProvider>.(previewView: PreviewView) -> Unit
) {
    ConstraintLayout {
        Button(onClick = { /*TODO*/ }) {
        }
        SimpleCameraPreview(cameraProviderFutureFunc = cameraProviderFutureFunc)
    }
}

@Composable
fun CameraScreen(
    componentActivity: ComponentActivity,
    cameraExecutor: ExecutorService,
    qrScanListener: (String) -> Unit
) {
    CameraScreen(componentActivity) { previewView ->
        addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            // val cameraExecutor = Executors.newSingleThreadExecutor()

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer(qrScanListener))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    componentActivity, cameraSelector, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraScreen", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(componentActivity))
    }
}