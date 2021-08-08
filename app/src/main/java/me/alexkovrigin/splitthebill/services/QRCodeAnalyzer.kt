package me.alexkovrigin.splitthebill.services

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class QRCodeAnalyzer(val listener: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val options = FirebaseVisionBarcodeDetectorOptions
        .Builder()
        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
        .build()

    private val barcodeScanner = FirebaseVision.getInstance()
        .getVisionBarcodeDetector(options)

    @ExperimentalGetImage
    override fun analyze(proxy: ImageProxy) {
        proxy.use { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val rotation = imageProxy.imageInfo.rotationDegrees / 90
                val firebaseVisionImage = FirebaseVisionImage.fromMediaImage(mediaImage, rotation)
                barcodeScanner.detectInImage(firebaseVisionImage)
                    .addOnCompleteListener { barcodes ->
                        barcodes.result
                            ?.mapNotNull { it.rawValue }
                            ?.forEach { value ->
                                listener(value)
                            }
                    }
            }
        }
    }
}