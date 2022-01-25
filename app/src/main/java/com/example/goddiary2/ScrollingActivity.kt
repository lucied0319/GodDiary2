package com.example.goddiary2

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.goddiary2.databinding.ActivityScrollingBinding
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity(),AlertUpdateDeleteDialog.OnAlertListener {
    
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //realmとviewBindingを初期化
        realm = Realm.getDefaultInstance()
        binding = ActivityScrollingBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = title

        //最初の起動でConfigデータがあるか確認、なければ作成
        var i: Int = 0
        realm.executeTransaction{
            var config = realm.where<Config>().equalTo("id",i).findFirst()
            if(config == null){
                config = realm.createObject<Config>(i)
            }
            if(config.byteMainImage == null){
                binding.imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        }

        //viewPager2のページを作成
        var arrayFragment = ArrayList<Fragment>()
        arrayFragment.add(DiaryListFragment())
        arrayFragment.add(MakeDiaryFragment())
        arrayFragment.add(MakeDiaryFragment())

        var viewPager2Adapter = ViewPager2Adapter(this,arrayFragment)
        binding.viewPager2.adapter = viewPager2Adapter

        //タブレイアウトとviewPager2を関連付け
        val array = arrayOf("日記一覧","日記作成","タグ作成")
        TabLayoutMediator(binding.tabLayout,binding.viewPager2,
           {tab,position -> tab.text = array[position]}).attach()



    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    //日記削除の処理
    override fun onDeleteClick(date :String) {

        realm.executeTransaction{
            realm.where<Diary>().equalTo("date",date).findFirst()?.deleteFromRealm()
        }
        Snackbar.make(binding.root,"削除しました",Snackbar.LENGTH_SHORT).show()
    }
    //日記再編集の処理
    override fun onUpdateClick(date: String) {
        val listFragment = ArrayList<Fragment>()
        listFragment.add(DiaryListFragment())
        listFragment.add(MakeDiaryFragment.newInstance(date))
        listFragment.add(MakeDiaryFragment.newInstance(date))

        val viewPager2Adapter = ViewPager2Adapter(this,listFragment)
        binding.viewPager2.adapter = viewPager2Adapter
        viewPager2.setCurrentItem(1)

    }
}