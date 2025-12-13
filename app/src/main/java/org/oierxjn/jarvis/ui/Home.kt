package org.oierxjn.jarvis.ui


import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.composable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import org.oierxjn.jarvis.R

enum class Destination(
    val route: String,
    val label: String,
    val iconResourceId: Int,
    val contentDescription: String
){
    Home("home", "Home", R.drawable.home_24dp, "Home"),
    Settings("settings", "Settings", R.drawable.settings_24dp, "Settings")
}


@Composable
fun MainHome(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
//    val startDestination = Destination.Home

    Scaffold(
        modifier = modifier,
        bottomBar = { BottomToolBar() }
    ){ innerPadding ->
        HomeNavHost(
            navController,
            startDestination = Destination.Home,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    Greeting("这是HOME", modifier)
}

@Composable
fun Settings(
    modifier: Modifier = Modifier
) {
    Greeting("这是Settings", modifier)
}


@Composable
fun HomeNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
    ){
        Destination.entries.forEach { destination ->
            composable(destination.route){
                when(destination){
                    Destination.Home -> Home(modifier)
                    Destination.Settings -> Settings(modifier)
                }
            }
        }
    }
}

@Composable
fun BottomToolBar(){
    val selectedDestination by rememberSaveable { mutableIntStateOf(Destination.Home.ordinal) }

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {},
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResourceId),
                        contentDescription = destination.contentDescription,
                    )
                },
                label = { Text(destination.label) }
            )
        }
    }
}


/**
 * 用于测试
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}