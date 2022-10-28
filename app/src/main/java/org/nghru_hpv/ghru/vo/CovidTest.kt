package org.nghru_hpv.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "covid_request")
data class CovidRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: CovidData?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CovidData::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<CovidRequest> {
        override fun createFromParcel(parcel: Parcel): CovidRequest {
            return CovidRequest(parcel)
        }

        override fun newArray(size: Int): Array<CovidRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class CovidData(
    @Expose @field:SerializedName("status") var status: String?
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CovidData> {
        override fun createFromParcel(parcel: Parcel): CovidData {
            return CovidData(parcel)
        }

        override fun newArray(size: Int): Array<CovidData?> {
            return arrayOfNulls(size)
        }
    }

}