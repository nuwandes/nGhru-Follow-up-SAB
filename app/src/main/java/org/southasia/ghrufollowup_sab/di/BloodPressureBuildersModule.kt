package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.BPFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.info.InfoFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.main.BPMainFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.manual.one.BPManualOneFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.manual.three.BPManualThreeFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.manual.two.BPManualTwoFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.skip.SkipDialogFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.manualentrybp.ManualEntryBodyMeasurement
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.measurements.MeasurementsFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.measurements.second.MeasurementsSecondFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.review.ReviewFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.review.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.scanbarcodebp.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.bodymeasurements.verifyid.VerifyIDFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class BloodPressureBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeBPFragment(): BPFragment

    @ContributesAndroidInjector
    abstract fun contributeMeasurementsFragment(): MeasurementsFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewFragment(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeVerifyIDFragment(): VerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeMeasurementsSecondFragment(): MeasurementsSecondFragment

    @ContributesAndroidInjector
    abstract fun contributeBPMainFragment(): BPMainFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualOneFragment(): BPManualOneFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualTwoFragment(): BPManualTwoFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualThreeFragment(): BPManualThreeFragment

    @ContributesAndroidInjector
    abstract fun contributeSkipDialogFragment(): SkipDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteManualEntryBodyMeasurement(): ManualEntryBodyMeasurement

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteReasonDialogFragment(): org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.reason.ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.bodymeasurements.bp.completed.CompletedDialogFragment

}