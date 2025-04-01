package com.waka.momosmsmonitor.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.waka.momosmsmonitor.data.database.TransactionDatabase
import com.waka.momosmsmonitor.utils.SMSParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    private val TAG = "SMSReceiver"
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages?.let { smsMessages ->
            for (message in smsMessages) {
                val sender = message.originatingAddress ?: continue
                val body = message.messageBody ?: continue
                
                // Only process messages from MTN MoMo (you might want to adjust this filter)
                if (!isMoMoMessage(sender)) {
                    Log.d(TAG, "Skipping non-MoMo message from $sender")
                    continue
                }

                Log.d(TAG, "Processing MoMo message from $sender: ${body.take(50)}...")
                
                scope.launch {
                    try {
                        processMoMoMessage(context, sender, body)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing message: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun isMoMoMessage(sender: String): Boolean {
        // MoMo messages come from "M-Money" sender
        return sender == "M-Money"
    }

    private suspend fun processMoMoMessage(context: Context, sender: String, body: String) {
        val database = TransactionDatabase.getDatabase(context)
        val parser = SMSParser(database)
        parser.parseAndSaveTransaction(sender, body)
    }
} 