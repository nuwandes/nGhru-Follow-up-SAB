package org.nghru_ins.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_ins.ghru.ui.covid.confirmation.CovidConfirmationFragment
import org.nghru_ins.ghru.ui.covid.confirmation.completed.StageOneCompletedDialogFragment
import org.nghru_ins.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_ins.ghru.ui.covid.reason.ReasonDialogFragment

@Suppress("unused")
@Module
abstract class CovidNewBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeConfirmationFragment(): CovidConfirmationFragment

    @ContributesAndroidInjector
    abstract fun contributeStageOneDialogFragment(): StageOneCompletedDialogFragment

}
