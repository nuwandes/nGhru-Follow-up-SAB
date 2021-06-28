package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.codeheck.CodeCheckDialogFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.concent.ConcentFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.concent.reasondialog.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.createhousehold.CreateHouseholdFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.householdmembers.HouseholdMembersFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.householdmembers.asigndialog.AsignDialogFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.manualentry.ManualEntryFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.member.AddHouseHoldMemberFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.registergeolocation.RegisterGeolocationFragment
import org.southasia.ghrufollowup_sab.ui.enumeration.scanCode.ScanQRCodeFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.visitedhouseholds.VisitedHouseholdFragment

@Suppress("unused")
@Module
abstract class EnumerationFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeConcentFragment(): ConcentFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterGeolocationFragment(): RegisterGeolocationFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanQRCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeVisitedHouseholdFragment(): VisitedHouseholdFragment

    @ContributesAndroidInjector
    abstract fun contributeAddHouseHoldMemberFragment(): AddHouseHoldMemberFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeHouseholdMembersFragment(): HouseholdMembersFragment

    @ContributesAndroidInjector
    abstract fun contributeAsignDialogFragment(): AsignDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateHouseholdFragment(): CreateHouseholdFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.enumeration.completed.CompletedDialogFragment

}
