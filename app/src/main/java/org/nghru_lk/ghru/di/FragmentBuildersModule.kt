package org.nghru_lk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_lk.ghru.ui.login.LoginFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}
