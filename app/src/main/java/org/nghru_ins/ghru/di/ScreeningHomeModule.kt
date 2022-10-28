package org.nghru_ins.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_ins.ghru.ui.home.HomeFragment

@Suppress("unused")
@Module
abstract class ScreeningHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

}
