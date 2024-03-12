package org.nghru_pk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_pk.ghru.ui.samplemanagement.SampleMangementFragment

@Suppress("unused")
@Module
abstract class SampleHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

}
