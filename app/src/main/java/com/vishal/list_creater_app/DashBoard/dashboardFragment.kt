package com.vishal.list_creater_app.DashBoard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabase
import com.vishal.list_creater_app.R
import com.vishal.list_creater_app.databinding.FragmentDashboardBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class dashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private  val SPEECH_REQUEST_CODE = 0
lateinit var binding:FragmentDashboardBinding
lateinit var viewModel:DashBoardViewModel

    var text:EditText?=null
    var bottomSheetDialog:BottomSheetDialog?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //(requireActivity() as AppCompatActivity).supportActionBar?.title="Dashboard"
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding= FragmentDashboardBinding.inflate(inflater,container,false)

        val application = requireNotNull(this.activity).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        // setting the viewModel and viewModelFactory
        val dashboardViewmodelFactory= DashboardViewModelFactory(datasource)
        viewModel = ViewModelProvider(this, dashboardViewmodelFactory).get(DashBoardViewModel::class.java)
          viewModel.ok.observe(viewLifecycleOwner){
              if(it){

                      val size: Int? = viewModel._itemList.value?.size?.minus(1)
                  if(size!=null) {

                      val id: Long? = viewModel._itemList.value?.get(size)?.listId
                      val name:String?=viewModel._itemList.value?.get(size)?.listName
                      if(id!=null && name!=null) {
                          findNavController().navigate(dashboardFragmentDirections.actionDashboardToItemFragment().setId(id).setListname(name))

                      }
                  }
                  viewModel.ok.value=false
              }
          }



        binding.createlist.setOnClickListener(){
            showBottomSheetDialogForAddListName()
        //findNavController().navigate(dashboardFragmentDirections.actionDashboardToListFragment2())
        }

        binding.viewlist.setOnClickListener(){
            findNavController().navigate(dashboardFragmentDirections.actionDashboardToListFragment2())
        }
        return binding.root
    }

    // bottomsheet dialog for adding list name
    private fun showBottomSheetDialogForAddListName() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog?.setContentView(R.layout.add_list)
        bottomSheetDialog?.setCanceledOnTouchOutside(true)
        text=bottomSheetDialog?.findViewById<EditText>(R.id.add_list_name)
        val create: Button? =bottomSheetDialog?.findViewById(R.id.add)
        val mic: ImageView? =bottomSheetDialog?.findViewById(R.id.mic)
        mic?.setOnClickListener{
            displaySpeechRecognizer()


        }

        create?.setText(resources.getString(R.string.create))

        create?.setOnClickListener{

            val item= ItemList(listName = text?.text.toString())
            viewModel.clicked(item)

            bottomSheetDialog?.cancel()
        }


        bottomSheetDialog?.show()

    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        }
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                        results?.get(0)
                    }
            // Do something with spokenText.
            if(bottomSheetDialog!=null) {
                if (bottomSheetDialog?.isShowing == true){
                    text?.setText(spokenText)
                }
            }



        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}