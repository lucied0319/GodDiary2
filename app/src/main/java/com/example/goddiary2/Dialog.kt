package com.example.goddiary2

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Adapter
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.Month
import java.time.Year
import java.util.*
///////////////////////日付ダイアログ//////////////////////////////////////////////////

class DatePickerFragment : DialogFragment(), OnDateSetListener{

    interface OnDatePickListener{
        fun onDateSelect(year: Int,month: Int,day:Int)
    }
    private lateinit var listener: OnDatePickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(parentFragment is OnDatePickListener){
            listener = parentFragment as OnDatePickListener
        }
    }
    //DatePickerDialogインスタンスの作成
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(),this,year,month,day)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        listener.onDateSelect(p1,p2,p3)
    }

}
/////////////////////////////保存確認ダイアログ/////////////////////////////////////////////

class AlertSaveDialog : DialogFragment(){

    interface OnAlertListener{
        fun onPositiveClick()
    }

    private  lateinit var listener : OnAlertListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(parentFragment is OnAlertListener){
            listener = parentFragment as OnAlertListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity).apply {
            setTitle("保存確認")
            setMessage("この内容で保存してもいいですか？")
            setPositiveButton("OK"){dialog,which ->
                listener.onPositiveClick()
            }
            setNegativeButton("キャンセル"){dialog,which ->
            }
        }
        return builder.create()
    }
}
//////////////////////////再編集、削除確認ダイアログ///////////////////////////////////////

class AlertUpdateDeleteDialog : DialogFragment(){

    interface OnAlertListener{
        fun onDeleteClick(date: String)
        fun onUpdateClick(date: String)
    }
    private lateinit var listener: OnAlertListener

    private var date : String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when{
            context is OnAlertListener ->{
                listener = context
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(date: String) =
            AlertUpdateDeleteDialog().apply {
                arguments = Bundle().apply {
                    putString("date",date)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString("date") ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = arrayOf("再編集","削除","キャンセル")
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle("メニュー")
            setItems(items) {dialoginterface,i ->
                //再編集をタップ
                when{
                    i == 0 -> listener.onUpdateClick(date)
                    i == 1 ->{
//                        val dialog = AlertDeleteDialog()
//                        dialog.show(parentFragmentManager,"")
                        val builder = AlertDialog.Builder(activity).apply {
                            setTitle("削除確認")
                            setMessage("この日記を削除してもいいですか？")
                            setPositiveButton("OK"){dialog,which ->
                                listener.onDeleteClick(date)
                            }
                            setNegativeButton("キャンセル"){dialog,which ->
                            }
                        }
                        builder.show()
                    }
                }
            }
        }
        return builder.create()
    }
    inner class AlertDeleteDialog : DialogFragment(){

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val builder = AlertDialog.Builder(activity).apply {
                setTitle("削除確認")
                setMessage("この日記を削除してもいいですか？")
                setPositiveButton("OK"){dialog,which ->
                    listener.onDeleteClick(date)
                }
                setNegativeButton("キャンセル"){dialog,which ->
                }
            }
            return builder.create()
        }
    }
}
/////////////////////////////削除確認ダイアログ/////////////////////////////////////////////

