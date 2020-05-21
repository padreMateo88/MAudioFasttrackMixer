package com.mpiotrowski.maudiofasttrackmixer.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.mpiotrowski.maudiofasttrackmixer.data.Repository
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): PresetsDatabase {
        Log.d("MPdebug", "provideDataBase")
        return Room.databaseBuilder(
            context.applicationContext,
            PresetsDatabase::class.java,
            "presets_database"
        )
        .fallbackToDestructiveMigration()
        .addCallback(PresetsDatabase.PresetsDatabaseCallback1())
        .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideRepository(presetsDatabase: PresetsDatabase): Repository {
        return Repository(presetsDatabase.presetsDao())
    }
}