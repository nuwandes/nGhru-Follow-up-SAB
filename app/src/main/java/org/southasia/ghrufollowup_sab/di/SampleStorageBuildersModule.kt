package org.southasia.ghrufollowup_sab.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.southasia.ghrufollowup_sab.ui.codeheck.CodeCheckDialogFragment
import org.southasia.ghrufollowup_sab.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.StorageFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.manualentry.ManualEntryBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.manualentry.ManualEntryFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.reason.ReasonDialogFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.samplelist.PendingSampleListFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.scanqrcode.ScanBarcodeFragment
import org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.transfer.TransferFragment

@Suppress("unused")
@Module
abstract class SampleStorageBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.scanbarcode.ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeStorageFragment(): StorageFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment


    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributePendingSampleListFragment(): PendingSampleListFragment

    @ContributesAndroidInjector
    abstract fun contributeTransferFragment(): TransferFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragmentXX(): org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.reasonc.ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.southasia.ghrufollowup_sab.ui.samplemanagement.storage.completed.CompletedDialogFragment

}

