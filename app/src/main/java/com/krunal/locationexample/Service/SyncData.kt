package com.krunal.locationexample.Service

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import com.krunal.locationexample.Activity.MainActivity
import com.krunal.locationexample.Utility.ProgressResponseBody.OnAttachmentDownloadListener
import java.lang.ref.WeakReference

class SyncData  internal constructor(context: Context,onAttachmentDownloadListener: OnAttachmentDownloadListener) : AsyncTask<Int, Int, String?>() {

    //extends ResponseBody {

    private var resp: String? = null
    private val activityReference: WeakReference<Context> = WeakReference(context)
    private val progressListener: OnAttachmentDownloadListener = onAttachmentDownloadListener

    override fun onPreExecute() {
        val activity = activityReference.get() ?: return
    }

    override fun doInBackground(vararg params: Int?): String? {
        publishProgress(0) // Calls onProgressUpdate()
        try {
            val time = params[0]?.times(1000)
            time?.toLong()?.let { Thread.sleep(it / 2) }
            publishProgress(50) // Calls onProgressUpdate()
            time?.toLong()?.let { Thread.sleep(it / 2) }
            publishProgress(100) // Calls onProgressUpdate()
            resp = "Android was sleeping for " + params[0] + " seconds"
        } catch (e: InterruptedException) {
            e.printStackTrace()
            resp = e.message
        } catch (e: Exception) {
            e.printStackTrace()
            resp = e.message
        }

        return resp
    }


    override fun onPostExecute(result: String?) {

        val activity = activityReference.get() ?: return
    }

    override fun onProgressUpdate(vararg text: Int?) {

        val activity = activityReference.get() ?: return

        text.let {
            it[0]?.let { it1 -> progressListener.onAttachmentDownloadUpdate(it1) }
        }

    }
}