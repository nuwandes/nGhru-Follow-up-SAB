package org.nghru_inn.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_inn.ghru.ui.samplemanagement.SampleMangementFragment

@Suppress("unused")
@Module
abstract class SampleHomeModule {
    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

}
