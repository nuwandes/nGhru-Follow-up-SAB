package org.nghru_bd.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_bd.ghru.ui.setting.SettingFragment

@Suppress("unused")
@Module
abstract class SettingFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment


}
