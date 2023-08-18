package com.example.contactsharetest

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.contactsharetest.modelTest.ContactScreen
import com.example.contactsharetest.modelTest.ContactStore
import com.example.contactsharetest.navigation.ContactNavigation
import com.example.contactsharetest.ui.theme.ContactShareTestTheme
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val STRATEGY = Strategy.P2P_STAR
    private lateinit var connectionsClient : ConnectionsClient
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    private var opponentName: String? = null
    private var opponentEndpointId: String? = null

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            // Accepting a connection means you want to receive messages. Hence, the API expects
            // that you attach a PayloadCall to the acceptance

        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {

            }
        }

        override fun onDisconnected(endpointId: String) {
            resetGame()
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient.requestConnection("not implemented", endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
        }
    }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {

            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Determines the winner and updates game state/UI after both players have chosen.
            // Feel free to refactor and extract this code into a different method
            if (update.status == PayloadTransferUpdate.Status.SUCCESS){

                setGameControllerEnabled(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactShareTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    Greeting("Android")
                    ContactNavigation()
                }
            }
        }

        connectionsClient = Nearby.getConnectionsClient(this)

        resetGame()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @CallSuper
    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_REQUIRED_PERMISSIONS
            )
        }
    }

    fun startAdvertising() {
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
            "Jojo",
            packageName,
            connectionLifecycleCallback,
            options
        )
    }

    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val errMsg = "Cannot start without required permissions"
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            grantResults.forEach {
                if (it == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
            }
            recreate()
        }
    }

    /** Sends the user's selection of rock, paper, or scissors to the opponent. */

//    private fun sendGameChoice(choice: GameChoice) {
////        myChoice = choice
//        connectionsClient.sendPayload(
//            opponentEndpointId!!,
//            Payload.fromBytes(choice.name.toByteArray(StandardCharsets.UTF_8))
//        )
////        binding.status.text = "You chose ${choice.name}"
//        // For fair play, we will disable the game controller so that users don't change their
//        // choice in the middle of a game.
//        setGameControllerEnabled(false)
//    }

    /**
     * Enables/Disables the rock, paper and scissors buttons. Disabling the game controller
     * prevents users from changing their minds after making a choice.
     */
    private fun setGameControllerEnabled(state: Boolean) {
//        binding.apply {
//            rock.isEnabled = state
//            paper.isEnabled = state
//            scissors.isEnabled = state
//        }
    }
    /** Wipes all game state and updates the UI accordingly. */
    private fun resetGame() {
        // reset data

    }

    fun startDiscovery(){
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(packageName,endpointDiscoveryCallback,options)
    }

    @CallSuper
    override fun onStop(){
        connectionsClient.apply {
            stopAdvertising()
            stopDiscovery()
            stopAllEndpoints()
        }
        resetGame()
        super.onStop()
    }

}

@Composable
fun HomePage(navController: NavController){

    var imageUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit){
        ContactStore().getContact(context).collect{ contact ->
            name = contact.name
            phoneNumber = contact.phoneNumber
            email = contact.email
            imageUri = contact.imageUri
        }
    }

    /**
    *
    */

    Box(modifier = Modifier.fillMaxSize()) {
//        val image = painterResource(id = R.drawable.person)
        val painter = rememberAsyncImagePainter(
            ImageRequest
                .Builder(LocalContext.current)
                .data(data = imageUri)
                .build()
        )
        Image(modifier = Modifier.fillMaxSize(),painter = painter, contentDescription = null, contentScale = ContentScale.Crop)

        Box(modifier = Modifier
            .align(Alignment.TopStart)
            .padding(10.dp)
            .size(35.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .clickable {

            }){
            Icon(painter = painterResource(id = R.drawable.share), contentDescription = "share", modifier = Modifier
                .align(
                    Alignment.Center
                )
                .size(20.dp))
        }

        Box(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)
            .size(35.dp)
            .background(color = Color.White, shape = RoundedCornerShape(5.dp))
            .clickable {
                navController.navigate(ContactScreen.EditDetailsPage.name)
            }){
            Icon(painter = painterResource(id = R.drawable.pencil), contentDescription = "edit", modifier = Modifier
                .align(
                    Alignment.Center
                )
                .size(20.dp))
        }


        Column(modifier = Modifier
            .offset(0.dp,250.dp)
            .align(Alignment.Center)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = name,  fontWeight = FontWeight.Bold, fontSize = 50.sp, modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
            Text(text = phoneNumber, modifier = Modifier.fillMaxWidth(),fontWeight = FontWeight.Bold,fontSize = 20.sp, textAlign = TextAlign.Center)
            Text(text = email, modifier = Modifier.fillMaxWidth(),fontWeight = FontWeight.Bold,fontSize = 20.sp,textAlign = TextAlign.Center)
        }

    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContactShareTestTheme {

    }
}