package com.kierman.lufanalezaco.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kierman.lufanalezaco.R

class ResultsAdapter(private val results: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return results.size
    }

    override fun getItem(position: Int): Any {
        return results[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(
            R.layout.result_list_item,
            parent,
            false
        )

        val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
        resultTextView.text = results[position]

        // Opcjonalnie: Wyróżnij najlepszy wynik
        // Tutaj możesz dodać logikę do wyróżnienia najlepszego wyniku.

        return view
    }
}
