package com.example.goddiary2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goddiary2.databinding.FragmentDiaryListBinding
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import java.util.*


class DiaryListFragment : Fragment() {

    private lateinit var realm: Realm

    private var _binding : FragmentDiaryListBinding? = null
    private  val binding get() = _binding!!


    var year : Int = 0
    var month : Int = 0

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        realm = Realm.getDefaultInstance()

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)+ 1


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiaryListBinding.inflate(inflater,container,false)

        //スピナー設定
        binding.spinnerYear.setSelection(year - 2020)
        binding.spinnerMonth.setSelection(month - 1)
        var listTag : List<String> = listOf("全て")
        var i = 0
        var config = realm.where<Config>().equalTo("id",i).findFirst()
        if(config !=null) {
            listTag = config.tag.split(",")
            var adapter = ArrayAdapter<String>(requireContext(),R.layout.spinner4,listTag)
            adapter.setDropDownViewResource( R.layout.spinner_dropdown)
            binding.spinnerTag.adapter = adapter
        }
        //////////////////////////リサイクルビュー/////////////////////////////
        //リサイクルアダプター作成
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = linearLayoutManager
        val results = realm.where<Diary>().equalTo("year",year).equalTo("month",month)
            .findAll().sort("date",Sort.DESCENDING)
        val adapter = DiaryRealmAdapter(results,listTag,true)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}