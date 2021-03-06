package masoumi.formularenderer.ui.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import kotlinx.coroutines.runBlocking

/**
 * Suggestion Array Adapter, responsible for providing suggestions to an AutoCompleteTextView
 * @param context UI context
 * @param viewRedId layout resource id for viewing each suggestion
 * @param filterPhrase a suspending function to call in a worker threat and receive suggestion list
 */
class SuggestionAdapter(context: Context, @LayoutRes viewRedId: Int,
                        private val filterPhrase : suspend (phrase : String) -> List<String>)
    : ArrayAdapter<String>(context, viewRedId) {
    private val data = ArrayList<String>()

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): String {
        return data[index]
    }

    override fun getFilter() = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults = runBlocking {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val trimmedPhrase = constraint.toString().replace("\\s+".toRegex(),"")
                    val filtered = filterPhrase(trimmedPhrase)
                    data.clear()
                    data.addAll(filtered)
                    filterResults.values = data
                    filterResults.count = data.size
                }
                filterResults
            }

            override fun publishResults(contraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
}