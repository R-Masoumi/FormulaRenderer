package masoumi.formularenderer.ui.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import dagger.hilt.android.AndroidEntryPoint
import masoumi.formularenderer.R
import masoumi.formularenderer.databinding.FragmentHomeBinding
import masoumi.formularenderer.ui.adapter.SuggestionAdapter
import masoumi.formularenderer.ui.viewmodel.HomeViewModel
import masoumi.formularenderer.util.Utility.dipToPixels
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * HomeFragment that shows the search bar and views rendered formula
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    //drawable icons for loading and submitting.
    private var progressDrawable : IndeterminateDrawable<CircularProgressIndicatorSpec>? = null
    private var sendDrawable : Drawable? = null

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setDrawable()
        val adapter = SuggestionAdapter(requireContext(),
                android.R.layout.simple_dropdown_item_1line){
            viewModel.getFormulasAsync(it)
        }
        binding.searchBar.setAdapter(adapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.check.observe(viewLifecycleOwner){
            if(it.isSuccess()){
                it.data?.hash?.let { hash ->
                    viewModel.setImageHash(hash)
                }
                it.data?.formatted?.let { formula ->
                    viewModel.setFormula(formula)
                }
            }
        }

        //fetch formula query and text inside editText, if app was on pause
        savedInstanceState?.getString("query")?.let { viewModel.setQuery(it) }
        binding.searchBar.setText(savedInstanceState?.getString("text"))

        //switch button drawable when loading
        viewModel.loading.observe(viewLifecycleOwner){
            if (it) {
                binding.tilSearch.startIconDrawable = progressDrawable
            } else {
                binding.tilSearch.startIconDrawable = sendDrawable
            }
        }

        //start checking formula on button click
        binding.tilSearch.setStartIconOnClickListener {
            setQuery()
        }

        //start checking formula on suggestion click
        binding.searchBar.setOnItemClickListener { _, _, _, _ ->
            setQuery()
        }

        //start checking formula on virtual keyboard done click
        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setQuery()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        //start share process on share button click
        binding.btnShare.setOnClickListener {
            val context = requireContext()

            //save image from internal to cache to prepare sharing
            val path = "${viewModel.formula.value ?: "formula"}.png"
            try {
                context.openFileInput(viewModel.image.value.orEmpty()).use { orig ->
                    FileOutputStream("${context.cacheDir}/$path").use { cache ->
                        orig.copyTo(cache)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@setOnClickListener
            }

            val file = File(context.cacheDir, path)
            val contentUri: Uri? = FileProvider.getUriForFile(context, context.getString(R.string.PROVIDER_AUTHORITY), file)

            //send share intent
            if (contentUri != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                shareIntent.setDataAndType(contentUri, "images/png")
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //save query and edit text content on app exit to memory
        outState.putString("query",viewModel.getQuery())
        outState.putString("text",binding.searchBar.toString())
    }


    //set loading and submit drawables
    private fun setDrawable(){
        val context = requireContext()
        val progressIndicatorSpec = CircularProgressIndicatorSpec(context, null)
        progressIndicatorSpec.indicatorInset = 0
        progressIndicatorSpec.trackColor = ContextCompat.getColor(
                context,
                R.color.loading_color
        )
        progressIndicatorSpec.indicatorSize = context.dipToPixels(R.dimen.loading_size)
        progressDrawable = IndeterminateDrawable.createCircularDrawable(
                context,
                progressIndicatorSpec
        )
        sendDrawable = ContextCompat.getDrawable(context, R.drawable.ic_submit)
    }

    //trim and set query to be checked
    private fun setQuery(){
        val query = binding.searchBar.text?.toString()
        if(!query.isNullOrEmpty()){ viewModel.setQuery(query.replace("\\s+".toRegex(), "")) }
    }
}