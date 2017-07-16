package e.chakritrakhuang.testookbee.net

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class BookClient {

    private val client : AsyncHttpClient = AsyncHttpClient()

    private fun getApiUrl(relativeUrl : String) : String {
        return API_BASE_URL + relativeUrl
    }

    // Method for accessing the search API
    fun getBooks(query : String , handler : JsonHttpResponseHandler) {
        try {
            val url = getApiUrl("search.json?q=")
            client.get(url + URLEncoder.encode(query , "utf-8") , handler)
        } catch (e : UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // Method for accessing books API to get publisher and no. of pages in a book.
    fun getExtraBookDetails(openLibraryId : String , handler : JsonHttpResponseHandler) {
        val url = getApiUrl("books/")
        client.get(url + openLibraryId + ".json" , handler)
    }

    companion object {

        private val API_BASE_URL = " http://openlibrary.org/"
    }
}