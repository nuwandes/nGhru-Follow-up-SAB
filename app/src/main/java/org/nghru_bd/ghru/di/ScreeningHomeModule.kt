package org.nghru_bd.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_bd.ghru.ui.home.HomeFragment

@Suppress("unused")
@Module
abstract class ScreeningHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

}
