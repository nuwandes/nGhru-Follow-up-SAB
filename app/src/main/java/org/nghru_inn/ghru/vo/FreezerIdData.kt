package org.nghru_inn.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "freezer_ids")
data class FreezerIdData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Expose @SerializedName("id") @ColumnInfo(name = "key") val key: String?,
    @Expose @SerializedName("freezer_id") @ColumnInfo(name = "freezer_id") val storage_id: String?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor(sampleId: String?) : this(
        id = 0,
        key = null,
        storage_id = sampleId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(key)
        parcel.writeString(storage_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FreezerIdData> {
        override fun createFromParcel(parcel: Parcel): FreezerIdData {
            return FreezerIdData(parcel)
        }

        override fun newArray(size: Int): Array<FreezerIdData?> {
            return arrayOfNulls(size)
        }
    }

}
