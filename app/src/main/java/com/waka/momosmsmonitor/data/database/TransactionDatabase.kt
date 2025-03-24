package com.waka.momosmsmonitor.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.waka.momosmsmonitor.data.dao.*
import com.waka.momosmsmonitor.data.models.*

@Database(
    entities = [
        IncomingMoney::class,
        PaymentToCodeHolder::class,
        TransferToMobile::class,
        BankDeposit::class,
        AirtimeBillPayment::class,
        CashPowerBillPayment::class,
        ThirdPartyTransaction::class,
        WithdrawalFromAgent::class,
        BankTransfer::class,
        InternetBundlePurchase::class,
        VoiceBundlePurchase::class,
        SyncStatus::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun incomingMoneyDao(): IncomingMoneyDao
    abstract fun paymentToCodeHolderDao(): PaymentToCodeHolderDao
    abstract fun transferToMobileDao(): TransferToMobileDao
    abstract fun bankDepositDao(): BankDepositDao
    abstract fun airtimeBillPaymentDao(): AirtimeBillPaymentDao
    abstract fun cashPowerBillPaymentDao(): CashPowerBillPaymentDao
    abstract fun thirdPartyTransactionDao(): ThirdPartyTransactionDao
    abstract fun withdrawalFromAgentDao(): WithdrawalFromAgentDao
    abstract fun bankTransferDao(): BankTransferDao
    abstract fun internetBundlePurchaseDao(): InternetBundlePurchaseDao
    abstract fun voiceBundlePurchaseDao(): VoiceBundlePurchaseDao
    abstract fun syncStatusDao(): SyncStatusDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): java.util.Date? {
        return value?.let { java.util.Date(it) }
    }

    @androidx.room.TypeConverter
    fun dateToTimestamp(date: java.util.Date?): Long? {
        return date?.time
    }
} 