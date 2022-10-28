package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.covid.confirmation.CovidConfirmationFragment
import org.southasia.ghrufollowup_sab.ui.covid.confirmation.completed.StageOneCompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.intake.readings.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.covid.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.covidsurvey.LanguageListFragment
import org.southasia.ghrufollowup_sab.ui.covidsurvey.survey.CovidSurveyFragment

@Suppress("unused")
@Module
abstract class CovidSurveyBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeLanguageListFragment(): LanguageListFragment

    @ContributesAndroidInjector
    abstract fun contributeCovidSurveyFragment(): CovidSurveyFragment

}

