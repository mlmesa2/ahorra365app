package com.mlmesa.savingdays

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.ui.components.BottomNavigationBar
import com.mlmesa.savingdays.ui.navigation.NavigationGraph
import com.mlmesa.savingdays.ui.navigation.Screen
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.worker.DailyNotificationReminder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var reminderManager: DailyNotificationReminder

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateResultLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>
    private val updateType = AppUpdateType.FLEXIBLE

    private val installStateUpdatedListener =
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // Download is finished. Show a notification
                Log.d("MainActivity", "Update downloaded. Ready to install.")
                mainViewModel.onUpdateDownloaded()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    
        installSplashScreen().setKeepOnScreenCondition {
            mainViewModel.userPreferences.value == null
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateManager.registerListener(installStateUpdatedListener)
        appUpdateResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e("MainActivity", "Update flow failed! Result code: ${result.resultCode}")
            }
        }
        checkForAppUpdate()
        setContent {
            Saving365Theme {
                val showUpdateDialog by mainViewModel.showUpdateDialog.collectAsStateWithLifecycle()
                val userPreferences by mainViewModel.userPreferences.collectAsStateWithLifecycle()

                // Wait for preferences to load (Splash screen covers this)
                if (userPreferences == null) {
                    return@Saving365Theme
                }

                val onboardingCompleted = userPreferences!!.onboardingCompleted
                val startDestination = if (onboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                if (showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = { Text("Actualización descargada") },
                        text = { Text("La nueva versión de la aplicación ha sido descargada. Reinicia para instalarla.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    appUpdateManager.completeUpdate()
                                    mainViewModel.hideUpdateDialog()
                                }
                            ) {
                                Text("Reiniciar")
                            }
                        },
                    )
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Log.d("MYTAG", "onCreate: Current route: $currentRoute")
                Log.d("MYTAG", "onCreate: onBoardingcomplete: ${userPreferences!!.onboardingCompleted}")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Hide bottom bar during onboarding. Use null check to allow initial composition but
                        // rely on reactive state update
                        if (currentRoute != Screen.Onboarding.route && currentRoute != null) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavigationGraph(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }

            LaunchedEffect(Unit) {
                reminderManager.reSchedule()
            }
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            val isUpdateAvailable =
                updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> updateInfo.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> updateInfo.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                startUpdateFlow(updateInfo)
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            appUpdateResultLauncher,
            AppUpdateOptions.newBuilder(updateType).build()
        )
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                mainViewModel.onUpdateDownloaded()
            }
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(info)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}