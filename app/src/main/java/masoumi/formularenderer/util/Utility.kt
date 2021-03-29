package masoumi.formularenderer.util

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import android.widget.EditText
import androidx.annotation.DimenRes
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility object for miscellaneous functionality
 */
object Utility {
    /**
     * Save bitmap to files dir
     * @param context application context
     * @param name image file name
     */
    fun Bitmap.save(context: Context, name : String){
        try {
            context.openFileOutput(name, Context.MODE_PRIVATE).use { out ->
                compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * dps to pixel converter method
     * @param dipRes dp resource id
     * @return pixel size equivalent
     */
    fun Context.dipToPixels(@DimenRes dipRes: Int) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resources.getDimension(dipRes), resources.displayMetrics).toInt()
}
