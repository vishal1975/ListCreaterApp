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
            if(it!=null) {
                adapter.submitList(it)
                setQuantityAmount(it)
            }
        })

        //Toast.makeText(activity,"hello $id",Toast.LENGTH_LONG).show()
        binding.addItem.setOnClickListener {
            showBottomSheetDialogToAddOrEditItemInList(id)
        }
        setClickHandlerForItemRecyclerView()

        return binding.root
    }




    // setting the quantity and amount of list
    fun setQuantityAmount(item: List<Item>){
        var totalQuantity:Double=0.0
        var totalAmount:Double=0.0
        for(items in item){
            totalQuantity+=items.quantity
            totalAmount+=items.quantity*items.amount
        }
        binding.quantity.text=totalQuantity.toString()
        binding.rate.text=totalAmount.toString()
    }
    fun setClickHandlerForItemRecyclerView(){
        adapter.seTitemAdapterClickHandler(object :ItemAdapterClickHandler{
            override fun info(item: Item) {
                showBottomSheetDialogToShowItemInList(item)

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
    val edit=bottomSheetDialog.findViewById<ImageView>(R.id.edit)
    val delete=bottomSheetDialog.findViewById<ImageView>(R.id.delete)

    // setting the clicklistner for edit
    edit?.setOnClickListener(){
        showBottomSheetDialogToAddOrEditItemInList(item.list_id,1,item)
     bottomSheetDialog.cancel()
    }
    // setting the clicklistner for delete
    delete?.setOnClickListener(){

    }


    // setting the content of views

    itemName?.text=item.itemName
    quantity?.text= item.quantity.toString()
    quantityUnit?.text=item.quantityUnit
    rate?.text=item.amount.toString()
    description?.text=item.itemDescription
    bottomSheetDialog.show()

}

// bottom sheet dialog to edit item list




// bottom sheet dialog to add item in list
private fun showBottomSheetDialogToAddOrEditItemInList(id:Long,flag:Int=0,item:Item?=null) {
    val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    bottomSheetDialog.setContentView(R.layout.additem)
    bottomSheetDialog.setCanceledOnTouchOutside(true)
    val add: Button? =bottomSheetDialog.findViewById(R.id.add)
    // editext for quantity
    val quantity=bottomSheetDialog.findViewById<EditText>(R.id.quantity)
    // editext for amount
    val amount=bottomSheetDialog.findViewById<EditText>(R.id.amount)
    // editext for itemName
    val itemName=bottomSheetDialog.findViewById<EditText>(R.id.itemName)
    // editext for description
    val description=bottomSheetDialog.findViewById<EditText>(R.id.description)
    // spinner
    val spinner=bottomSheetDialog.findViewById<Spinner>(R.id.weightspinner)
    val adapter=ArrayAdapter.createFromResource(requireContext(),
            R.array.weightUnit, android.R.layout.simple_spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner?.adapter=adapter
    // unit of item
    var unit=spinner?.selectedItem.toString()
    // checking whether we have to edit or add
    if(flag==1 && item!=null){
        add?.setText("Update")
        // setting the text for respective field
        itemName?.setText(item.itemName)
        quantity?.setText(item.quantity.toString())
        amount?.setText(item.amount.toString())
        description?.setText(item.itemDescription)
    }
    else {
        add?.setText("ADD")
    }
    add?.setOnClickListener{


        // checking the quantity
        if(quantity?.text.toString().isEmpty()){
            quantity?.setText("0")
        }
       // checking the amount
        if(amount?.text.toString().isEmpty()){
            amount?.setText("0")
        }
       // checking the unit
        if(unit=="None"){
            unit=""
        }
         if(flag==1 && item!=null){
             val newitem = Item(list_id = id, itemName = itemName?.text.toString(), quantity = quantity?.text.toString().toDouble(),
                     amount = amount?.text.toString().toDouble(),
                     itemDescription = description?.text.toString(),
                     quantityUnit = unit,
                     itemId = item.itemId)
             viewModel.updateItem(newitem)
             Toast.makeText(requireContext(),"$id , ${item.list_id}",Toast.LENGTH_LONG).show()
         }
        else {
             // setting the item
             val newitem = Item(list_id = id, itemName = itemName?.text.toString(), quantity = quantity?.text.toString().toDouble(),
                     amount = amount?.text.toString().toDouble(),
                     itemDescription = description?.text.toString(),
                     quantityUnit = unit
             )
             viewModel.insert_list(newitem)

         }
        bottomSheetDialog.cancel()
    }

    bottomSheetDialog.show()

}

}