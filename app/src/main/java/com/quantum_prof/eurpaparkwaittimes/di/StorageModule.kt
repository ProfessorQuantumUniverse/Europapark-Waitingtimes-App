package com.quantum_prof.eurpaparkwaittimes.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    const val KEY_FAVORITE_CODES = "favorite_codes"
    const val KEY_WAIT_TIME_ALERTS = "wait_time_alerts"
    const val KEY_HAS_SEEN_WELCOME = "has_seen_welcome_dialog"

    private const val PREFS_NAME = "europapark_wait_times_prefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
