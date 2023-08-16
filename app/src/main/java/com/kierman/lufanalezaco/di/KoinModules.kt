package com.kierman.lufanalezaco.di

import com.kierman.lufanalezaco.viewmodel.Repository
import com.kierman.lufanalezaco.viewmodel.LufaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LufaViewModel(get()) }
}

val repositoryModule = module{
    single{
        Repository()
    }
}
