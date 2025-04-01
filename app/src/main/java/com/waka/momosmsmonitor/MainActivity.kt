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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var smsReceiver: SMSReceiver
    
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.POST_NOTIFICATIONS
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
        
        smsReceiver = SMSReceiver()
        checkAndRequestPermissions()
        
        setContent {
            MoMoSMSMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onTestSMS = { type -> simulateTestSMS(type) },
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
        val filter = IntentFilter(SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, filter)
        
        Log.d(TAG, "SMS monitoring started")
    }

    private fun simulateTestSMS(type: String) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        val testMessage = when (type) {
            "REGISTRATION" -> """
                Y'ello, Wakuma Tekalign DEBELA, you have been successfully registered for a mobile money account on $currentTime.
            """.trimIndent()
            
            "MONEY_RECEIVED" -> """
                You have received 2000 RWF from Pelin MUTANGUHA (*********013) on your mobile money account at $currentTime. 
                Message from sender: . Your new balance:2000 RWF. Financial Transaction Id: 13889821288.
            """.trimIndent()
            
            "PAYMENT" -> """
                TxId: 13889833469. Your payment of 1,000 RWF to Jacques 16911 has been completed at $currentTime. 
                Your new balance: 1,000 RWF. Fee was 0 RWF.
            """.trimIndent()
            
            "BANK_DEPOSIT" -> """
                *113*R*A bank deposit of 40000 RWF has been added to your mobile money account at $currentTime. 
                Your NEW BALANCE :40400 RWF. Cash Deposit::CASH::::0::250795963036.
                Thank you for using MTN MobileMoney.*EN#
            """.trimIndent()
            
            "MONEY_TRANSFER" -> """
                *165*S*10000 RWF transferred to Fillette ABAHIRE (250787330254) from 36521838 at $currentTime. 
                Fee was: 100 RWF. New balance: 28300 RWF.
            """.trimIndent()
            
            "AIRTIME" -> """
                *162*TxId:13913173274*S*Your payment of 2000 RWF to Airtime with token has been completed at $currentTime. 
                Fee was 0 RWF. Your new balance: 25280 RWF . Message: - -. *EN#
            """.trimIndent()
            
            else -> return
        }

        // Use the SMSReceiver's test method directly instead of trying to broadcast
        smsReceiver.processTestMessage(this, "M-Money", testMessage)
        Log.d(TAG, "Test message sent for processing: $type")
    }
}

@Composable
fun MainScreen(
    onTestSMS: (String) -> Unit,
    onStartService: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
        
        Text(
            text = "Test Different Message Types",
            style = MaterialTheme.typography.titleMedium
        )
        
        // Test buttons for different message types
        listOf(
            "REGISTRATION" to "Test Registration SMS",
            "MONEY_RECEIVED" to "Test Money Received",
            "PAYMENT" to "Test Payment Made",
            "BANK_DEPOSIT" to "Test Bank Deposit",
            "MONEY_TRANSFER" to "Test Money Transfer",
            "AIRTIME" to "Test Airtime Purchase"
        ).forEach { (type, label) ->
            Button(
                onClick = { onTestSMS(type) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(label)
            }
        }
    }
}