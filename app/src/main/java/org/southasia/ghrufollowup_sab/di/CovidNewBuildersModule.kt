package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.covid.confirmation.CovidConfirmationFragment
import org.southasia.ghrufollowup_sab.ui.covid.confirmation.completed.StageOneCompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.intake.readings.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.covid.reason.ReasonDialogFragment

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

