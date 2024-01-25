package org.nghru_pk.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_pk.ghru.ui.codeheck.CodeCheckDialogFragment
import org.nghru_pk.ghru.ui.enumeration.concent.ConcentFragment
import org.nghru_pk.ghru.ui.enumeration.concent.reasondialog.ReasonDialogFragment
import org.nghru_pk.ghru.ui.enumeration.createhousehold.CreateHouseholdFragment
import org.nghru_pk.ghru.ui.enumeration.householdmembers.HouseholdMembersFragment
import org.nghru_pk.ghru.ui.enumeration.householdmembers.asigndialog.AsignDialogFragment
import org.nghru_pk.ghru.ui.enumeration.manualentry.ManualEntryFragment
import org.nghru_pk.ghru.ui.enumeration.member.AddHouseHoldMemberFragment
import org.nghru_pk.ghru.ui.enumeration.registergeolocation.RegisterGeolocationFragment
import org.nghru_pk.ghru.ui.enumeration.scanCode.ScanQRCodeFragment
import org.nghru_pk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_pk.ghru.ui.visitedhouseholds.VisitedHouseholdFragment

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
    abstract fun contrubuteCompletedDialogFragment(): org.nghru_pk.ghru.ui.enumeration.completed.CompletedDialogFragment

}
