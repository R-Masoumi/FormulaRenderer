package masoumi.formularenderer.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import masoumi.formularenderer.data.net.DataSource

class SuggestionAdapter(context: Context, @LayoutRes viewRedId: Int,
                        private val dataSource: DataSource)
    : ArrayAdapter<String>(context, viewRedId) {
    private val data = ArrayList<String>()

    fun getFilterFormula(phrase : String) = dataSource.getFormulasBlocking(phrase)

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): String {
        return data[index]
    }

    override fun getFilter() = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val filtered = getFilterFormula(constraint.toString())
                    data.clear()
                    data.addAll(filtered.map { it.formula })
                    filterResults.values = data
                    filterResults.count = data.size
                }
                return filterResults
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