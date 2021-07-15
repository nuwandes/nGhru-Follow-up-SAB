package org.southasia.ghrufollowup_sab.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "covid_request_new")
data class CovidRequestNew(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: CovidDataNew?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?,
    @Expose @field:SerializedName("status") var status: String?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CovidDataNew::class.java.classLoader),
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readString()
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
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CovidRequestNew> {
        override fun createFromParcel(parcel: Parcel): CovidRequestNew {
            return CovidRequestNew(parcel)
        }

        override fun newArray(size: Int): Array<CovidRequestNew?> {
            return arrayOfNulls(size)
        }
    }

}

data class CovidDataNew(
    @Expose @field:SerializedName("questionnaire_completed") var questionnaire_completed: Boolean = false
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte()

    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (questionnaire_completed) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CovidDataNew> {
        override fun createFromParcel(parcel: Parcel): CovidDataNew {
            return CovidDataNew(parcel)
        }

        override fun newArray(size: Int): Array<CovidDataNew?> {
            return arrayOfNulls(size)
        }
    }

}