package me.alexkovrigin.splitthebill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.ui.views.CameraScreen
import me.alexkovrigin.splitthebill.ui.views.PhoneEnterScreen
import me.alexkovrigin.splitthebill.ui.views.QRInfoScreen
import me.alexkovrigin.splitthebill.ui.views.SMSCodeScreen
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {

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
                    println(startDestination)

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("enterPhone") {
                            PhoneEnterScreen(navigateToEnterCode = { phone ->
                                navController.navigate("enterCode/${phone}")
                            }, viewModel = viewModel)
                        }
                        composable(
                            "enterCode/{phone}",
                            arguments = listOf(navArgument("phone") { type = NavType.StringType })
                        ) { backStackEntry ->
                            SMSCodeScreen(
                                phone = backStackEntry.arguments?.getString("phone") ?: error(""),
                                navigateToHome = {
                                    navController.navigate("camera")
                                    // TODO: 15.08.2021 Set camera as start destination
                                },
                                viewModel = viewModel
                            )
                        }
                        composable("camera") {
                            CameraScreen(
                                navigateHome = { navController.navigate("home") },
                                navigateToQR = { qr ->
                                    viewModel.loadReceiptAsync(qr, onSuccess = {})
                                    navController.navigate("ticketInfo/$qr")
                                },
                                viewModel = viewModel
                            )
                        }
                        composable(
                            "ticketInfo/{qr}"
                        ) { backStackEntry ->
                            val qr = backStackEntry.arguments?.getString("qr") ?: error("")
                            QRInfoScreen(
                                qr = qr,
                                viewModel = viewModel
                            )
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
