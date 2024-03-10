package org.nghru_inn.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_inn.ghru.ui.home.HomeFragment

@Suppress("unused")
@Module
abstract class ScreeningHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

}
