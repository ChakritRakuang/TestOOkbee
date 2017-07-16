package e.chakritrakhuang.testookbee.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso

import java.util.ArrayList

import e.chakritrakhuang.testookbee.R
import e.chakritrakhuang.testookbee.models.Book

@Suppress("NAME_SHADOWING")
class BookAdapter(context : Context , aBooks : ArrayList<Book>) : ArrayAdapter<Book>(context , 0 , aBooks) {

    // View lookup cache
    private class ViewHolder {
        internal var ivCover : ImageView? = null
        internal var tvTitle : TextView? = null
        internal var tvAuthor : TextView? = null
    }

    // Translates a particular `Book` given a position
    // into a relevant row within an AdapterView
    override fun getView(position : Int , convertView : View? , parent : ViewGroup) : View {
        var convertView = convertView

        // Get the data item for this position
        val book = getItem(position)

        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder : ViewHolder // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_book , parent , false)
            viewHolder.ivCover = convertView !!.findViewById(R.id.ivBookCover)
            viewHolder.tvTitle = convertView.findViewById(R.id.tvTitle)
            viewHolder.tvAuthor = convertView.findViewById(R.id.tvAuthor)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // Populate the data into the template view using the data object
        viewHolder.tvTitle !!.text = book?.title
        viewHolder.tvAuthor !!.text = book?.author
        Picasso.with(context).load(Uri.parse(book?.coverUrl)).error(R.drawable.ic_nocover).into(viewHolder.ivCover)

        // Return the completed view to render on screen
        return convertView
    }
}