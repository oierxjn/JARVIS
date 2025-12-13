package org.oierxjn.jarvis.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.navigation.compose.composable
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import org.oierxjn.jarvis.R
import org.oierxjn.jarvis.ScreenBase
import org.oierxjn.jarvis.model.DataModel

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

    Scaffold(
        modifier = modifier,
        bottomBar = { BottomToolBar(navController) }
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
    ScreenBase(
        modifier,
    ){
        Greeting("Home")
    }
}

@Composable
fun Settings(
    modifier: Modifier = Modifier
) {
    ScreenBase(
        modifier,
    ) {
        Column {
            Row {
                Button({}){
                    Text("保存")
                }
            }
            Card() {
                val innerModifier = Modifier.padding(16.dp)
                Column (innerModifier){
                    var aiApiEndPoint by remember { mutableStateOf(DataModel.settingData.ai_endpoint) }
                    var aiApiKey by remember { mutableStateOf(DataModel.settingData.ai_api_key) }
                    var aiModel by remember { mutableStateOf(DataModel.settingData.ai_model) }

                    Text("AI配置")
                    OutlinedTextField(
                        value = aiApiEndPoint,
                        label = { Text("AI API端点")},
                        onValueChange = { aiApiEndPoint = it },
                        singleLine = true,
                    )
                }
            }
        }
    }
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
fun BottomToolBar(
    navController: NavController,
){
    val startDestination = Destination.Home
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {
                    navController.navigate(destination.route)
                    selectedDestination = index
                },
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