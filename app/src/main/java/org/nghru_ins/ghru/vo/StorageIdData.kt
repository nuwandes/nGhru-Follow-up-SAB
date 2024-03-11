package org.nghru_ins.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "storage_ids")
data class StorageIdData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Expose @SerializedName("id") @ColumnInfo(name = "key") val key: String?,
    @Expose @SerializedName("storage_id") @ColumnInfo(name = "storage_id") val storage_id: String?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(key)
        parcel.writeString(storage_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StorageIdData> {
        override fun createFromParcel(parcel: Parcel): StorageIdData {
            return StorageIdData(parcel)
        }

        override fun newArray(size: Int): Array<StorageIdData?> {
            return arrayOfNulls(size)
        }
    }

}
