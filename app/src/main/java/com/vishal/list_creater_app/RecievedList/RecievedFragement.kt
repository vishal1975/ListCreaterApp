package com.vishal.list_creater_app.RecievedList

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabase
import com.vishal.list_creater_app.Lists.*
import com.vishal.list_creater_app.Lists.ItemListAdapterClickHandle
import com.vishal.list_creater_app.R
import com.vishal.list_creater_app.databinding.ListFragmentBinding

class RecievedFragement : Fragment() {



    private lateinit var binding: ListFragmentBinding
    private lateinit var viewModel: RecievedListViewModel
    private lateinit var adapter: RecievedListAdapter
    var text: EditText?=null
    var bottomSheetDialog: BottomSheetDialog?=null
    private  val SPEECH_REQUEST_CODE= 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding= ListFragmentBinding.inflate(inflater, container, false)
        // binding= DataBindingUtil.inflate(inflater,R.layout.list_fragment,container,false)
        val application = requireNotNull(this.activity).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        // setting the viewModel and viewModelFactory
        val recievedViewmodelFactory= RecievedViewModelFactory(datasource)
        viewModel = ViewModelProvider(this, recievedViewmodelFactory).get(RecievedListViewModel::class.java)



        // setting the recyclerview and adapter
        adapter= RecievedListAdapter(viewModel)
        binding.allList.adapter=adapter
        val manager = LinearLayoutManager(activity)
        binding.allList.layoutManager = manager

        binding.addList.hide()

        // adding listName

//        binding.addList.setOnClickListener(){
//            showBottomSheetDialogForAddListName()
//        }




        // observing the itemList
        viewModel._itemList.observe(viewLifecycleOwner){
            if(it!=null) {
                adapter.submitList(it)
                if(it.size==0){
                    binding.emptylist.visibility= View.VISIBLE
                }
                else{
                    binding.emptylist.visibility= View.GONE
                }

            }

        }



        // observing the ok button



//        viewModel.ok.observe(viewLifecycleOwner){
//            if(it){
//
//                val size: Int? = viewModel._itemList.value?.size?.minus(1)
//                if(size!=null) {
//
//                    val id: Long? = viewModel._itemList.value?.get(size)?.listId
//                    val name:String?=viewModel._itemList.value?.get(size)?.listName
//                    if(id!=null && name!=null) {
//                        findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(id).setListname(name))
//
//                    }
//                }
//                viewModel.ok.value=false
//            }
//        }




        // observing end
        // ads implementation
        implement_Ads();
        setListRecyclerViewItemClickHandler()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.title=resources.getString(R.string.your_list)
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.deep_purple_300)))
        return binding.root
    }

    private fun setListRecyclerViewItemClickHandler() {
        adapter.seTItemListAdapterClickHandle(object : RecievedItemListAdapterClickHandle {
            override fun layoutClick(id: Long,name:String) {
               // findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(id).setListname(name))
                findNavController().navigate(RecievedFragementDirections.actionRecievedFragementToItemFragment().setId(id).setListname(name))
            }

            override fun onDelete(id: Long) {
                showBottomSheetDialogForDeleteList(id)
            }

            override fun onEdit(item: ItemList) {


                showBottomSheetDialogForUpdateList(item)
            }

            override fun onShare(id: Long) {
                // share()
            }


        })
    }





    // bottom sheet dialog to update the list
    private fun showBottomSheetDialogForUpdateList(itemlist: ItemList) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog?.setContentView(R.layout.add_list)
        bottomSheetDialog?.setCanceledOnTouchOutside(true)
        text=bottomSheetDialog?.findViewById<EditText>(R.id.add_list_name)
        text?.setText(itemlist.listName)
        val update: Button? =bottomSheetDialog?.findViewById(R.id.add)
        update?.setText(getString(R.string.update))
        val mic: ImageView? =bottomSheetDialog?.findViewById(R.id.mic)
        mic?.setOnClickListener{
            displaySpeechRecognizer()


        }
        update?.setOnClickListener{

            val item= ItemList(listName = text?.text.toString(), listId = itemlist.listId,type = 1)
            viewModel.updateList(item)

            bottomSheetDialog?.cancel()
        }

        bottomSheetDialog?.show()
    }


    // bottomsheet dialog for adding list name
//    private fun showBottomSheetDialogForAddListName() {
//        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
//        bottomSheetDialog?.setContentView(R.layout.add_list)
//        bottomSheetDialog?.setCanceledOnTouchOutside(true)
//        val create: Button? =bottomSheetDialog?.findViewById(R.id.add)
//        create?.setText(getString(R.string.create))
//        text=bottomSheetDialog?.findViewById<EditText>(R.id.add_list_name)
//        val mic: ImageView? =bottomSheetDialog?.findViewById(R.id.mic)
//        mic?.setOnClickListener{
//            displaySpeechRecognizer()
//
//
//        }
//        create?.setOnClickListener{
//
//            val item= ItemList(listName = text?.text.toString())
//            viewModel.clicked(item)
//
//            bottomSheetDialog?.cancel()
//        }
//
//        bottomSheetDialog?.show()
//
//    }

    // bottomsheet dialog for deleting the list from the database

    private fun showBottomSheetDialogForDeleteList(id: Long) {
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(binding.root.context, R.style.DialogStyle)
        bottomSheetDialog.setContentView(R.layout.bottomdialogfordelete)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        val ok: Button? =bottomSheetDialog.findViewById(R.id.ok)
        ok?.setOnClickListener{

            viewModel.deleteList(id)

            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.show()

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
    fun implement_Ads(){


        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError)
                binding.adView.loadAd(adRequest)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }




}