package com.vishal .list_creater_app.Items

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishal.list_creater_app.Database.Item
import com.vishal.list_creater_app.Database.ListDatabase
import com.vishal.list_creater_app.R
import com.vishal.list_creater_app.databinding.ItemFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout

class ItemFragment : Fragment() {




    private lateinit var binding:ItemFragmentBinding
    lateinit var viewModel: ItemViewModel
    lateinit var adapter: ItemAdapter
    var final_totalItems=0
    var final_totalAmount=0.0
    private  val SPEECH_REQUEST_CODE_NAME = 0
    private  val SPEECH_REQUEST_CODE_DESCRIPTION = 1
    var itemName:EditText?=null
    var description:EditText?=null
    var bottomSheetDialog: BottomSheetDialog?=null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding= ItemFragmentBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        val id= ItemFragmentArgs.fromBundle(arguments).id
        val name=ItemFragmentArgs.fromBundle(arguments).listname
        val itemViewModelFactory=ItemViewModelFactory(datasource, id)
        viewModel = ViewModelProvider(this, itemViewModelFactory).get(ItemViewModel::class.java)
        adapter= ItemAdapter()
        binding.item.adapter=adapter
        val manager = LinearLayoutManager(activity)
        binding.item.layoutManager = manager
        viewModel._item.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.submitList(it)
                setQuantityAmount(it)
                if (it.size == 0) {
                    binding.emptyitem.visibility = View.VISIBLE
                } else {
                    binding.emptyitem.visibility = View.GONE
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
        binding.amountLayout.setOnClickListener {
            showCustomDialog()
        }
        setClickHandlerForItemRecyclerView()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.title= "$name"
        (requireActivity() as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(getResources().getColor(R.color.deep_purple_300)))
        return binding.root
    }


   // alert dialog to share a list
   fun share(itemList: List<Item>){
       val items= arrayOf(resources.getString(R.string.item_name), resources.getString(R.string.quantity), resources.getString(R.string.rate), resources.getString(R.string.description), resources.getString(R.string.total_Items), resources.getString(R.string.total_amount))
       val checkedItems= booleanArrayOf(true, false, false, false, false, false)
       val builder = AlertDialog.Builder(requireContext())
       builder.setTitle(resources.getString(R.string.select_the_Item_Property_you_want_to_Share))
       builder.setMultiChoiceItems(items, checkedItems, OnMultiChoiceClickListener
       { dialogInterface, i, b ->
           checkedItems[i] = b
       })
       builder.setPositiveButton(resources.getString(R.string.ok), DialogInterface.OnClickListener
       { dialogInterface, i ->

           val s = StringBuilder()

           for (item in itemList) {
               if (checkedItems[0]) {
                   s.append(item.itemName).append("\n\n")
               }
               if (checkedItems[1]) {
                   s.append(items[1]).append(": ").append(item.quantity).append(" ").append(item.quantityUnit).append("\n")
               }
               if (checkedItems[2]) {
                   s.append(items[2]).append(": ").append(item.amount).append("\n")
               }
               if (checkedItems[3]) {
                   s.append(items[3]).append(": ").append(item.itemDescription).append("\n")

               }
               s.append("------------  END --------------------").append("\n")
           }
           if (checkedItems[4]) {
               s.append(items[4]).append(" ").append(final_totalItems).append("\n")
           }
           if (checkedItems[5]) {
               s.append(items[5]).append(" ").append(final_totalAmount).append("\n")
           }
           s.append(resources.getString(R.string.download_Our_App_from_Play_Store)).append("\n").append("https://play.google.com/store/apps/details?id=com.vishal.list_creater_app \n")
           val intent = Intent().apply {
               action = Intent.ACTION_SEND
               type = "text/plain"
               putExtra(Intent.EXTRA_TEXT, s.toString())
           }
           startActivity(Intent.createChooser(intent, resources.getString(R.string.choose_the_app_to_share_your_list)))


       })
       builder.setNegativeButton(resources.getString(R.string.cancel), null)
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
        adapter.seTitemAdapterClickHandler(object : ItemAdapterClickHandler {
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
        showBottomSheetDialogToAddOrEditItemInList(item.list_id, 1, item)
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
private fun showBottomSheetDialogToAddOrEditItemInList(id: Long, flag: Int = 0, item: Item? = null) {
     bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    bottomSheetDialog?.setContentView(R.layout.additem)
    bottomSheetDialog?.setCanceledOnTouchOutside(true)
    val add: Button? =bottomSheetDialog?.findViewById(R.id.add)
    // editext for quantity
    val quantity=bottomSheetDialog?.findViewById<EditText>(R.id.quantity)
    // editext for amount
    val amount=bottomSheetDialog?.findViewById<EditText>(R.id.amount)
    // editext for itemName
    itemName=bottomSheetDialog?.findViewById<EditText>(R.id.itemName)
    // editext for description
     description=bottomSheetDialog?.findViewById<EditText>(R.id.description)
    // spinner
    val spinner=bottomSheetDialog?.findViewById<Spinner>(R.id.weightspinner)
    val adapter=ArrayAdapter.createFromResource(requireContext(),
            R.array.weightUnit, R.layout.spinner_item)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner?.adapter=adapter

    val outlinedTextFieldName: TextInputLayout? =bottomSheetDialog?.findViewById<TextInputLayout>(R.id.outlinedTextFieldName)
    val outlinedTextFieldDescription: TextInputLayout? =bottomSheetDialog?.findViewById<TextInputLayout>(R.id.outlinedTextFieldDescription)
    outlinedTextFieldName?.setEndIconOnClickListener{

        displaySpeechRecognizer(SPEECH_REQUEST_CODE_NAME)

    }
    outlinedTextFieldDescription?.setEndIconOnClickListener{

        displaySpeechRecognizer(SPEECH_REQUEST_CODE_DESCRIPTION)

    }

    // unit of item

    // checking whether we have to edit or add
    if(flag==1 && item!=null){
        add?.setText(getString(R.string.update))
        // setting the text for respective field
        itemName?.setText(item.itemName)
        quantity?.setText(item.quantity.toString())
        amount?.setText(item.amount.toString())
        description?.setText(item.itemDescription)
        spinner?.setSelection(adapter.getPosition(item.quantityUnit))
    }
    else {
        add?.setText(getString(R.string.add))
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
        if(unit==getString(R.string.None)){
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
        bottomSheetDialog?.cancel()
    }

    bottomSheetDialog?.show()

}




    private fun displaySpeechRecognizer(request_code: Int) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        }
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, request_code)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                    data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                        results?.get(0)
                    }
            // Do something with spokenText.


            when(requestCode){

                SPEECH_REQUEST_CODE_NAME -> {
                    if (bottomSheetDialog != null) {
                        if (bottomSheetDialog?.isShowing == true) {
                            itemName?.setText(spokenText)
                        }
                    }
                }
                SPEECH_REQUEST_CODE_DESCRIPTION -> {
                    if (bottomSheetDialog != null) {
                        if (bottomSheetDialog?.isShowing == true) {
                            description?.setText(spokenText)
                        }
                    }
                }

            }



        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.amount_dialog)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.show()
        dialog.window!!.attributes = lp
    }


}