package me.alexkovrigin.splitthebill.ui.views

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import me.alexkovrigin.splitthebill.services.QRCodeAnalyzer
import java.util.concurrent.Executor

@Composable
fun SimpleCameraPreview(
    qrScanListener: (String) -> Unit,
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Preview is incorrectly scaled in Compose on some devices without this
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider = cameraProviderFuture.get()

                bindToLifecycle(
                    lifecycleOwner,
                    previewView,
                    cameraProvider,
                    executor,
                    qrScanListener
                )

            }, executor)

            previewView
        }
    )
}

private fun bindToLifecycle(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider,
    executor: Executor,
    qrScanListener: (String) -> Unit
) {
    // Preview
    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    val imageAnalysis = ImageAnalysis.Builder()
        .build()
        .also {
            it.setAnalyzer(executor, QRCodeAnalyzer(qrScanListener))
        }

    // Select back camera as a default
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()

        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageAnalysis
        )
    } catch (exc: Exception) {
        Log.e("CameraScreen", "Use case binding failed", exc)
    }
}