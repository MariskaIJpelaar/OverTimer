package org.mariska.overtimer.weekday

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalTime
import java.time.temporal.ChronoUnit.HOURS

class WeekDayItem(day: String) : Parcelable, Serializable {
    var active: Boolean = false
    var weekday: String = day
    var start_time: LocalTime = LocalTime.of(9, 0)
    var end_time: LocalTime = LocalTime.of(17, 0)
    var hours_worked = 0


    fun total_hours() : Int {
        if (!active)
            return 0
        return start_time.until(end_time, HOURS).toInt()
    }

    constructor(parcel: Parcel) : this("") {
        active = parcel.readByte() != 0.toByte()
        weekday = parcel.readString().orEmpty()
        start_time = LocalTime.parse(parcel.readString().orEmpty())
        end_time = LocalTime.parse(parcel.readString().orEmpty())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(weekday)
        parcel.writeString(start_time.toString())
        parcel.writeString(end_time.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeekDayItem> {
        override fun createFromParcel(parcel: Parcel): WeekDayItem {
            return WeekDayItem(parcel)
        }

        override fun newArray(size: Int): Array<WeekDayItem?> {
            return arrayOfNulls(size)
        }
    }
}