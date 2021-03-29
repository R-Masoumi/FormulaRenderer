package masoumi.formularenderer.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import dagger.hilt.android.AndroidEntryPoint
import masoumi.formularenderer.R
import masoumi.formularenderer.ui.adapter.SuggestionAdapter
import masoumi.formularenderer.databinding.FragmentHomeBinding
import masoumi.formularenderer.util.Utility.dipToPixels
import masoumi.formularenderer.ui.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
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

        viewModel.loading.observe(viewLifecycleOwner){
            if (it) {
                binding.tilSearch.startIconDrawable = progressDrawable
            } else {
                binding.tilSearch.startIconDrawable = sendDrawable
            }
        }

        binding.tilSearch.setStartIconOnClickListener {
            setQuery()
        }

        binding.searchBar.setOnItemClickListener { _, _, _, _ ->
            setQuery()
        }

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setQuery()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

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

    private fun setQuery(){
        val query = binding.searchBar.text?.toString()
        if(!query.isNullOrEmpty()){ viewModel.setQuery( query.replace("\\s+".toRegex(),"")) }
    }
}