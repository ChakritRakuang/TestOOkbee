@file:Suppress("DEPRECATION")

package e.chakritrakhuang.testookbee

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.support.v7.app.ActionBarActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView

import com.loopj.android.http.JsonHttpResponseHandler
import com.squareup.picasso.Picasso

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import e.chakritrakhuang.testookbee.activity.BookListActivity
import e.chakritrakhuang.testookbee.models.Book
import e.chakritrakhuang.testookbee.net.BookClient

@Suppress("DEPRECATION" , "VARIABLE_WITH_REDUNDANT_INITIALIZER")
class MainActivity : ActionBarActivity() {

    private var ivBookCover : ImageView? = null
    private var tvTitle : TextView? = null
    private var tvAuthor : TextView? = null
    private var tvPublisher : TextView? = null
    private var tvPageCount : TextView? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch views
        ivBookCover = findViewById(R.id.ivBookCover)
        tvTitle = findViewById(R.id.tvTitle)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvPublisher = findViewById(R.id.tvPublisher)
        tvPageCount = findViewById(R.id.tvPageCount)

        // Use the book to populate the data into our views
        val book = intent.getSerializableExtra(BookListActivity.BOOK_DETAIL_KEY) as Book
        loadBook(book)
    }

    // Populate data for the book
    private fun loadBook(book : Book) {

        //change activity title
        this.title = book.title

        // Populate data
        Picasso.with(this).load(Uri.parse(book.largeCoverUrl)).error(R.drawable.ic_nocover).into(ivBookCover)
        tvTitle !!.text = book.title
        tvAuthor !!.text = book.author

        // fetch extra book data from books API
        val client = BookClient()
        book.openLibraryId?.let {
            client.getExtraBookDetails(it , object : JsonHttpResponseHandler() {

            @SuppressLint("SetTextI18n")
            fun onSuccess(response : JSONObject) {
                try {
                    if (response.has("publishers")) {
                        // display comma separated list of publishers
                        val publisher = response.getJSONArray("publishers")
                        val numPublishers = publisher.length()
                        val publishers = arrayOfNulls<String>(numPublishers)
                        for (i in 0 until numPublishers) {
                            publishers[i] = publisher.getString(i)
                        }
                        tvPublisher !!.text = TextUtils.join(", " , publishers)
                    }
                    if (response.has("number_of_pages")) {
                        tvPageCount !!.text = Integer.toString(response.getInt("number_of_pages")) + " pages"
                    }
                } catch (e : JSONException) {
                    e.printStackTrace()
                }

            }
        })
        }
    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_book_detail , menu)
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_share) {
            setShareIntent()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setShareIntent() {
        val ivImage = findViewById<ImageView>(R.id.ivBookCover)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)

        // Get access to the URI for the bitmap
        val bmpUri = getLocalBitmapUri(ivImage)

        // Construct a ShareIntent with link to image
        val shareIntent = Intent()

        // Construct a ShareIntent with link to image
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "*/*"
        shareIntent.putExtra(Intent.EXTRA_TEXT , tvTitle.text as String)
        shareIntent.putExtra(Intent.EXTRA_STREAM , bmpUri)

        // Launch share menu
        startActivity(Intent.createChooser(shareIntent , "Share Image"))

    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    private fun getLocalBitmapUri(imageView : ImageView) : Uri? {

        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp : Bitmap? = null
        if (drawable is BitmapDrawable) {
            bmp = (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }

        // Store image to default external storage directory
        var bmpUri : Uri? = null
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , "share_image_" + System.currentTimeMillis() + ".png")
            file.parentFile.mkdirs()
            val out = FileOutputStream(file)
            bmp !!.compress(Bitmap.CompressFormat.PNG , 90 , out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e : IOException) {
            e.printStackTrace()
        }

        return bmpUri
    }
}