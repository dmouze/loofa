package com.example.loofa.di

import com.example.loofa.Repository
import com.example.loofa.viewmodel.LoofaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoofaViewModel(get()) }
}

val repositoryModule = module{
    single{
        Repository()
    }
}