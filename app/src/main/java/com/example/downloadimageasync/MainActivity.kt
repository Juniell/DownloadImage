package com.example.downloadimageasync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultAddress = "https://clck.ru/SMLw2"

        async_btn.setOnClickListener {
            val startTime = System.currentTimeMillis()
            DownloadImageTask(image).execute(
                if (url_text.text.isNotEmpty())
                    url_text.text.toString()
                else
                    defaultAddress
            )
            Log.i(
                "download_time",
                "Time for AsyncTask = ${System.currentTimeMillis() - startTime} ms"
            )
        }

        cor_btn.setOnClickListener {
            val startTime = System.currentTimeMillis()
            coroutine(
                if (url_text.text.isNotEmpty())
                    url_text.text.toString()
                else
                    defaultAddress
            )
            Log.i(
                "download_time",
                "Time for Coroutine = ${System.currentTimeMillis() - startTime} ms"
            )
        }

        glide_btn.setOnClickListener {
            val startTime = System.currentTimeMillis()
            Glide
                .with(this)
                .load(
                    if (url_text.text.isNotEmpty())
                        url_text.text.toString()
                    else
                        defaultAddress
                )
                .into(image)
            Log.i("download_time", "Time for Glide = ${System.currentTimeMillis() - startTime} ms")
        }
    }

    class DownloadImageTask(var bmImage: ImageView) :
        AsyncTask<String?, Void?, Bitmap?>() {

        override fun doInBackground(vararg params: String?): Bitmap? {
            val url = params[0]
            var mIcon11: Bitmap? = null
            try {
                val inp = URL(url).openStream()
                mIcon11 = BitmapFactory.decodeStream(inp)
            } catch (e: Exception) {
                Log.e("Error", e.message!!)
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }
    }

    private fun coroutine(param: String) {
        lifecycle.coroutineScope.launchWhenResumed {
            image.setImageBitmap(downloadCor(param))
        }
    }

    private suspend fun downloadCor(param: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val url = param
            var mIcon11: Bitmap? = null
            try {
                val inp = URL(url).openStream()
                mIcon11 = BitmapFactory.decodeStream(inp)
            } catch (e: Exception) {
                Log.e("Error", e.message!!)
                e.printStackTrace()
            }
            return@withContext mIcon11
        }
}