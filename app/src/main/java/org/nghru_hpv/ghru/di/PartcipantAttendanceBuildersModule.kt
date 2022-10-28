package org.nghru_hpv.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_hpv.ghru.ui.camera.CameraFragment
import org.nghru_hpv.ghru.ui.participantlist.attendance.ParticipantAttendanceFragment
import org.nghru_hpv.ghru.ui.participantlist.attendance.consent.ConsentFragment
import org.nghru_hpv.ghru.ui.participantlist.attendance.consent.completed.ConsentCompletedDialogFragment
import org.nghru_hpv.ghru.ui.participantlist.attendance.consent.reasondialog.ConsentReasonDialogFragment
import org.nghru_hpv.ghru.ui.participantlist.attendance.updateparticipant.UpdateParticipantFragment

@Suppress("unused")
@Module
abstract class PartcipantAttendanceBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeParticipantAttendanceFragment(): ParticipantAttendanceFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateParticipantFragment(): UpdateParticipantFragment

    @ContributesAndroidInjector
    abstract fun contributeConsentFragment(): ConsentFragment

    @ContributesAndroidInjector
    abstract fun contributeConsentReasonDialogFragment(): ConsentReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraFragment(): CameraFragment

    @ContributesAndroidInjector
    abstract fun contributeConsentCompletedDialogFragment(): ConsentCompletedDialogFragment

}