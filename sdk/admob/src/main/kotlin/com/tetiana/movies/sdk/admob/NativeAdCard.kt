package com.tetiana.movies.sdk.admob

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView

/** Renders a session-owned ad. Disposing the row releases its view, not the ad. */
@SuppressLint("InflateParams") // AndroidView supplies the root's parent and measurement constraints.
@Composable
internal fun NativeAdCard(ads: AdMobNativeSession, slot: Int, modifier: Modifier = Modifier) {
    val ad = ads.ad(slot) ?: return
    val colors = MaterialTheme.colorScheme
    key(ad) {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.native_ad, null) as NativeAdView
                if (ads.attach(slot, ad, view)) {
                    bind(view, ad)
                    view.doOnLayout {
                        if (ads.isAttached(slot, ad, view)) {
                            view.registerNativeAd(ad, view.findViewById<MediaView>(R.id.ad_media))
                        }
                    }
                } else {
                    view.destroy()
                }
                view
            },
            update = { view ->
                if (ads.isAttached(slot, ad, view)) {
                    val content = view.findViewById<View>(R.id.ad_content)
                    val background = content.background as? GradientDrawable ?: GradientDrawable().apply {
                        cornerRadius = view.resources.getDimension(R.dimen.native_ad_corner_radius)
                        content.background = this
                    }
                    background.setColor(colors.surfaceContainer.toArgb())
                    listOf(R.id.ad_attribution, R.id.ad_headline, R.id.ad_advertiser).forEach { id ->
                        view.findViewById<TextView>(id).setTextColor(colors.onSurface.toArgb())
                    }
                    view.findViewById<Button>(R.id.ad_action).apply {
                        setTextColor(colors.onPrimary.toArgb())
                        backgroundTintList = ColorStateList.valueOf(colors.primary.toArgb())
                    }
                }
            },
            onRelease = { view -> ads.detach(slot, view) },
            modifier = modifier.fillMaxWidth(),
        )
    }
}

private fun bind(view: NativeAdView, ad: NativeAd) {
    view.headlineView = view.findViewById<TextView>(R.id.ad_headline).apply { text = ad.headline }
    view.callToActionView = view.findViewById<Button>(R.id.ad_action).apply { text = ad.callToAction }
    view.findViewById<ImageView>(R.id.ad_icon).apply {
        val icon = ad.icon
        isVisible = icon != null
        setImageDrawable(icon?.drawable)
        if (icon != null) view.iconView = this
    }
    view.findViewById<TextView>(R.id.ad_advertiser).apply {
        text = ad.advertiser
        isVisible = !ad.advertiser.isNullOrBlank()
        if (isVisible) view.advertiserView = this
    }
    view.findViewById<MediaView>(R.id.ad_media).apply { mediaContent = ad.mediaContent }
}
