package org.nghru_hpv.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_hpv.ghru.ui.samplemanagement.SampleMangementFragment

@Suppress("unused")
@Module
abstract class SampleHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

}
