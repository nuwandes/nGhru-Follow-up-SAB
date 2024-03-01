package org.nghru_pk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_pk.ghru.ui.setting.SettingFragment

@Suppress("unused")
@Module
abstract class SettingFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment


}
