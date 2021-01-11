package com.mpiotrowski.maudiofasttrackmixer.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): PresetsDatabase {

        lateinit var presetsDatabase: PresetsDatabase
        val callback = object: RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                LogUtil.d( "onOpen")
                CoroutineScope(Dispatchers.Default).launch(Dispatchers.IO) {
                    presetsDatabase.populateDatabase()
                }
            }
        }

        presetsDatabase = Room.databaseBuilder(
            context.applicationContext,
            PresetsDatabase::class.java,
            "presets_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

        return presetsDatabase
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRepository(presetsDatabase: PresetsDatabase): Repository {
        return Repository(presetsDatabase.presetsDao())
    }
}