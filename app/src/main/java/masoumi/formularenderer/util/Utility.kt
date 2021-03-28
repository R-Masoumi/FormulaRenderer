package masoumi.formularenderer.util

import android.content.Context
import android.graphics.Bitmap
import android.util.TypedValue
import androidx.annotation.DimenRes
import java.io.FileOutputStream
import java.io.IOException

object Utility {
    fun Bitmap.save(context: Context, name : String){
        try {
            context.openFileOutput(name, Context.MODE_PRIVATE).use { out ->
                compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun Context.dipToPixels(@DimenRes dipRes: Int) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resources.getDimension(dipRes), resources.displayMetrics).toInt()
}
