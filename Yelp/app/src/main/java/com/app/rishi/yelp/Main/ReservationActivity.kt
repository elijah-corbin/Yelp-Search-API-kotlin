package com.app.rishi.yelp.Main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rishi.yelp.Adapter.ReservationAdapter
import com.app.rishi.yelp.Adapter.SwipeToDeleteCallback
import com.app.rishi.yelp.Model.ReservationClass
import com.app.rishi.yelp.R
import com.app.rishi.yelp.Util.Constant
import com.app.rishi.yelp.Util.Preference
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_reservation.*


class ReservationActivity : AppCompatActivity() {

    private lateinit var adapter : ReservationAdapter
    private lateinit var rxReserve:RecyclerView

    private lateinit var reservations : MutableList<ReservationClass>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        reservations = mutableListOf<ReservationClass>()
        rxReserve = findViewById(R.id.rvReservations) as RecyclerView


        val reservations = Preference.getInstance().getReservations(this@ReservationActivity, Constant.RESERVATIONS)

        if (reservations.size >0){

            rxReserve.layoutManager = LinearLayoutManager(this)
            adapter = ReservationAdapter(this, reservations)

            rxReserve.adapter = adapter
            rxReserve.addItemDecoration(
                DividerItemDecoration(
                    rxReserve.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            val swipeHandler = object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(rxReserve)

        }else{
            txNoFound.visibility = View.VISIBLE
        }

    }


}