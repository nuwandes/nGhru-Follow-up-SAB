package org.nghru_hpv.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_hpv.ghru.ui.setting.SettingFragment

@Suppress("unused")
@Module
abstract class SettingFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment


}
