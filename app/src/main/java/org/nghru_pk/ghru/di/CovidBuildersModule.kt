package org.nghru_pk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_pk.ghru.ui.covid.CovidGuideFragment
import org.nghru_pk.ghru.ui.covid.completed.StartedDialogFragment
import org.nghru_pk.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_pk.ghru.ui.covid.stageonereason.ReasonDialogFragmentNew

@Suppress("unused")
@Module
abstract class CovidBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeStartedDialogFragment(): StartedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeStageOneFragment(): ReasonDialogFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeGuideFragment(): CovidGuideFragment


}

