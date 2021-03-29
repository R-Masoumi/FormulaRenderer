package masoumi.formularenderer.util

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import masoumi.formularenderer.R
import masoumi.formularenderer.util.Utility.save
import java.io.File

@BindingAdapter(value = ["app:image",  "app:errorRes"], requireAll = false)
fun loadImage(iv : ImageView, image : String?, errorRes : Int?) {
    val context = iv.context
    if(image != null){
        val loader = ImageLoader.Builder(context).build()
        val cacheFile = File(context.filesDir, image)
        val req =
            if (cacheFile.exists()) {
                ImageRequest.Builder(context)
                    .data(cacheFile)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        iv.setImageBitmap(bitmap)
                    }
            } else {
                ImageRequest.Builder(context)
                    .data(context.getString(R.string.RESOURCE_URL) + image)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        bitmap.save(context, image)
                        iv.setImageBitmap(bitmap)
                    }
            }

        val d = ShimmerDrawable()
        val s = Shimmer.AlphaHighlightBuilder().setDuration(1800).setBaseAlpha(1f)
            .setHighlightAlpha(0.6f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true).build()
        d.setShimmer(s)
        req.placeholder(d)
        req.error(errorRes ?: R.mipmap.ic_launcher)

        loader.enqueue(req.build())
    }
    else{
        iv.setImageResource(errorRes ?: R.mipmap.ic_launcher)
    }
}