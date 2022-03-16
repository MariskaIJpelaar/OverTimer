package org.mariska.overtimer.results

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import org.mariska.overtimer.RegisterHoursFragment
import org.mariska.overtimer.weekday.WeekDayItem

class WeekHoursItemContract : ActivityResultContract<Void, WeekDayItem?>() {
    companion object {
        const val ID: String = "RegisterHoursFragment"
    }

    override fun createIntent(context: Context, input: Void): Intent {
        return Intent(context, RegisterHoursFragment::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): WeekDayItem? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getParcelableExtra(ID)
        TODO("Not yet implemented")
    }
}