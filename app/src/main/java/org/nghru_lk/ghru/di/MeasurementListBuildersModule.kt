package org.nghru_lk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_lk.ghru.ui.participantlist.measurementlist.MeasurementListFragment
import org.nghru_lk.ghru.ui.participantlist.measurementlist.completevisitcompleted.VisitCompletedDialogFragment
import org.nghru_lk.ghru.ui.participantlist.measurementlist.completevisitwarning.VisitWarningDialogFragment

@Suppress("unused")
@Module
abstract class MeasurementListBuildersModule{

    @ContributesAndroidInjector
    abstract fun contributeMeasurementListFragment(): MeasurementListFragment

    @ContributesAndroidInjector
    abstract fun contributeVisitCompletedDialogFragment(): VisitCompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeVisitWarningDialogFragment(): VisitWarningDialogFragment


}