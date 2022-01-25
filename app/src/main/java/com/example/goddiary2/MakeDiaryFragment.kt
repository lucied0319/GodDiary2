package com.example.goddiary2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.goddiary2.databinding.FragmentMakeDiaryBinding
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [MakeDiaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MakeDiaryFragment : Fragment() ,DatePickerFragment.OnDatePickListener , AlertSaveDialog.OnAlertListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private  var _binding : FragmentMakeDiaryBinding? = null
    private  val binding get() = _binding!!
    private  lateinit var realm : Realm

    private var year : Int = 0
    private  var month : Int = 0
    private var weather : Int = 0
    private var tag = 0
    private var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)


        }
        //リアムデータベース取得
        realm = Realm.getDefaultInstance()

        //日付ボタンに今日の日付を取得
        if (arguments != null){
            date = arguments?.getString("date") ?: ""
        }else {
            val calendar = Calendar.getInstance()
            //日付ボタンに今日の日付をセット
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH ) +1
            date = String.format("%d/%02d/%02d",year, month,calendar.get(Calendar.DATE))
        }

        //今日の日付を取得するラムダ関数
        val makedate  ={
            val calendar = Calendar.getInstance()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH ) +1
            String.format("%d/%02d/%02d",year, month,calendar.get(Calendar.DATE))
        }
//        val makeDate  ={
//            val calendar = Calendar.getInstance()
//            year = calendar.get(Calendar.YEAR)
//            month = calendar.get(Calendar.MONTH ) +1
//            String.format("%d/%02d/%02d",year, month,calendar.get(Calendar.DATE))
//        }
        //Bundleにリストデータからの日付があれば取り出す、なければ今日の日付
        date = arguments?.getString("date") ?: makedate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // viewbindingを取得
        _binding = FragmentMakeDiaryBinding.inflate(inflater,container,false)

        //日付をボタンに表示
        binding.buttonDate.text = date//android.text.format.DateFormat.format("yyyy/MM/dd",calendar)
        //日付ボタンのリスナーをセット
        binding.buttonDate.setOnClickListener{
            val dialog = DatePickerFragment()
            dialog.show(childFragmentManager,"日付")
        }
        //保存ボタンのリスナーをセット
        binding.buttonSave.setOnClickListener{
            val dialog = AlertSaveDialog()
            dialog.show(childFragmentManager,"保存")
        }
        //天気スピナーにリスナーをセット
        binding.spinnerWeather.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                weather = p2
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        //Configデータからタグスピナーを作成
        var i = 0
        var config = realm.where<Config>().equalTo("id",i).findFirst()
        if(config !=null) {
            var arrayTag = config.tag.split(",")
            var adapter = ArrayAdapter<String>(requireContext(),R.layout.spinner4,arrayTag)
            adapter.setDropDownViewResource( R.layout.spinner_dropdown)
            binding.spinnerTag.adapter = adapter
        }
        //タグスピナーにリスナーをセット
        binding.spinnerTag.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tag = p2
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        showDiary()
        return binding.root
    }
    fun showDiary(){
        var diary = realm.where<Diary>().equalTo("date",date).findFirst()
        diary?.run{
            binding.editTitle.setText(title)
            binding.editBodyText.setText(bodyText)
            binding.spinnerWeather.setSelection(weather)
            binding.spinnerTag.setSelection(tag)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(date: String) =
            MakeDiaryFragment().apply {
                arguments = Bundle().apply {
                    putString("date", date)

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //DatePickerFragmentのインターフェイスと関連付け
    override fun onDateSelect(year: Int, month: Int, day: Int) {
        date = "%d/%02d/%02d".format(year,month,day)
        binding.buttonDate.text = date
        showDiary()
    }
    //AlertSaveDialogのインターフェイスと関連付け
    override fun onPositiveClick() {
        //日記の保存
        realm.executeTransaction{
            var diary = realm.where<Diary>().equalTo("date",date).findFirst()
            if(diary == null){
                diary = realm.createObject(date)
            }
                diary.title = binding.editTitle.text.toString()
                diary.bodyText = binding.editBodyText.text.toString()
                diary.year = year
                diary.month = month
                diary.weather = weather
                diary.tag = tag


        }
        Snackbar.make(binding.root,"保存しました",Snackbar.LENGTH_SHORT).show()
    }
}