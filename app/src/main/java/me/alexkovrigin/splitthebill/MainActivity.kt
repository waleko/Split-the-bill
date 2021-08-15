package me.alexkovrigin.splitthebill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import me.alexkovrigin.splitthebill.ui.CameraScreen
import me.alexkovrigin.splitthebill.ui.PhoneEnterScreen
import me.alexkovrigin.splitthebill.ui.QRInfoScreen
import me.alexkovrigin.splitthebill.ui.SMSCodeScreen
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        // FIXME: remove this trash. possibly replace with camera activity
        ActivityCompat.requestPermissions(
            this, CAMERA_PERMISSIONS, CAMERA_REQUEST_CODE
        )

        cameraExecutor = Executors.newSingleThreadExecutor()

        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel(MainActivityViewModel::class.java)
            val navController = rememberNavController()
            SplitTheBillTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val startDestination = if (viewModel.isAuthenticated)
                        "camera"
                    else
                        "enterPhone"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("enterPhone") {
                            PhoneEnterScreen { phone ->
                                viewModel.enterPhoneAsync(phone) {
                                    println("Phone entering success")
                                    navController.navigate("enterCode/$phone")
                                }
                            }
                        }
                        composable(
                            "enterCode/{phone}",
                            arguments = listOf(navArgument("phone") { type = NavType.StringType })
                        ) { backStackEntry ->
                            SMSCodeScreen(
                                phone = backStackEntry.arguments?.getString("phone") ?: error(""),
                                verifyPhoneWithCode = { phone, code ->
                                    viewModel.enterCodeAsync(phone, code) {
                                        println("Code sent! Authenticated: ${viewModel.isAuthenticated}")
                                        navController.navigate("camera") {
                                            // popUpTo("enterPhone") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }
                        activity("cameraActivity") {
                            activityClass = CameraActivity::class
                        }
                        composable("camera") {
                            val lock = Any()
                            var found = false
                            CameraScreen(
                                componentActivity = this@MainActivity,
                                cameraExecutor = cameraExecutor,
                                qrScanListener = { qr ->
                                    synchronized(lock) {
                                        if (found)
                                            return@synchronized
                                        println("Checking $qr")
                                        if (Regex("t=\\d{8}.*&s=[\\d.]*&fn=\\d*&i=\\d*&fp=\\d*&n=\\d*").matchEntire(qr) == null)
                                            return@synchronized
                                        found = true
                                        navController.navigate("ticketInfo/$qr")
                                    }
                                }
                            )
                        }
                        composable(
                            "ticketInfo/{qr}"
                        ) { backStackEntry ->
                            val qr = backStackEntry.arguments?.getString("qr") ?: error("")
                            QRInfoScreen(qr = qr, qrToReceipt = { onSuccess ->
                                viewModel.getReceiptAsync(qr) {
                                    onSuccess(it.second)
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SplitTheBillTheme {
        Greeting("Android")
    }
}