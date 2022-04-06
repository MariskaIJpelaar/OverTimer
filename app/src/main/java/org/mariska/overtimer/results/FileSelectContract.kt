package org.mariska.overtimer.results

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class FileSelectContract : ActivityResultContract<Void, Uri?>() {
    override fun createIntent(context: Context, input: Void): Intent {
        return Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT).apply {  }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.data
    }
}