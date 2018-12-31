package com.test.movies.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeVisibleOrGone(visible: Boolean) {
    if (visible) makeVisible() else makeGone()
}

fun View.showSystemMessage(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun <T> Call<T>.enqueue(handler: (response: T?) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) {
            handler(if (response.isSuccessful) response.body() else null)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            android.util.Log.d("Enqueue", t.toString())
            handler(null)
        }
    })
}

fun String.formatAsDate(): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    return try {
        outputFormat.format(inputFormat.parse(this))
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun Context.glide(url: String) = Glide.with(this).load(url)


fun RequestBuilder<Drawable>.loadInto(imageView: ImageView, listener: RequestListener<Drawable>? = null) {
    apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
        .apply { if (listener != null) listener(listener) }
        .into(imageView)
}