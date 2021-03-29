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

/**
 * Load Image binding adapter used with data binding,
 * this method is responsible for loading and saving the bitmap
 * @param iv actual ImageView to load bitmap into
 * @param image string representing image hash
 * @param errorRes optional drawable to set on load failure
 */
@BindingAdapter(value = ["app:image",  "app:errorRes"], requireAll = false)
fun loadImage(iv : ImageView, image : String?, errorRes : Int?) {
    val context = iv.context
    if(image != null){
        val loader = ImageLoader.Builder(context).build()
        val cacheFile = File(context.filesDir, image)
        val req =
            if (cacheFile.exists()) {
                //try loading from disk is image is already saved
                ImageRequest.Builder(context)
                    .data(cacheFile)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        iv.setImageBitmap(bitmap)
                    }
            } else {
                //otherwise load from network and save bitmap
                ImageRequest.Builder(context)
                    .data(context.getString(R.string.RESOURCE_URL) + image)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        bitmap.save(context, image)
                        iv.setImageBitmap(bitmap)
                    }
            }

        //set a shimmer drawable as loading indicator
        val d = ShimmerDrawable()
        val s = Shimmer.AlphaHighlightBuilder().setDuration(1800).setBaseAlpha(1f)
            .setHighlightAlpha(0.6f).setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true).build()
        d.setShimmer(s)
        iv.setImageDrawable(d)
        req.error(errorRes ?: R.mipmap.ic_launcher)

        loader.enqueue(req.build())
    }
    else{
        //set default/error image if hash is null
        iv.setImageResource(errorRes ?: R.mipmap.ic_launcher)
    }
}