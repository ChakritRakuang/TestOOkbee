package e.chakritrakhuang.testookbee.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar

import com.loopj.android.http.JsonHttpResponseHandler

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import e.chakritrakhuang.testookbee.MainActivity
import e.chakritrakhuang.testookbee.R
import e.chakritrakhuang.testookbee.adapters.BookAdapter
import e.chakritrakhuang.testookbee.models.Book
import e.chakritrakhuang.testookbee.net.BookClient

@Suppress("DEPRECATION")
@SuppressLint("Registered")
class BookListActivity : AppCompatActivity() {

    private var lvBooks : ListView? = null
    private var bookAdapter : BookAdapter? = null
    private var progress : ProgressBar? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        lvBooks = findViewById<View>(R.id.lvBooks) as ListView
        val aBooks = ArrayList<Book>()

        // initialize the adapter
        bookAdapter = BookAdapter(this , aBooks)

        // attach the adapter to the ListView
        lvBooks !!.adapter = bookAdapter
        progress = findViewById<View>(R.id.progress) as ProgressBar
        setupBookSelectedListener()
    }

    private fun setupBookSelectedListener() {
        lvBooks !!.onItemClickListener = AdapterView.OnItemClickListener { _ , _ , position , _ ->
            // Launch the detail view passing book as an extra
            val intent = Intent(this@BookListActivity , MainActivity::class.java)
            intent.putExtra(BOOK_DETAIL_KEY , bookAdapter !!.getItem(position))
            startActivity(intent)
        }
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private fun fetchBooks(query : String) {

        // Show progress bar before making network request
        progress !!.visibility = ProgressBar.VISIBLE
        val client = BookClient()
        client.getBooks(query , object : JsonHttpResponseHandler() {

            fun onSuccess(response : JSONObject?) {
                try {

                    // hide progress bar
                    progress !!.visibility = ProgressBar.GONE
                    val docs : JSONArray
                    if (response != null) {

                        // Get the docs json array
                        docs = response.getJSONArray("docs")

                        // Parse json array into array of model objects
                        val books = Book.fromJson(docs)

                        // Remove all books from the adapter
                        bookAdapter !!.clear()

                        // Load model objects into the adapter
                        for (book in books) {
                            bookAdapter !!.add(book) // add book through the adapter
                        }
                        bookAdapter !!.notifyDataSetChanged()
                    }
                } catch (e : JSONException) {

                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace()
                }
            }

            fun onFailure() {
                progress !!.visibility = ProgressBar.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_book_list , menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query : String) : Boolean {

                // Fetch the data remotely
                fetchBooks(query)

                // Reset SearchView
                searchView.clearFocus()
                searchView.setQuery("" , false)
                searchView.isIconified = true
                searchItem.collapseActionView()

                // Set activity title to search query
                this@BookListActivity.title = query
                return true
            }

            override fun onQueryTextChange(s : String) : Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {

        val BOOK_DETAIL_KEY = "book"
    }
}