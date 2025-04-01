package com.waka.momosmsmonitor.utils

import android.util.Log
import com.waka.momosmsmonitor.data.database.TransactionDatabase
import com.waka.momosmsmonitor.data.models.*
import java.text.SimpleDateFormat
import java.util.*

class SMSParser(private val database: TransactionDatabase) {
    private val TAG = "SMSParser"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    suspend fun parseAndSaveTransaction(sender: String, body: String) {
        when {
            // Incoming Money
            body.startsWith("You have received") -> {
                parseIncomingMoney(body)?.let { transaction ->
                    database.incomingMoneyDao().insert(transaction)
                    Log.d(TAG, "Saved incoming money transaction: ${transaction.amount}")
                }
            }

            // Payment to Code Holder
            body.startsWith("TxId") && body.contains("payment of") -> {
                parsePaymentToCodeHolder(body)?.let { transaction ->
                    database.paymentToCodeHolderDao().insert(transaction)
                    Log.d(TAG, "Saved payment to code holder: ${transaction.amount}")
                }
            }

            // Transfer to Mobile Number
            body.startsWith("*165*S*") -> {
                parseTransferToMobile(body)?.let { transaction ->
                    database.transferToMobileDao().insert(transaction)
                    Log.d(TAG, "Saved mobile transfer: ${transaction.amount}")
                }
            }

            // Bank Deposit
            body.startsWith("*113*R*") -> {
                parseBankDeposit(body)?.let { transaction ->
                    database.bankDepositDao().insert(transaction)
                    Log.d(TAG, "Saved bank deposit: ${transaction.amount}")
                }
            }

            // Airtime or Cash Power Purchase
            body.startsWith("*162*") -> {
                if (body.contains("Airtime")) {
                    parseAirtimePurchase(body)?.let { transaction ->
                        database.airtimeBillPaymentDao().insert(transaction)
                        Log.d(TAG, "Saved airtime purchase: ${transaction.amount}")
                    }
                } else if (body.contains("Cash Power")) {
                    parseCashPowerPurchase(body)?.let { transaction ->
                        database.cashPowerBillPaymentDao().insert(transaction)
                        Log.d(TAG, "Saved cash power purchase: ${transaction.amount}")
                    }
                }
            }

            // Third Party Transaction
            body.startsWith("*164*S*") -> {
                parseThirdPartyTransaction(body)?.let { transaction ->
                    database.thirdPartyTransactionDao().insert(transaction)
                    Log.d(TAG, "Saved third party transaction: ${transaction.amount}")
                }
            }

            // Withdrawal from Agent
            body.startsWith("You") && body.contains("withdrawn") -> {
                parseWithdrawal(body)?.let { transaction ->
                    database.withdrawalFromAgentDao().insert(transaction)
                    Log.d(TAG, "Saved withdrawal: ${transaction.amount}")
                }
            }

            // Bank Transfer
            body.startsWith("You have transferred") -> {
                parseBankTransfer(body)?.let { transaction ->
                    database.bankTransferDao().insert(transaction)
                    Log.d(TAG, "Saved bank transfer: ${transaction.amount}")
                }
            }

            // Bundle Purchases
            body.startsWith("Yello!Umaze kugura") -> {
                if (body.contains("GB") || body.contains("MB")) {
                    parseInternetBundle(body)?.let { transaction ->
                        database.internetBundlePurchaseDao().insert(transaction)
                        Log.d(TAG, "Saved internet bundle purchase: ${transaction.amount}")
                    }
                } else if (body.contains("Mins")) {
                    parseVoiceBundle(body)?.let { transaction ->
                        database.voiceBundlePurchaseDao().insert(transaction)
                        Log.d(TAG, "Saved voice bundle purchase: ${transaction.amount}")
                    }
                }
            }

            else -> {
                Log.d(TAG, "Unrecognized message format: ${body.take(50)}...")
            }
        }
    }

    private fun parseIncomingMoney(body: String): IncomingMoney? {

        val amount_sender_match = """received (\d+) RWF from (.+?) \(""".toRegex()
        val dateTime_match = """at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        val txnId_match = """Financial Transaction Id: (\d+)""".toRegex()

        return amount_sender_match.find(body)?.let { amount_sender_match ->
            val amount = amount_sender_match.groupValues[1].toDouble()
            val sender = amount_sender_match.groupValues[2]

            dateTime_match.find(body)?.let { dateTime_match ->
                val dateTime = dateTime_match.groupValues[1]

                txnId_match.find(body)?.let { txnId_match ->
                    val txnId = txnId_match.groupValues[1]

                    IncomingMoney(
                        amount = amount,
                        sender = sender,
                        dateTime = dateTime,
                        transactionId = txnId
                    )
                }
            }
        }
    }

    private fun parsePaymentToCodeHolder(body: String): PaymentToCodeHolder? {
        val pattern = """TxId: (\d+).*?payment of ([\d,]+) RWF to (.+?) (\d+).*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        return pattern.find(body)?.let { match ->
            PaymentToCodeHolder(
                transactionId = match.groupValues[1],
                amount = match.groupValues[2].replace(",", "").toDouble(),
                recipient = match.groupValues[3],
                recipientCode = match.groupValues[4],
                dateTime = match.groupValues[5]
            )
        }
    }

    private fun parseTransferToMobile(body: String): TransferToMobile? {
        //val pattern = """(\d+) RWF transferred to (.+?) \((\d+)\).*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}).*?Fee was: (\d+) RWF""".toRegex()
        val amount_recipient_match = """(\d+) RWF transferred to (.+?) \((250\d+)\)""".toRegex()
        val dateTime_match = """at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        val fee_match = """Fee was: (\d+) RWF""".toRegex()

        return amount_recipient_match.find(body)?.let { amount_recipient_match ->
            val amount = amount_recipient_match.groupValues[1].toDouble()
            val recipient = amount_recipient_match.groupValues[2]
            val recipientNumber = amount_recipient_match.groupValues[3]

            dateTime_match.find(body)?.let { dateTime_match ->
                val dateTime = dateTime_match.groupValues[1]

                fee_match.find(body)?.let { fee_match ->
                    val fee = fee_match.groupValues[1].toDouble()

                    TransferToMobile(
                        amount = amount,
                        recipient = recipient,
                        recipientNumber = recipientNumber,
                        dateTime = dateTime,
                        fee = fee
                    )
                }
            }
        }
    }
    private fun parseBankDeposit(body: String): BankDeposit? {
        val pattern = """deposit of (\d+) RWF.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        return pattern.find(body)?.let { match ->
            BankDeposit(
                amount = match.groupValues[1].toDouble(),
                dateTime = match.groupValues[2]
            )
        }
    }

    private fun parseAirtimePurchase(body: String): AirtimeBillPayment? {
        //val pattern = """TxId:(\d+)\*S\*Your payment of (\d+) RWF.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}).*?Fee was (\d+) RWF""".toRegex()
        val main_match = """TxId:(\d+)\*S\*Your payment of (\d+) RWF to (Airtime) with token.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        val fee_match = """Fee was (\d+) RWF""".toRegex()

        return main_match.find(body)?.let { match ->
            AirtimeBillPayment(
                transactionId = match.groupValues[1],
                amount = match.groupValues[2].toDouble(),
                dateTime = match.groupValues[4],
                fee = fee_match.find(body)?.groupValues?.get(1)?.toDouble() ?: 0.0
            )
        }
    }

    private fun parseCashPowerPurchase(body: String): CashPowerBillPayment?{
        //val pattern = """TxId:(\d+)\*S\*Your payment of (\d+) RWF.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}).*?Fee was (\d+) RWF""".toRegex()
        val main_match = """TxId:(\d+)\*S\*Your payment of (\d+) RWF to (MTN Cash Power) with token.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        val fee_match = """Fee was (\d+) RWF""".toRegex()

        return main_match.find(body)?.let { match ->
            CashPowerBillPayment(
                transactionId = match.groupValues[1],
                amount = match.groupValues[2].toDouble(),
                dateTime = match.groupValues[4],
                fee = fee_match.find(body)?.groupValues?.get(1)?.toDouble() ?: 0.0
            )
        }
    }

    private fun parseThirdPartyTransaction(body: String): ThirdPartyTransaction? {
        val main_match = """A transaction of (\d+) RWF by (.*?) on your MOMO account was successfully completed at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        val txnId_match = """Financial Transaction Id: (\d+)""".toRegex()

        return main_match.find(body)?.let { match ->
            ThirdPartyTransaction(
                amount = match.groupValues[1].toDouble(),
                initiatedBy = match.groupValues[2],
                dateTime = match.groupValues[3],
                transactionId = txnId_match.find(body)?.groupValues?.get(1) ?: ""
            )
        }
    }

    private fun parseWithdrawal(body: String): WithdrawalFromAgent? {
        val pattern = """You (.*?)\(\*+\d{3}\) have via agent: (.*?) \((\d+)\), withdrawn (\d+) RWF.*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        return pattern.find(body)?.let { match ->
            WithdrawalFromAgent(
                userName = match.groupValues[1].trim(),
                agentName = match.groupValues[2].trim(),
                agentNumber = match.groupValues[3],
                amount = match.groupValues[4].toDouble(),
                dateTime = match.groupValues[5]
            )
        }
    }

    private fun parseBankTransfer(body: String): BankTransfer? {
        val pattern = """transferred (\d+) RWF to (.*?) from your .*?at (\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})""".toRegex()
        return pattern.find(body)?.let { match ->
            BankTransfer(
                amount = match.groupValues[1].toDouble(),
                recipient = match.groupValues[2].trim(),
                dateTime = match.groupValues[3]
            )
        }
    }

    private fun parseInternetBundle(body: String): InternetBundlePurchase? {
        val pattern = """kugura ([\d,]+)(?:Rwf|FRW)\((\d+)(GB|MB)\)""".toRegex()
        return pattern.find(body)?.let { match ->
            InternetBundlePurchase(
                amount = match.groupValues[1].replace(",", "").toDouble(),
                bundleSize = match.groupValues[2],
                unit = match.groupValues[3],
                dateTime = dateFormat.format(Date())
            )
        }
    }

    private fun parseVoiceBundle(body: String): VoiceBundlePurchase? {
        val pattern = """kugura ([\d,]+)Frw=(\d+)Mins\+(\d+)SMS""".toRegex()
        return pattern.find(body)?.let { match ->
            VoiceBundlePurchase(
                amount = match.groupValues[1].replace(",", "").toDouble(),
                minutes = match.groupValues[2],
                smses = match.groupValues[3],
                dateTime = dateFormat.format(Date())
            )
        }
    }
} 