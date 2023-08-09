package com.kierman.lufanalezaco.di

import com.kierman.lufanalezaco.Repository
import com.kierman.lufanalezaco.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

val repositoryModule = module{
    single{
        Repository()
    }
}
