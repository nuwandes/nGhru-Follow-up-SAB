package org.nghru_ins.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.nghru_ins.ghru.vo.FastingBloodGlucoseDto
import org.nghru_ins.ghru.vo.Meta
import java.io.Serializable

class FastingBloodGlucoseWithMeta (
    @Expose @SerializedName("meta") val meta: Meta?,
    @Expose @SerializedName("data") val fastingBloodGlucose: FastingBloodGlucoseDto?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(FastingBloodGlucoseDto::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(fastingBloodGlucose, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FastingBloodGlucoseWithMeta> {
        override fun createFromParcel(parcel: Parcel): FastingBloodGlucoseWithMeta {
            return FastingBloodGlucoseWithMeta(parcel)
        }

        override fun newArray(size: Int): Array<FastingBloodGlucoseWithMeta?> {
            return arrayOfNulls(size)
        }
    }

}