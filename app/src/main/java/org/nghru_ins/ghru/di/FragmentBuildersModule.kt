package org.nghru_ins.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_ins.ghru.ui.login.LoginFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}
