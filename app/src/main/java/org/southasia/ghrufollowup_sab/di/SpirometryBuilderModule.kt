package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.ecg.guide.ElectrodeFragment
import org.southasia.ghrufollowup_sab.ui.ecg.guide.PreperationFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.TraceFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.complete.CompleteDialogFragment
import org.southasia.ghrufollowup_sab.ui.ecg.trace.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.cancel.CancelDialogFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.checklist.CheckListFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.guide.GuideMainFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.manualentry.ManualEntrySpirometryFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.record.RecordTestFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.scanbarcode.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.tests.TestFragment
import org.southasia.ghrufollowup_sab.ui.spirometry.verifyid.SpirometryVerifyIDFragment
import org.southasia.ghrufollowup_sab.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class SpirometryBuilderModule {

    @ContributesAndroidInjector
    abstract fun SpirometryVerifyIDFragment(): SpirometryVerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeSpirometryGuideMainFragment(): GuideMainFragment

    @ContributesAndroidInjector
    abstract fun contributeTestFragment(): TestFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeRecordTestFragment(): RecordTestFragment

    @ContributesAndroidInjector
    abstract fun contributeElectrodeFragment(): ElectrodeFragment

    @ContributesAndroidInjector
    abstract fun contributePreperationFragment(): PreperationFragment

    @ContributesAndroidInjector
    abstract fun contributeTraceFragment(): TraceFragment

    @ContributesAndroidInjector
    abstract fun contributeCompleteDialogFragment(): CompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragmentt(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryECGFragment(): ManualEntrySpirometryFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.spirometry.tests.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCheckListFragment(): CheckListFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): org.southasia.ghrufollowup_sab.ui.spirometry.cancelchecklist.CancelDialogFragment


}