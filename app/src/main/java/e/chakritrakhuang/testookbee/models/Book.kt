package e.chakritrakhuang.testookbee.models

import android.text.TextUtils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList

class Book : Serializable {

    var openLibraryId : String? = null
        private set
    var author : String? = null
        private set
    var title : String? = null
        private set

    // Get medium sized book cover from covers API
    val coverUrl : String
        get() = "http://covers.openlibrary.org/b/olid/$openLibraryId-M.jpg?default=false"

    // Get large sized book cover from covers API
    val largeCoverUrl : String
        get() = "http://covers.openlibrary.org/b/olid/$openLibraryId-L.jpg?default=false"

    companion object {

        // Returns a Book given the expected JSON
        private fun fromJson(jsonObject : JSONObject) : Book? {
            val book = Book()
            try {

                // Deserialize json into object fields
                // Check if a cover edition is available
                if (jsonObject.has("cover_edition_key")) {
                    book.openLibraryId = jsonObject.getString("cover_edition_key")
                } else if (jsonObject.has("edition_key")) {
                    val ids = jsonObject.getJSONArray("edition_key")
                    book.openLibraryId = ids.getString(0)
                }
                book.title = if (jsonObject.has("title_suggest")) jsonObject.getString("title_suggest") else ""
                book.author = getAuthor(jsonObject)
            } catch (e : JSONException) {
                e.printStackTrace()
                return null
            }

            // Return new object
            return book
        }

        // Return comma separated author list when there is more than one author
        private fun getAuthor(jsonObject : JSONObject) : String {
            try {
                val authors = jsonObject.getJSONArray("author_name")
                val numAuthors = authors.length()
                val authorStrings = arrayOfNulls<String>(numAuthors)
                for (i in 0 until numAuthors) {
                    authorStrings[i] = authors.getString(i)
                }
                return TextUtils.join(", " , authorStrings)
            } catch (e : JSONException) {
                return ""
            }
        }

        // Decodes array of book json results into business model objects
        fun fromJson(jsonArray : JSONArray) : ArrayList<Book> {
            val books = ArrayList<Book>(jsonArray.length())

            // Process each result in json array, decode and convert to business
            // object
            for (i in 0 until jsonArray.length()) {
                val bookJson : JSONObject
                try {
                    bookJson = jsonArray.getJSONObject(i)
                } catch (e : Exception) {
                    e.printStackTrace()
                    continue
                }

                val book = Book.fromJson(bookJson)
                if (book != null) {
                    books.add(book)
                }
            }
            return books
        }
    }
}