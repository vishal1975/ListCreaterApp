package com.example.list_creater_app.DashBoard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabase
import com.example.list_creater_app.Lists.ListFragmentDirections
import com.example.list_creater_app.Lists.ListViewModel
import com.example.list_creater_app.Lists.ListViewmodelFactory
import com.example.list_creater_app.R
import com.example.list_creater_app.databinding.FragmentDashboardBinding
import com.example.list_creater_app.databinding.ItemFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class dashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters



lateinit var binding:FragmentDashboardBinding
lateinit var viewModel:DashBoardViewModel
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
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(R.layout.add_list)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        val create: Button? =bottomSheetDialog.findViewById(R.id.add)
        create?.setText("Create")
        create?.setOnClickListener{
            val text=bottomSheetDialog.findViewById<EditText>(R.id.add_list_name)
            val item= ItemList(listName = text?.text.toString())
            viewModel.clicked(item)

            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.show()

    }
}