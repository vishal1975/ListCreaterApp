package com.example .list_creater_app.Items

import android.app.Notification
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.list_creater_app.Database.Item
import com.example.list_creater_app.Database.ListDatabase
import com.example.list_creater_app.R
import com.example.list_creater_app.databinding.ItemFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.list_creater_app.Items.ItemAdapter
import com.example.list_creater_app.Items.ItemFragmentArgs
import com.example.list_creater_app.Items.ItemViewModel
import com.example.list_creater_app.Items.ItemViewModelFactory

class ItemFragment : Fragment() {




    private lateinit var binding:ItemFragmentBinding
    lateinit var viewModel: ItemViewModel
    lateinit var adapter: ItemAdapter
    var final_totalItems=0
    var final_totalAmount=0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= ItemFragmentBinding.inflate(inflater,container,false)
        val application = requireNotNull(this.activity).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        val id= ItemFragmentArgs.fromBundle(arguments).id
        val name=ItemFragmentArgs.fromBundle(arguments).listname
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
                if(it.size==0){
                    binding.emptyitem.visibility=View.VISIBLE
                }
                else{
                    binding.emptyitem.visibility=View.GONE
                }
            }
        })

        //Toast.makeText(activity,"hello $id",Toast.LENGTH_LONG).show()
        binding.addItem.setOnClickListener {
            showBottomSheetDialogToAddOrEditItemInList(id)
        }
        binding.share.setOnClickListener(){
            viewModel._item.value?.let { it1 -> share(it1) }
        }
        setClickHandlerForItemRecyclerView()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.title= "$name"
        return binding.root
    }


   // alert dialog to share a list
   fun share(itemList:List<Item>){
       val items= arrayOf("ItemName", "Quantity", "Rate", "Description", "Total Items", "Total Amount")
       val checkedItems= booleanArrayOf(true, false, false, false, false, false)
       val builder = AlertDialog.Builder(requireContext())
       builder.setTitle("Select the Item Property you want to Share")
       builder.setMultiChoiceItems(items, checkedItems, OnMultiChoiceClickListener
       { dialogInterface, i, b ->
           checkedItems[i] = b
       })
       builder.setPositiveButton("OK", DialogInterface.OnClickListener
       { dialogInterface, i ->

           val s = StringBuilder()

           for(item in itemList) {
               if (checkedItems[0]) {
                   s.append(item.itemName).append("\n\n")
               }
               if(checkedItems[1]){
                   s.append(items[1]).append(": ").append(item.quantity).append(" ").append(item.quantityUnit).append("\n")
               }
               if(checkedItems[2]){
                   s.append(items[2]).append(": ").append(item.amount).append("\n")
               }
               if(checkedItems[3]){
                   s.append(items[3]).append(": ").append(item.itemDescription).append("\n")

               }
               s.append("------------  END --------------------").append("\n")
           }
           if(checkedItems[4]){
           s.append(items[4]).append(" ").append(final_totalItems).append("\n")
           }
           if(checkedItems[5]){
               s.append(items[5]).append(" ").append(final_totalAmount).append("\n")
           }

           val intent=Intent().apply {
               action=Intent.ACTION_SEND
               type="text/plain"
               putExtra(Intent.EXTRA_TEXT,s.toString())
           }
           startActivity(Intent.createChooser(intent, "choose the app to share your list"))




       })
       builder.setNegativeButton("CANCEL", null)
       builder.show()
   }





    // setting the quantity and amount of list
    fun setQuantityAmount(item: List<Item>){
        var totalAmount=0.0
        for(items in item){

            if(items.quantityUnit=="")
            totalAmount+=items.quantity*items.amount
            else{
                totalAmount+=items.amount
            }
        }
        binding.totalItems.text=item.size.toString()
        binding.rate.text=totalAmount.toString()
        final_totalAmount=totalAmount
        final_totalItems=item.size

    }
    fun setClickHandlerForItemRecyclerView(){
        adapter.seTitemAdapterClickHandler(object :ItemAdapterClickHandler{
            override fun info(item: Item) {
                showBottomSheetDialogToShowItemInList(item)

            }

        })
    }


    // bottom sheet dialog to show the item

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
        showBottomSheetDialogToDeleteItem(item.itemId)
        bottomSheetDialog.cancel()
    }


    // setting the content of views

    itemName?.text=item.itemName
    quantity?.text= item.quantity.toString()
    quantityUnit?.text=item.quantityUnit
    rate?.text=item.amount.toString()
    description?.text=item.itemDescription
    bottomSheetDialog.show()

}

// bottom sheet dialog to Delete item list

 fun showBottomSheetDialogToDeleteItem(id: Long){
     val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(binding.root.context, R.style.DialogStyle)
     bottomSheetDialog.setContentView(R.layout.bottomdialogfordelete)
     bottomSheetDialog.setCanceledOnTouchOutside(true)
     val ok: Button? =bottomSheetDialog.findViewById(R.id.ok)
     ok?.setOnClickListener{

         viewModel.DeleteItem(id)

         bottomSheetDialog.cancel()
     }

     bottomSheetDialog.show()
 }


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
            R.array.weightUnit, R.layout.spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner?.adapter=adapter

    // unit of item

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
        var unit=spinner?.selectedItem.toString()


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