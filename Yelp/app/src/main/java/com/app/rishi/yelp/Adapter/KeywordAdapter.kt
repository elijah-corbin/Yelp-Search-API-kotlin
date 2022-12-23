package com.app.rishi.yelp.Adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.app.rishi.yelp.Model.YelpTerm
import com.app.rishi.yelp.R

class KeywordAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    terms: List<YelpTerm> ) :
    ArrayAdapter<YelpTerm>(mContext, mLayoutResourceId, terms) {

        private val term: MutableList<YelpTerm> = ArrayList(terms)
        private var allterms: List<YelpTerm> = ArrayList(terms)

        override fun getCount(): Int {
            return term.size
        }
        override fun getItem(position: Int): YelpTerm {
            return term[position]
        }
//        override fun getItemId(position: Int): Long {
//            return term[position].text.toLong()
//        }
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun convertResultToString(resultValue: Any) :String {
                    return (resultValue as YelpTerm).text
                }
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filterResults = FilterResults()
                    if (constraint != null) {
                        val termSuggestion: MutableList<YelpTerm> = ArrayList()
                        for (term in allterms) {
                            if (term.text.toLowerCase().startsWith(constraint.toString().toLowerCase())
                            ) {
                                termSuggestion.add(term)
                            }
                        }
                        filterResults.values = termSuggestion
                        filterResults.count = termSuggestion.size
                    }
                    return filterResults
                }
                override fun publishResults(
                    constraint: CharSequence?,
                    results: FilterResults
                ) {
                    term.clear()
                    if (results.count > 0) {
                        for (result in results.values as List<*>) {
                            if (result is YelpTerm) {
                                term.add(result)
                            }
                        }
                        notifyDataSetChanged()
                    } else if (constraint == null) {
                        term.addAll(allterms)
                        notifyDataSetInvalidated()
                    }
                }
            }
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                val inflater = (mContext as Activity).layoutInflater
                convertView = inflater.inflate(mLayoutResourceId, parent, false)
            }
            try {
                val term: YelpTerm = getItem(position)
                val cityAutoCompleteView = convertView!!.findViewById<View>(R.id.text2) as TextView
                cityAutoCompleteView.text = term.text
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return convertView!!
        }
}