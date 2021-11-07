package com.vishal.list_creater_app.Main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController


import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*


import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabase
import com.vishal.list_creater_app.R
import com.vishal.list_creater_app.util.ItemListInfo
import com.vishal.list_creater_app.util.ReadAndWriteFile


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawer:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {

        }
         drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val application = requireNotNull(this).application
        val datasource= ListDatabase.getInstance(application).sleepDatabaseDao
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.navigation)
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.dashboard, R.id.itemFragment, R.id.listFragment, R.id.recievedFragement), drawer)
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navigationView.setupWithNavController(navHostFragment.navController)
        if(intent!=null && intent.action== Intent.ACTION_VIEW) {

          val readAndWriteFile=  ReadAndWriteFile(object : ItemListInfo {
              override fun getItemList(itemList: ItemList) {

                  val bundle = Bundle()
                  bundle.putLong("id", itemList.listId)
                  bundle.putString("listname", itemList.listName)
                  graph.startDestination = R.id.itemFragment
                  navHostFragment.navController.setGraph(graph, bundle)
              }

          })

                readAndWriteFile.read(intent.data ?: return, this, datasource)


        }
        else{
            graph.startDestination= R.id.dashboard
            navHostFragment.navController.setGraph(graph)

        }


    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        //return NavigationUI.navigateUp(navController,drawer)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.share -> {
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type="text/plain"
                    putExtra(Intent.EXTRA_TEXT,"Download Our App  https://play.google.com/store/apps/details?id=com.vishal.list_creater_app")

                }
                startActivity(Intent.createChooser(shareIntent, "choose the app to share the App"))
            }
            R.id.rate -> {

                val shareIntent: Intent = Intent(Intent.ACTION_VIEW,Uri.parse( "https://play.google.com/store/apps/details?id=com.vishal.list_creater_app"))



                startActivity(shareIntent)
            }





        }
        return super.onOptionsItemSelected(item)

    }
}