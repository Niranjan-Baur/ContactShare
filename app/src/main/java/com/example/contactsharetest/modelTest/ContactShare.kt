package com.example.contactsharetest.modelTest

import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Strategy

class ContactShare {

    private val STRATEGY = Strategy.P2P_STAR
    private lateinit var connectionsClients : ConnectionsClient

    /**
     * The request code for verifying our call to [requestPermissions]. Recall that calling
     * [requestPermissions] leads to a callback to [onRequestPermissionsResult]
     */
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1



}