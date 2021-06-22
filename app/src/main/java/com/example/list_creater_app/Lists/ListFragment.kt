package com.example.list_creater_app.Lists

import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.list_creater_app.DashBoard.dashboardFragmentDirections
import com.example.list_creater_app.DashBoard.dashboardFragmentDirections.actionDashboardToItemFragment
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
        binding= ListFragmentBinding.inflate(inflater, container, false)
       // binding= DataBindingUtil.inflate(inflater,R.layout.list_fragment,container,false)
        val application = requireNotNull(this.activity).application
        val datasource=ListDatabase.getInstance(application).sleepDatabaseDao
        // setting the viewModel and viewModelFactory
        val listViewmodelFactory=ListViewmodelFactory(datasource)
         viewModel = ViewModelProvider(this, listViewmodelFactory).get(ListViewModel::class.java)



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
               if(it!=null) {
                   adapter.submitList(it)
                  if(it.size==0){
                      binding.emptylist.visibility=View.VISIBLE
                  }
                   else{
                      binding.emptylist.visibility=View.GONE
                  }

               }

           }



        // observing the ok button



        viewModel.ok.observe(viewLifecycleOwner){
            if(it){

                val size: Int? = viewModel._itemList.value?.size?.minus(1)
                if(size!=null) {

                    val id: Long? = viewModel._itemList.value?.get(size)?.listId
                    val name:String?=viewModel._itemList.value?.get(size)?.listName
                    if(id!=null && name!=null) {
                        findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(id).setListname(name))

                    }
                }
                viewModel.ok.value=false
            }
        }




        // observing end


        setListRecyclerViewItemClickHandler()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.title="Your Lists"
        return binding.root
    }

    private fun setListRecyclerViewItemClickHandler() {
        adapter.seTItemListAdapterClickHandle(object : ItemListAdapterClickHandle {
            override fun layoutClick(id: Long,name:String) {
                findNavController().navigate(ListFragmentDirections.actionListFragmentToItemFragment().setId(id).setListname(name))
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



    // Alert Dialog for share
//    fun share(){
//        val items= arrayOf("ItemName", "Quantity", "Rate", "Description", "Total Quantity", "Total Amount")
//        val checkedItems= booleanArrayOf(true, false, false, false, false, false)
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Select the Item you want to Share")
//        builder.setMultiChoiceItems(items, checkedItems, OnMultiChoiceClickListener
//        { dialogInterface, i, b ->
//            checkedItems[i] = b
//        })
//        builder.setPositiveButton("OK", DialogInterface.OnClickListener
//        { dialogInterface, i ->
//
//            val s = StringBuilder()
//            for (i in 0..checkedItems.size - 1) {
//                if (checkedItems[i]) {
//                    s.append(items[i])
//                    s.append(" ")
//                }
//            }
//            Toast.makeText(requireContext(), s.toString(), Toast.LENGTH_LONG).show()
//
//        })
//        builder.setNegativeButton("CANCEL", null)
//        builder.show()
//    }

    // bottom sheet dialog to update the list
    private fun showBottomSheetDialogForUpdateList(itemlist: ItemList) {
        val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(R.layout.add_list)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        val text=bottomSheetDialog.findViewById<EditText>(R.id.add_list_name)
        text?.setText(itemlist.listName)
        val update: Button? =bottomSheetDialog.findViewById(R.id.add)
        update?.setText("Update")
        update?.setOnClickListener{

            val item=ItemList(listName = text?.text.toString(), listId = itemlist.listId)
            viewModel.updateList(item)

            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.show()
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
            val item=ItemList(listName = text?.text.toString())
            viewModel.clicked(item)

            bottomSheetDialog.cancel()
        }

        bottomSheetDialog.show()

    }

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


}