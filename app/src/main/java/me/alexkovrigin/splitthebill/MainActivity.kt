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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.gson.Gson
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.ui.views.camera.CameraScreen
import me.alexkovrigin.splitthebill.ui.views.login.PhoneEnterScreen
import me.alexkovrigin.splitthebill.ui.views.login.SMSCodeScreen
import me.alexkovrigin.splitthebill.ui.views.receiptsplitting.ReceiptSplittingScreen
import me.alexkovrigin.splitthebill.ui.views.summary.SummaryScreen
import me.alexkovrigin.splitthebill.ui.views.userselection.PayerSelectScreen
import me.alexkovrigin.splitthebill.utilities.fromJson
import me.alexkovrigin.splitthebill.viewmodels.MainActivityViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    @ExperimentalPagerApi
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
                                    navController.navigate("payerSelection/$qr")
                                },
                                viewModel = viewModel
                            )
                        }
                        composable(
                            "ticketInfo/{qr}?users={users}"
                        ) { backStackEntry ->
                            val qr =
                                backStackEntry.arguments?.getString("qr") ?: error("No qr provided")
                            val usersString = backStackEntry.arguments?.getString("users") ?: "[]"
                            val users = Gson().fromJson<List<User>>(usersString)
                            ReceiptSplittingScreen(
                                qr = qr,
                                users = users,
                                viewModel = viewModel,
                                navigateToSummary = { summaryUID ->
                                    navController.navigate("summary/$summaryUID")
                                }
                            )
                        }
                        composable(
                            "payerSelection/{qr}"
                        ) { backStackEntry ->
                            val qr =
                                backStackEntry.arguments?.getString("qr") ?: error("No qr provided")
                            PayerSelectScreen(
                                viewModel = viewModel,
                                navigateToReceiptSplitting = { users ->
                                    val usersString = Gson().toJson(users)
                                    navController.navigate("ticketInfo/$qr?users=$usersString")
                                }
                            )
                        }
                        composable(
                            "summary/{uid}"
                        ) { backStackEntry ->
                            val uid = backStackEntry.arguments?.getString("uid")
                                ?: error("No uid provided")
                            SummaryScreen(
                                uid = uid,
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
