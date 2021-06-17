package com.example .list_creater_app.Items

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.list_creater_app.Database.Item
import com.example.list_creater_app.Database.ListDatabase
import com.example.list_creater_app.R
import com.example.list_creater_app.databinding.ItemFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ItemFragment : Fragment() {




    private lateinit var binding:ItemFragmentBinding
    lateinit var viewModel:ItemViewModel
    lateinit var adapter:ItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= ItemFragmentBinding.inflate(inflater,container,false)
        val application = requireNotNull(this.activity).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        val id=ItemFragmentArgs.fromBundle(arguments).id
        val itemViewModelFactory=ItemViewModelFactory(datasource,id)
        viewModel = ViewModelProvider(this,itemViewModelFactory).get(ItemViewModel::class.java)
        adapter= ItemAdapter()
        binding.item.adapter=adapter
        val manager = LinearLayoutManager(activity)
        binding.item.layoutManager = manager
        viewModel._item.observe(viewLifecycleOwner,{
            adapter.submitList(it)
        })

        //Toast.makeText(activity,"hello $id",Toast.LENGTH_LONG).show()
        binding.addItem.setOnClickListener {
            showBottomSheetDialogToAddItemInList(id)
        }
        setClickHandlerForItemRecyclerView()

        return binding.root
    }


    fun setClickHandlerForItemRecyclerView(){
        adapter.seTitemAdapterClickHandler(object :ItemAdapterClickHandler{
            override fun info(item: Item) {
                showBottomSheetDialogToShowItemInList(item)
                //showBottomSheetDialogToAddItemInList(1)
            }

        })
    }

private fun showBottomSheetDialogToShowItemInList(item: Item){
    val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext())
    bottomSheetDialog.setContentView(R.layout.show_item)
    bottomSheetDialog.setCanceledOnTouchOutside(true)


    // Initializing the views of item
    val itemName=bottomSheetDialog.findViewById<TextView>(R.id.itemName)
    val quantity=bottomSheetDialog.findViewById<TextView>(R.id.quantity)
    val quantityUnit=bottomSheetDialog.findViewById<TextView>(R.id.quantityUnit)
    val rate=bottomSheetDialog.findViewById<TextView>(R.id.rate)
    val description=bottomSheetDialog.findViewById<TextView>(R.id.description)

    // setting the content of views

    itemName?.text=item.itemName
    quantity?.text= item.quantity.toString()
    quantityUnit?.text=item.quantityUnit
    rate?.text=item.amount.toString()
    description?.text=item.itemDescription
    bottomSheetDialog.show()

}





// bottom sheet dialog to add item in list
private fun showBottomSheetDialogToAddItemInList(id:Long) {
    val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    bottomSheetDialog.setContentView(R.layout.additem)
    bottomSheetDialog.setCanceledOnTouchOutside(true)
    val add: Button? =bottomSheetDialog.findViewById(R.id.add)
    add?.setText("ADD")
    add?.setOnClickListener{
        val itemName=bottomSheetDialog.findViewById<EditText>(R.id.itemName)
        val quantity=bottomSheetDialog.findViewById<EditText>(R.id.quantity)
        val amount=bottomSheetDialog.findViewById<EditText>(R.id.amount)
        val description=bottomSheetDialog.findViewById<EditText>(R.id.description)
        val spinner=bottomSheetDialog.findViewById<Spinner>(R.id.weightspinner)
        val adapter=ArrayAdapter.createFromResource(requireContext(),
        R.array.weightUnit, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter=adapter
        var unit=spinner?.selectedItem.toString()
        if(unit=="None"){
            unit=""
        }
        val item= Item(list_id = id,itemName = itemName?.text.toString(),quantity = quantity?.text.toString().toFloat(),
        amount = amount?.text.toString().toFloat(),
        itemDescription = description?.text.toString(),
                quantityUnit = unit
        )
       viewModel.insert_list(item)

        bottomSheetDialog.cancel()
    }

    bottomSheetDialog.show()

}

}