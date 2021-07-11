package com.vishal.list_creater_app

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vishal.list_creater_app.Database.Item
import com.vishal.list_creater_app.Database.ItemList
import com.vishal.list_creater_app.Database.ListDatabase
import com.vishal.list_creater_app.Database.ListDatabaseDao
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListDatabaseTest {
private lateinit var listDatabase: ListDatabase
private lateinit var listDatabaseDao: ListDatabaseDao
    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        listDatabase= Room.inMemoryDatabaseBuilder(context,ListDatabase::class.java)
            .allowMainThreadQueries().build()
        listDatabaseDao=listDatabase.sleepDatabaseDao
    }

    @After
    fun tearDown() {
        listDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun getSleepDatabaseDao() {
        val itemlist1=ItemList(listName="list1")
        val itemlist2=ItemList(listName="list2")
        listDatabaseDao.insertItemList(itemlist1)
        listDatabaseDao.insertItemList(itemlist2)
        val itemlists=listDatabaseDao.getAllItemList()
        //assertEquals("list1",itemlists.get(0).listName)
        val l=itemlists.get(0).listId
        val l1=itemlists.get(1).listId
        val item1=Item(list_id = l,itemName = "vishal",quantity = 1,amount = 200,itemDescription = "how are you")
        val item2=Item(list_id = l1,itemName = "nishu",quantity = 3,amount = 100,itemDescription = "i am fine")
        listDatabaseDao.insertItem(item1)
        listDatabaseDao.insertItem(item2)
        val items=listDatabaseDao.getAllItem(l)
        val quantity1 =items.get(0).quantity
       // val quantity2 =items.get(0).quantity
        assertEquals(1,quantity1)

    }
}