package com.example.myapplicationdc.Domain

import android.os.Parcel
import android.os.Parcelable

data class DoctorModel (
    val address: String = "",
    val biography: String = "",
    val id: Int = 0,
    val name: String = "",
    val picture: String = "",
    val special: String = "",
    val experience: Int = 0,
    val location: String = "",
    val mobile: String = "",
    val patients: String = "",
    val rating: Double = 0.0,
    val site: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString()
    ) { }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(biography)
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(picture)
        parcel.writeString(special)
        parcel.writeInt(experience)
        parcel.writeString(location)
        parcel.writeString(mobile)
        parcel.writeString(patients)
        parcel.writeDouble(rating)
        parcel.writeString(site)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DoctorModel> {
        override fun createFromParcel(parcel: Parcel): DoctorModel {
            return DoctorModel(parcel)
        }

        override fun newArray(size: Int): Array<DoctorModel?> {
            return arrayOfNulls(size)
        }
    }
}
