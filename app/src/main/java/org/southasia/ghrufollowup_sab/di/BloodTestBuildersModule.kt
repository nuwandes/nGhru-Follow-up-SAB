package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.codeheck.CodeCheckDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.bloodtesthome.BloodTestHomeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.fastingbloodglucose.FastingBloodGlucoseFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.bloodtesthome.cancel.CancelDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.fastingbloodglucose.completed.CompletedDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.hb1ac.Hb1AcFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.hdl.HDLFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.hemoglobin.HemoglobinFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.hogtt.HOGTTFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.home.SampleMangementHomeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.lipidprofile.LipidProfileFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.pendingsamplelist.PendingSampleListFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.StorageFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.manualentry.ManualEntryBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.manualentry.ManualEntryFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.scanqrcode.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.totalcholesterol.TotalCholesterolFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.triglycerides.TriglyceridesFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.tubescanbarcode.TubeScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.tubescanbarcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.tubescanbarcode.manualentry.TubeScanManualBarcodeFragment

@Suppress("unused")
@Module
abstract class BloodTestBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributePendingSampleListFragment(): PendingSampleListFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanBarcodeFragment(): TubeScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementHomeFragment(): SampleMangementHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeLipidProfileFragment(): LipidProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeFastingBloodGlucoseFragment(): FastingBloodGlucoseFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeHb1AcFragment(): Hb1AcFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragmentXX(): org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanManualBarcodeFragment(): TubeScanManualBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.scanbarcode.ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeStorageFragment(): StorageFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeHOGTTFragment(): HOGTTFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTotalCholesterolFragment(): TotalCholesterolFragment

    @ContributesAndroidInjector
    abstract fun contributeHDLFragment(): HDLFragment

    @ContributesAndroidInjector
    abstract fun contributeHemoglobinFragment(): HemoglobinFragment

    @ContributesAndroidInjector
    abstract fun contributeTriglyceridesFragment(): TriglyceridesFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeBloodTestHomeFragment(): BloodTestHomeFragment


}

