package com.waka.momosmsmonitor.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Base Transaction entity representing common fields for all transaction types
 */
abstract class BaseTransaction {
    abstract val id: Long?
    abstract val amount: Double
    abstract val dateTime: String
}

/**
 * Incoming money transaction
 */
@Entity(tableName = "incoming_money")
data class IncomingMoney(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val sender: String,
    override val dateTime: String,
    val transactionId: String
) : BaseTransaction()

/**
 * Payment to code holder
 */
@Entity(tableName = "payment_to_code_holder")
data class PaymentToCodeHolder(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    val transactionId: String,
    override val amount: Double,
    val recipient: String,
    val recipientCode: String? = null,
    override val dateTime: String
) : BaseTransaction()

/**
 * Transfer to mobile number
 */
@Entity(tableName = "transfer_to_mobile")
data class TransferToMobile(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val recipient: String,
    val recipientNumber: String,
    override val dateTime: String,
    val fee: Double
) : BaseTransaction()

/**
 * Bank deposit
 */
@Entity(tableName = "bank_deposits")
data class BankDeposit(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    override val dateTime: String
) : BaseTransaction()

/**
 * Airtime bill payment
 */
@Entity(tableName = "airtime_bill_payments")
data class AirtimeBillPayment(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    val transactionId: String,
    override val amount: Double,
    override val dateTime: String,
    val fee: Double
) : BaseTransaction()

/**
 * Cash power bill payment
 */
@Entity(tableName = "cash_power_bill_payments")
data class CashPowerBillPayment(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    val transactionId: String,
    override val amount: Double,
    override val dateTime: String,
    val fee: Double
) : BaseTransaction()

/**
 * Third party transaction
 */
@Entity(tableName = "third_party_transactions")
data class ThirdPartyTransaction(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val initiatedBy: String,
    override val dateTime: String,
    val transactionId: String
) : BaseTransaction()

/**
 * Withdrawal from agent
 */
@Entity(tableName = "withdrawals_from_agents")
data class WithdrawalFromAgent(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    val userName: String,
    val agentName: String,
    val agentNumber: String,
    override val amount: Double,
    override val dateTime: String
) : BaseTransaction()

/**
 * Bank transfer
 */
@Entity(tableName = "bank_transfers")
data class BankTransfer(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val recipient: String,
    override val dateTime: String
) : BaseTransaction()

/**
 * Internet bundle purchase
 */
@Entity(tableName = "internet_bundle_purchases")
data class InternetBundlePurchase(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val bundleSize: String,
    val unit: String,
    val duration: String? = null,
    override val dateTime: String = Date().toString()
) : BaseTransaction()

/**
 * Voice bundle purchase
 */
@Entity(tableName = "voice_bundle_purchases")
data class VoiceBundlePurchase(
    @PrimaryKey(autoGenerate = true)
    override val id: Long? = null,
    override val amount: Double,
    val minutes: String,
    val smses: String,
    override val dateTime: String = Date().toString()
) : BaseTransaction()

/**
 * Sync status entity to track which records have been synced to the server
 */
@Entity(tableName = "sync_status")
data class SyncStatus(
    @PrimaryKey
    val transactionId: String,
    val tableName: String,
    val recordId: Long,
    val synced: Boolean = false,
    val syncTimestamp: String? = null
) 