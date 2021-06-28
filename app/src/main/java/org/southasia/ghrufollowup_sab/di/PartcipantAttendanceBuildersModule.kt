package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.participantlist.attendance.ParticipantAttendanceFragment
import org.southasia.ghrufollowup_sab.ui.participantlist.attendance.updateparticipant.UpdateParticipantFragment

@Suppress("unused")
@Module
abstract class PartcipantAttendanceBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeParticipantAttendanceFragment(): ParticipantAttendanceFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateParticipantFragment(): UpdateParticipantFragment

//    @ContributesAndroidInjector
//    abstract fun contributeMeasurementListFragment(): MeasurementListFragment
//
//    @ContributesAndroidInjector
//    abstract fun contributeVisitCompletedDialogFragment(): VisitCompletedDialogFragment
//
//    @ContributesAndroidInjector
//    abstract fun contributeVisitWarningDialogFragment(): VisitWarningDialogFragment


}