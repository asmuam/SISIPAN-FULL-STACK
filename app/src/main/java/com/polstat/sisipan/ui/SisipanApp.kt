/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polstat.sisipan.ui

import com.polstat.sisipan.data.UserRepository
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.window.layout.DisplayFeature
import com.polstat.sisipan.R
import com.polstat.sisipan.ui.formasi.AddFormasi
import com.polstat.sisipan.ui.formasi.EditFormasi
import com.polstat.sisipan.ui.formasi.Formasi
import com.polstat.sisipan.ui.home.Home
import com.polstat.sisipan.ui.mahasiswa.AddMahasiswa
import com.polstat.sisipan.ui.mahasiswa.Mahasiswa
import com.polstat.sisipan.ui.pilihan.AddPilihan
import com.polstat.sisipan.ui.pilihan.EditPilihan
import com.polstat.sisipan.ui.pilihan.Pilihan
import com.polstat.sisipan.ui.profile.Profile
import com.polstat.sisipan.ui.signinsignup.SignInRoute
import com.polstat.sisipan.ui.signinsignup.SignUpRoute
import com.polstat.sisipan.ui.signinsignup.WelcomeRoute
import com.polstat.sisipan.ui.theme.SisipanTheme
import kotlinx.coroutines.launch


@Composable
fun SisipanApp(
    widthSizeClass: WindowWidthSizeClass,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    appState: SisipanAppState = rememberSisipanAppState()
) {
    SisipanTheme {
        if (appState.isOnline) {
            val navController = appState.navController
            val coroutineScope = rememberCoroutineScope()
            val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
            val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)
            if (sizeAwareDrawerState.isOpen) {
                BackHandler {
                    coroutineScope.launch {
                        sizeAwareDrawerState.close()
                    }
                }
            }
            ModalNavigationDrawer(
                drawerContent = {
                    AppDrawer(
                        currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty(),
                        navigateToHome = { navController.navigate(Screen.Home.route) },
                        navigateToFormasi = { navController.navigate(Screen.Formasi.route) },
                        navigateToWelcome = { navController.navigate(Screen.Welcome.route) },
                        navigateToMahasiswa = { navController.navigate(Screen.Mahasiswa.route) },
                        navigateToPilihan = { navController.navigate(Screen.Pilihan.route) },
                        closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                        logOut = { UserRepository.logOut(appState.navController.context.applicationContext)
                        }
                    )
                },
                drawerState = sizeAwareDrawerState,
                gesturesEnabled = !isExpandedScreen
            ) {
                Row {
//                    if (isExpandedScreen) {
//                        AppNavRail(
//                            currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty(),
//                            navigateToHome = { navController.navigate(Screen.Home.route) },
//                            navigateToFormasi = { navController.navigate(Screen.Formasi.route) },
//                            )
//                    }
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Welcome.route
//                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) { backStackEntry ->
                            Home(
                                openDrawer={ coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount ={
                                    navController.navigate("profil")
                                },
                                onAddPilihan ={
                                    navController.navigate("addPilihan")
                                },
                                onEditPilihan ={
                                    navController.navigate("${Screen.PilihanEdit.route}/${it}")
                                },
                            )
                        }
                        composable(Screen.Formasi.route) { backStackEntry ->
                            Formasi(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount ={
                                    navController.navigate("profil")
                                },
                                navigateToAddFormasi = {navController.navigate("addFormasi")},
                                onEditFormasi ={
                                    navController.navigate("${Screen.EditFormasi.route}/${it}")
                                },
                            )
                        }
                        composable(Screen.AddFormasi.route) {
                            AddFormasi(
                                navigateBack = { navController.popBackStack() },
                                onNavigateUp = { navController.navigateUp() },
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                            )
                        }
                        composable(Screen.EditFormasi.routeWithArgs,
                            arguments = listOf(navArgument(Screen.EditFormasi.formasiIdArg){
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            EditFormasi(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                navigateBack = { navController.popBackStack() },
                                onNavigateUp = { navController.navigateUp() },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                            )
                        }
                        composable(Screen.Pilihan.route) { backStackEntry ->
                            Pilihan(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                                navigateToEditPilihan ={
                                    navController.navigate("${Screen.PilihanEdit.route}/${it}")
                                },
                            )
                        }
                        composable(Screen.PilihanEdit.routeWithArgs,
                            arguments = listOf(navArgument(Screen.PilihanEdit.pilihanIdArg){
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            EditPilihan(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                navigateBack = { navController.popBackStack() },
                                onNavigateUp = { navController.navigateUp() },onAccount = {
                                    navController.navigate("profil")
                                },
                            )
                        }
                        composable(Screen.AddPilihan.route) {
                            AddPilihan(
                                navigateBack = { navController.popBackStack() },
                                onNavigateUp = { navController.navigateUp() },
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                            )
                        }
                        composable(Screen.Mahasiswa.route) { backStackEntry ->
                            Mahasiswa(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                                navigateToAddMahasiswa = {navController.navigate("addMahasiswa")},
                                )
                        }
                        composable(Screen.AddMahasiswa.route) { backStackEntry ->
                            AddMahasiswa(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount = {
                                    navController.navigate("profil")
                                },
                                navigateBack = { navController.popBackStack() },
                                onNavigateUp = { navController.navigateUp() },
                            )
                        }
                        composable(Screen.Profil.route) { backStackEntry ->
                            Profile(
                                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                                onAccount ={
                                    navController.navigate("profil")
                                },
                                navigateBack = { navController.popBackStack() },
                                )
                        }
                        composable(Screen.Welcome.route) { backStackEntry ->
                            WelcomeRoute(
                                onNavigateToSignIn = {
                                    navController.navigate("signin")
                                },
                                onNavigateToSignUp = {
                                    navController.navigate("signup")
                                },
                                onNavigateToHome = {
                                    navController.navigate("home")
                                },
                            )
                        }
                        composable(Screen.SignIn.route) {
                            SignInRoute(
                                onLoginSuccess = {
                                    navController.navigate(Screen.Home.route)
                                },
                                onNavUp = navController::navigateUp,
                            )
                        }

                        composable(Screen.SignUp.route) {
                            SignUpRoute(
                                onSignUpSubmitted = {
                                    navController.navigate(Screen.Welcome.route)
                                },
                                onNavUp = navController::navigateUp,
                            )
                        }

                        //another composable route
                    }
                }
            }
        } else {
            OfflineDialog { appState.refreshOnline() }
        }
    }
}


@Composable
fun OfflineDialog(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry_label))
            }
        }
    )
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}
