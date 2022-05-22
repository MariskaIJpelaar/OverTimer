package org.mariska.overtimer.results

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class FileSelectContract : ActivityResultContract<Void?, Uri?>() {
    override fun createIntent(context: Context, input: Void?): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).setType("text/plain").addCategory(Intent.CATEGORY_OPENABLE).apply {  }
        return Intent.createChooser(intent, "Choose a log-file")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.data
    }
}