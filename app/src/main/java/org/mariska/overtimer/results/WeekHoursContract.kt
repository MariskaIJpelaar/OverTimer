package org.mariska.overtimer.results

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.mariska.overtimer.SetHoursActivity
import org.mariska.overtimer.weekday.WeekDayItem

class WeekHoursContract : ActivityResultContract<Array<out WeekDayItem>, Array<out WeekDayItem>?>() {
    val ID: String = "SetHoursActivity"

    override fun createIntent(context: Context, input: Array<out WeekDayItem>): Intent {
        return Intent(context, SetHoursActivity::class.java).apply {
            putExtra(ID, input)
        }
    }

    @SuppressWarnings("unchecked")
    override fun parseResult(resultCode: Int, intent: Intent?): Array<out WeekDayItem>? {
        return intent?.getParcelableArrayExtra(ID) as Array<out WeekDayItem>?
    }
}