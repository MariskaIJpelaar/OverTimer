package org.mariska.overtimer.weekday

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalTime
import java.time.temporal.ChronoUnit.HOURS

class WeekDayItem(day: String) : Parcelable, Serializable {
    var active: Boolean = false
    var weekday: String = day
    var startTime: LocalTime = LocalTime.of(9, 0)
    var endTime: LocalTime = LocalTime.of(17, 0)
    var hoursWorked = 0


    fun totalHours() : Int {
        if (!active)
            return 0
        return startTime.until(endTime, HOURS).toInt()
    }

    constructor(parcel: Parcel) : this("") {
        active = parcel.readByte() != 0.toByte()
        weekday = parcel.readString().orEmpty()
        startTime = LocalTime.parse(parcel.readString().orEmpty())
        endTime = LocalTime.parse(parcel.readString().orEmpty())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(weekday)
        parcel.writeString(startTime.toString())
        parcel.writeString(endTime.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        final val CREATOR: Parcelable.Creator<WeekDayItem> =  object: Parcelable.Creator<WeekDayItem> {
            override fun createFromParcel(parcel: Parcel): WeekDayItem {
                return WeekDayItem(parcel)
            }

            override fun newArray(size: Int): Array<WeekDayItem?> {
                return arrayOfNulls(size)
            }
        }

        private const val serialVersionUID: Long = 2355114125920513
    }
}