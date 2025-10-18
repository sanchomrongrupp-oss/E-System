package com.example.e_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

class Attendance : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        // 1️⃣ Find views in the fragment's layout
        val spinner = view.findViewById<Spinner>(R.id.dropdown_spinner)
        val selectedText = view.findViewById<TextView>(R.id.card_title)

        // 2️⃣ Data for dropdown
        val items = listOf("Mobile System and App", "OOAD and Prog.", "S.E and IT PM", "Windows Server Admin", "M.I.S",)

        // 3️⃣ Create Adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 4️⃣ Handle item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedItem = items[position]
                selectedText.text = "$selectedItem"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedText.text = "None"
            }
        }

        return view
    }
}
