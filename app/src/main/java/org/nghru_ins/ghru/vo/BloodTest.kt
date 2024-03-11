package org.nghru_ins.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "blood_test_request")
data class BloodTestRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: BloodTests?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BloodTests::class.java.classLoader),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
    }


    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(body, flags)
        parcel.writeParcelable(meta, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodTestRequest> {
        override fun createFromParcel(parcel: Parcel): BloodTestRequest {
            return BloodTestRequest(parcel)
        }

        override fun newArray(size: Int): Array<BloodTestRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class BloodTests(
    @Embedded(prefix = "tch") @Expose @SerializedName("tch") var tch: BloodTestData?,
    @Embedded(prefix = "fbg") @Expose @SerializedName("fbg") var fbg: BloodTestData?
): Serializable, Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BloodTestData::class.java.classLoader),
        parcel.readParcelable(BloodTestData::class.java.classLoader)

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(tch, flags)
        parcel.writeParcelable(fbg, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodTests> {
        override fun createFromParcel(parcel: Parcel): BloodTests {
            return BloodTests(parcel)
        }

        override fun newArray(size: Int): Array<BloodTests?> {
            return arrayOfNulls(size)
        }
    }

}

data class BloodTestData(
    @Expose @field:SerializedName("value") @ColumnInfo(name = "value")var value: String?,
    @Expose @field:SerializedName("lot_id") @ColumnInfo(name = "lot_id")var lot_id: String?,
    @Expose @field:SerializedName("comment") @ColumnInfo(name = "comment")var comment: String?,
    @Expose @field:SerializedName("device_id") @ColumnInfo(name = "device_id")var device_id: String?
): Serializable, Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(lot_id)
        parcel.writeString(comment)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodTestData> {
        override fun createFromParcel(parcel: Parcel): BloodTestData {
            return BloodTestData(parcel)
        }

        override fun newArray(size: Int): Array<BloodTestData?> {
            return arrayOfNulls(size)
        }
    }
}