package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.covid.CovidGuideFragment
import org.southasia.ghrufollowup_sab.ui.covid.completed.StartedDialogFragment
import org.southasia.ghrufollowup_sab.ui.covid.confirmation.CovidConfirmationFragment
import org.southasia.ghrufollowup_sab.ui.intake.readings.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.covid.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.covid.stageonereason.ReasonDialogFragmentNew

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

