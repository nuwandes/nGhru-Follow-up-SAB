package org.nghru_lk.ghru.di


import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.nghru_lk.ghru.ui.intake.cancel.CancelDialogFragment
import org.nghru_lk.ghru.ui.intake.readings.IntakeReadingsFragment
import org.nghru_lk.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.nghru_lk.ghru.ui.intake.scanbarcode.ScanBarcodeFragment
import org.nghru_lk.ghru.ui.intake.scanbarcode.ManualEntryBarcodeFragment
import org.nghru_lk.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.nghru_lk.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module

abstract class IntakeBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteIntakeReadingsFragment(): IntakeReadingsFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): CompletedDialogFragment
}