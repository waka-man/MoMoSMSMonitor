package com.waka.momosmsmonitor

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.waka.momosmsmonitor.receivers.SMSReceiver
import com.waka.momosmsmonitor.services.SMSMonitorService
import com.waka.momosmsmonitor.ui.theme.MoMoSMSMonitorTheme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS
    )
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Log.d(TAG, "All permissions granted")
            startSMSMonitoring()
        } else {
            Log.e(TAG, "Some permissions were denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkAndRequestPermissions()
        
        setContent {
            MoMoSMSMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onTestSMS = { simulateTestSMS() },
                        onStartService = { startSMSMonitoring() }
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (requiredPermissions.all { 
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED 
        }) {
            Log.d(TAG, "All permissions already granted")
            startSMSMonitoring()
        } else {
            Log.d(TAG, "Requesting permissions")
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private fun startSMSMonitoring() {
        // Start the foreground service
        val serviceIntent = Intent(this, SMSMonitorService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        
        // Register SMS receiver
        val receiver = SMSReceiver()
        val filter = IntentFilter(SMS_RECEIVED_ACTION)
        registerReceiver(receiver, filter)
        
        Log.d(TAG, "SMS monitoring started")
    }

    private fun simulateTestSMS() {
        val intent = Intent(SMS_RECEIVED_ACTION)
        val testMessage = """
            TxId: 123456789. Your payment of 1,000 RWF to Test Merchant 12345 has been completed at 2024-03-19 10:00:00. 
            Your new balance: 9,000 RWF. Fee was 0 RWF.
        """.trimIndent()
        
        // Create a test SMS message
        val smsMessage = SmsMessage.createFromPdu(
            testMessage.toByteArray(),
            "M-Money"
        )
        
        intent.putExtra("pdus", arrayOf(smsMessage))
        sendBroadcast(intent)
        
        Log.d(TAG, "Test SMS broadcast sent")
    }
}

@Composable
fun MainScreen(
    onTestSMS: () -> Unit,
    onStartService: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "MoMo SMS Monitor",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Button(
            onClick = onStartService,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start SMS Monitoring")
        }
        
        Button(
            onClick = onTestSMS,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Test SMS")
        }
    }
}