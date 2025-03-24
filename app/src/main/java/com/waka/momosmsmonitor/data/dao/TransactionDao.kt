package com.waka.momosmsmonitor.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.waka.momosmsmonitor.data.models.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Incoming Money Transactions
 */
@Dao
interface IncomingMoneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: IncomingMoney): Long

    @Update
    suspend fun update(transaction: IncomingMoney)

    @Delete
    suspend fun delete(transaction: IncomingMoney)

    @Query("SELECT * FROM incoming_money")
    fun getAllTransactions(): Flow<List<IncomingMoney>>

    @Query("SELECT * FROM incoming_money WHERE id = :id")
    suspend fun getTransactionById(id: Long): IncomingMoney?

    @Query("SELECT * FROM incoming_money WHERE transactionId = :transactionId")
    suspend fun getTransactionByTransactionId(transactionId: String): IncomingMoney?
}

/**
 * DAO for Payment to Code Holder Transactions
 */
@Dao
interface PaymentToCodeHolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: PaymentToCodeHolder): Long

    @Update
    suspend fun update(transaction: PaymentToCodeHolder)

    @Delete
    suspend fun delete(transaction: PaymentToCodeHolder)

    @Query("SELECT * FROM payment_to_code_holder")
    fun getAllTransactions(): Flow<List<PaymentToCodeHolder>>

    @Query("SELECT * FROM payment_to_code_holder WHERE id = :id")
    suspend fun getTransactionById(id: Long): PaymentToCodeHolder?

    @Query("SELECT * FROM payment_to_code_holder WHERE transactionId = :transactionId")
    suspend fun getTransactionByTransactionId(transactionId: String): PaymentToCodeHolder?
}

/**
 * DAO for Transfer to Mobile Transactions
 */
@Dao
interface TransferToMobileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransferToMobile): Long

    @Update
    suspend fun update(transaction: TransferToMobile)

    @Delete
    suspend fun delete(transaction: TransferToMobile)

    @Query("SELECT * FROM transfer_to_mobile")
    fun getAllTransactions(): Flow<List<TransferToMobile>>

    @Query("SELECT * FROM transfer_to_mobile WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransferToMobile?
}

/**
 * DAO for Bank Deposit Transactions
 */
@Dao
interface BankDepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: BankDeposit): Long

    @Update
    suspend fun update(transaction: BankDeposit)

    @Delete
    suspend fun delete(transaction: BankDeposit)

    @Query("SELECT * FROM bank_deposits")
    fun getAllTransactions(): Flow<List<BankDeposit>>

    @Query("SELECT * FROM bank_deposits WHERE id = :id")
    suspend fun getTransactionById(id: Long): BankDeposit?
}

/**
 * DAO for Airtime Bill Payment Transactions
 */
@Dao
interface AirtimeBillPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: AirtimeBillPayment): Long

    @Update
    suspend fun update(transaction: AirtimeBillPayment)

    @Delete
    suspend fun delete(transaction: AirtimeBillPayment)

    @Query("SELECT * FROM airtime_bill_payments")
    fun getAllTransactions(): Flow<List<AirtimeBillPayment>>

    @Query("SELECT * FROM airtime_bill_payments WHERE id = :id")
    suspend fun getTransactionById(id: Long): AirtimeBillPayment?

    @Query("SELECT * FROM airtime_bill_payments WHERE transactionId = :transactionId")
    suspend fun getTransactionByTransactionId(transactionId: String): AirtimeBillPayment?
}

/**
 * DAO for Cash Power Bill Payment Transactions
 */
@Dao
interface CashPowerBillPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: CashPowerBillPayment): Long

    @Update
    suspend fun update(transaction: CashPowerBillPayment)

    @Delete
    suspend fun delete(transaction: CashPowerBillPayment)

    @Query("SELECT * FROM cash_power_bill_payments")
    fun getAllTransactions(): Flow<List<CashPowerBillPayment>>

    @Query("SELECT * FROM cash_power_bill_payments WHERE id = :id")
    suspend fun getTransactionById(id: Long): CashPowerBillPayment?

    @Query("SELECT * FROM cash_power_bill_payments WHERE transactionId = :transactionId")
    suspend fun getTransactionByTransactionId(transactionId: String): CashPowerBillPayment?
}

/**
 * DAO for Third Party Transactions
 */
@Dao
interface ThirdPartyTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: ThirdPartyTransaction): Long

    @Update
    suspend fun update(transaction: ThirdPartyTransaction)

    @Delete
    suspend fun delete(transaction: ThirdPartyTransaction)

    @Query("SELECT * FROM third_party_transactions")
    fun getAllTransactions(): Flow<List<ThirdPartyTransaction>>

    @Query("SELECT * FROM third_party_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): ThirdPartyTransaction?

    @Query("SELECT * FROM third_party_transactions WHERE transactionId = :transactionId")
    suspend fun getTransactionByTransactionId(transactionId: String): ThirdPartyTransaction?
}

/**
 * DAO for Withdrawal from Agent Transactions
 */
@Dao
interface WithdrawalFromAgentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: WithdrawalFromAgent): Long

    @Update
    suspend fun update(transaction: WithdrawalFromAgent)

    @Delete
    suspend fun delete(transaction: WithdrawalFromAgent)

    @Query("SELECT * FROM withdrawals_from_agents")
    fun getAllTransactions(): Flow<List<WithdrawalFromAgent>>

    @Query("SELECT * FROM withdrawals_from_agents WHERE id = :id")
    suspend fun getTransactionById(id: Long): WithdrawalFromAgent?
}

/**
 * DAO for Bank Transfer Transactions
 */
@Dao
interface BankTransferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: BankTransfer): Long

    @Update
    suspend fun update(transaction: BankTransfer)

    @Delete
    suspend fun delete(transaction: BankTransfer)

    @Query("SELECT * FROM bank_transfers")
    fun getAllTransactions(): Flow<List<BankTransfer>>

    @Query("SELECT * FROM bank_transfers WHERE id = :id")
    suspend fun getTransactionById(id: Long): BankTransfer?
}

/**
 * DAO for Internet Bundle Purchase Transactions
 */
@Dao
interface InternetBundlePurchaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: InternetBundlePurchase): Long

    @Update
    suspend fun update(transaction: InternetBundlePurchase)

    @Delete
    suspend fun delete(transaction: InternetBundlePurchase)

    @Query("SELECT * FROM internet_bundle_purchases")
    fun getAllTransactions(): Flow<List<InternetBundlePurchase>>

    @Query("SELECT * FROM internet_bundle_purchases WHERE id = :id")
    suspend fun getTransactionById(id: Long): InternetBundlePurchase?
}

/**
 * DAO for Voice Bundle Purchase Transactions
 */
@Dao
interface VoiceBundlePurchaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: VoiceBundlePurchase): Long

    @Update
    suspend fun update(transaction: VoiceBundlePurchase)

    @Delete
    suspend fun delete(transaction: VoiceBundlePurchase)

    @Query("SELECT * FROM voice_bundle_purchases")
    fun getAllTransactions(): Flow<List<VoiceBundlePurchase>>

    @Query("SELECT * FROM voice_bundle_purchases WHERE id = :id")
    suspend fun getTransactionById(id: Long): VoiceBundlePurchase?
}

/**
 * DAO for Sync Status
 */
@Dao
interface SyncStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(syncStatus: SyncStatus): Long

    @Update
    suspend fun update(syncStatus: SyncStatus)

    @Delete
    suspend fun delete(syncStatus: SyncStatus)

    @Query("SELECT * FROM sync_status WHERE synced = 0")
    fun getUnsyncedTransactions(): Flow<List<SyncStatus>>

    @Query("SELECT * FROM sync_status WHERE transactionId = :transactionId")
    suspend fun getSyncStatusByTransactionId(transactionId: String): SyncStatus?

    @Query("UPDATE sync_status SET synced = 1, syncTimestamp = :timestamp WHERE transactionId = :transactionId")
    suspend fun markAsSynced(transactionId: String, timestamp: String)
} 