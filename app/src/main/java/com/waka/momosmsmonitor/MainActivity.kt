package com.waka.momosmsmonitor

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
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
            
            "INCOMING_MONEY" -> """
                You have received 2000 RWF from John DOE (*********013) on your mobile money account at $currentTime. 
                Message from sender: . Your new balance:2000 RWF. Financial Transaction Id: 13889821299.
            """.trimIndent()
            
            "PAYMENT_CODE_HOLDERS" -> """
                TxId: 13889833480. Your payment of 1,000 RWF to Jacques 16911 has been completed at $currentTime. 
                Your new balance: 1,000 RWF. Fee was 0 RWF.
            """.trimIndent()

            "PAYMENT_TO_MOBILE" -> """
                *165*S*10000 RWF transferred to Janette SIRE (250787330355) from 36521840 at $currentTime. 
                Fee was: 100 RWF. New balance: 28300 RWF.
            """.trimIndent()
            
            "BANK_DEPOSIT" -> """
                *113*R*A bank deposit of 40000 RWF has been added to your mobile money account at $currentTime. 
                Your NEW BALANCE :40400 RWF. Cash Deposit::CASH::::0::250795965046.
                Thank you for using MTN MobileMoney.*EN#
            """.trimIndent()
            
            "BILL_AIRTIME" -> """
                *162*TxId:13913173324*S*Your payment of 2000 RWF to Airtime with token has been completed at $currentTime. 
                Fee was 0 RWF. Your new balance: 25280 RWF . Message: - -. *EN#
            """.trimIndent()

            "BILL_POWER" -> """
                *162*TxId:14103506345*S*Your payment of 4000 RWF to MTN Cash Power with token 72962-79980-44699-06073 has been completed at 2024-05-26 13:31:00. 
                Fee was 0 RWF. Your new balance: 800 RWF
            """.trimIndent()

            "THIRD_PARTY" -> """
                *164*S*Y'ello,A transaction of 600 RWF by INFORMATION TECHNOLOGY  ENGINEERING CONSTRUCTION   ITEC Ltd on your MOMO account was successfully completed at 2024-06-06 16:19:01. 
                Message from debit receiver: ITEC Pay. Your new balance:230 RWF. Fee was 0 RWF. 
                Financial Transaction Id: 14262446969. External Transaction Id: d5e8bfeb-33d8-4eb2-8d22-154e5ff5e440.
            """.trimIndent()

            "WITHDRAWAL_FROM_AGENT" -> """
                You Wakuma Tekalign DEBELA (*********036) have via agent: Susan Payne ICYIMANIZANYE (250791945850), withdrawn 50000 RWF from your mobile money account: 36521838 at 2024-11-23 13:23:44 
                and you can now collect your money in cash. Your new balance: 2401 RWF. Fee paid: 1100 RWF. Message from agent: 1. Financial Transaction Id: 17006777807.
            """.trimIndent()

            "BANK_TRANSFER" -> """
                You have transferred 50000 RWF to Wakuma Tekalign DEBELA (250795******) from your mobile money account 200******** imbank.bank at 2024-10-23 16:43:03. 
                Your new balance:  . Message from sender: . Message to receiver: . Financial Transaction Id: 16406796969
            """.trimIndent()

            "BUNDLE_INTERNET" -> """
                Yello!Umaze kugura 5,000FRW(7GB) igura 5,000 RWF
            """.trimIndent()

            "BUNDLE_VOICE" -> """
                Yello!Umaze kugura 3000Frw=2500Mins+100SMS igura 3,000 RWF
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
            "INCOMING_MONEY" to "Test Money Received",
            "PAYMENT_CODE_HOLDERS" to "Test Payment Made to MoMo Code Holders",
            "PAYMENT_TO_MOBILE" to "Test Payment Made to Mobile Number",
            "BANK_DEPOSIT" to "Test Bank Deposit",
            "BILL_AIRTIME" to "Test Airtime Bill Payment",
            "BILL_POWER" to "Test Cash Power Bill Payment",
            "THIRD_PARTY" to "Test Transactions initiated by Third Party",
            "WITHDRAWAL_FROM_AGENT" to "Test Withdrawal From Agent",
            "BANK_TRANSFER" to "Test Money Transfer from Own Bank Account",
            "BUNDLE_INTERNET" to "Test Internet Bundle Purchase",
            "BUNDLE_VOICE" to "Test Voice Bundle Purchase"
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