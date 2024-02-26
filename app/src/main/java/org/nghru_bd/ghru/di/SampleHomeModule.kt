package org.nghru_bd.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_bd.ghru.ui.samplemanagement.SampleMangementFragment

@Suppress("unused")
@Module
abstract class SampleHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

}
