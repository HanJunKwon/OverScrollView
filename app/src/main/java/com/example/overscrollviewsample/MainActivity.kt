package com.example.overscrollviewsample

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.kwon.overscrollview.RefreshOverScrollView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val view = layoutInflater.inflate(R.layout.layout_refresh, null, false)
        val loadingImageView = view.findViewById<ImageView>(R.id.iv_loading)
        val imageLoader = ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                }
                add(GifDecoder.Factory())
            }
            .build()

        loadingImageView.load(R.raw.loading, imageLoader) {
            crossfade(true)
        }

        findViewById<RefreshOverScrollView>(R.id.refresh_over_scroll_view).setCustomHeader(
            view
        )
    }
}