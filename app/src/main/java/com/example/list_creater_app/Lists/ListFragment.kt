package com.example.list_creater_app.Lists

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.list_creater_app.Database.ItemList
import com.example.list_creater_app.Database.ListDatabase
import com.example.list_creater_app.R
import com.example.list_creater_app.databinding.ListFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ListFragment : Fragment() {




    private lateinit var binding: ListFragmentBinding
    private lateinit var viewModel: ListViewModel
    private lateinit var adapter: ItemListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ListFragmentBinding.inflate(inflater,container,false)
       // binding= DataBindingUtil.inflate(inflater,R.layout.list_fragment,container,false)
        val application = requireNotNull(this.activity).application
        val datasource=ListDatabase.getInstance(application).sleepDatabaseDao
        // setting the viewModel and viewModelFactory
        val listViewmodelFactory=ListViewmodelFactory(datasource)
         viewModel = ViewModelProvider(this,listViewmodelFactory).get(ListViewModel::class.java)



        // setting the recyclerview and adapter
        adapter=ItemListAdapter(viewModel)
       binding.allList.adapter=adapter
        val manager = LinearLayoutManager(activity)
        binding.allList.layoutManager = manager



        // adding listName

        binding.addList.setOnClickListener(){
            showBottomSheetDialogForAddListName()
        }




        // observing the itemList
           viewModel._itemList.observe(viewLifecycleOwner){

               adapter.submitList(it)

           }


        setListRecyclerViewItemClickHandler()

        return binding.root
    }

    private fun setListRecyclerViewItemClickHandler() {
        adapter.seTItemListAdapterClickHandle(object :ItemListAdapterClickHandle{
            override fun layoutClick(id: Long) {
                findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(id))
            }

            override fun onDelete(id: Long) {
                showBottomSheetDialogForDeleteList(id)
            }

            override fun onEdit(item: ItemList) {
                TODO("Not yet implemented")
            }

            override fun onShare(id: Long) {
                TODO("Not yet implemented")
            }


        })
    }


    // bottomsheet dialog for adding list name
    private fun showBottomSheetDialogForAddListName() {
       val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(R.layout.add_list)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        val create: Button? =bottomSheetDialog.findViewById(R.id.create)
        create?.setOnClickListener{
            val text=bottomSheetDialog.findViewById<EditText>(R.id.add_list_name)
            val item=ItemList(listName = text?.text.toString())
            viewModel.insert_list(item)

            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.show()

    }

    // bottomsheet dialog for deleting the list from the database

    private fun showBottomSheetDialogForDeleteList(id:Long) {
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


}