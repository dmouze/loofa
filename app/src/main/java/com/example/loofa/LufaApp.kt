package com.example.loofa

import android.app.Application
import android.content.Context
import com.example.loofa.di.repositoryModule
import com.example.loofa.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LufaApp : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: LufaApp
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Możesz rejestrować logi, aby zobaczyć, co dzieje się w Koin.
            // Przykład na kontrolę błędów - androidLogger(Level.ERROR)
            androidLogger()
            // Przekazuje kontekst Androida.
            androidContext(this@LufaApp)
            // Pobiera właściwości z pliku assets/koin.properties.
            androidFileProperties()
            // Lista modułów do zainicjowania.
            modules(listOf(repositoryModule, viewModelModule))
        }

    }

}