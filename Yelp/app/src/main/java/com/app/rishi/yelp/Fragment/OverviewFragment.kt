package com.app.rishi.yelp.Fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.app.rishi.yelp.Adapter.SliderAdapter
import com.app.rishi.yelp.Main.RestaurantDetailActivity
import com.app.rishi.yelp.Model.ReservationClass
import com.app.rishi.yelp.Model.ReservationsClass
import com.app.rishi.yelp.Model.YelpRestaurantDetail
import com.app.rishi.yelp.Model.YelpService
import com.app.rishi.yelp.R
import com.app.rishi.yelp.Util.Constant
import com.app.rishi.yelp.Util.ModelPreferencesManager
import com.app.rishi.yelp.Util.Preference
import com.google.android.gms.maps.SupportMapFragment
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import kotlinx.android.synthetic.main.fragment_restaurant_overview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class OverviewFragment : Fragment() {

    lateinit var photosAdapter: SliderAdapter
    val photos = mutableListOf<String>()
    lateinit var  activity : RestaurantDetailActivity
    lateinit var btnReserve:Button
    lateinit var mapFragment: SupportMapFragment;
    lateinit var ft:androidx.fragment.app.FragmentTransaction


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_restaurant_overview, container, false)
        btnReserve = rootView.findViewById(R.id.btnReserve) as Button
        btnReserve.setOnClickListener(View.OnClickListener {

            showDialog(activity.name)

        })
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDetail()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RestaurantDetailActivity
    }

    fun Toast.showCustomToast(message: String, activity: Activity)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.custom_toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<TextView>(R.id.toText)
        textView.text = message

        // use the application extension function
        this.apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    private fun showDialog(title: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.booking_dialog)
        val body = dialog.findViewById(R.id.txvResName) as TextView
        body.text = title
        val cancelTxv = dialog.findViewById(R.id.txvCancel) as TextView
        val submitTxv = dialog.findViewById(R.id.txvSubmit) as TextView
        val edtEmail = dialog.findViewById(R.id.edtEmail) as EditText
        val edtDate = dialog.findViewById(R.id.edtDate) as EditText
        val edtTime = dialog.findViewById(R.id.edtTime) as EditText

        var cal = Calendar.getInstance()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "MM-dd-yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                edtDate.setText(sdf.format(cal.getTime()))
            }
        }
        edtDate.setOnClickListener{

            DatePickerDialog(activity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }
        val timeSetLister = object :TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {

                cal.set(Calendar.HOUR, p1)
                cal.set(Calendar.MINUTE,p2)

                if (p1<10|| p1>17){

                    Toast(activity).showCustomToast("Time should be between 10AM and 5PM",activity)
                }else{
                    edtTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
                }


            }
        }

        edtTime.setOnClickListener {
            TimePickerDialog(activity,
                timeSetLister,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true).show()
        }

        cancelTxv.setOnClickListener {
            dialog.dismiss()
        }
        submitTxv.setOnClickListener {

            if(!isValidEmail(edtEmail.text.toString())){
                Toast(activity).showCustomToast("Invalid Email Address", activity)
            }

            val time = edtTime.text.toString()
            val date = edtDate.text.toString()
            val email = edtEmail.text.toString()
            var flg = 0
            val reservations = Preference.getInstance().getReservations(activity, Constant.RESERVATIONS)

            for (reservation in reservations){
                if (time.equals(reservation.time)&&
                    date.equals(reservation.date)&&
                    email.equals(reservation.email)&&
                    reservation.name.equals(activity.name)){
                    flg =1
                    Toast(activity).showCustomToast("Reservation duplicated", activity)
                    dialog.dismiss()
                }
            }
            if (flg ==0){
                val reservation = ReservationClass(email, date, time, activity.name)
                reservations.add(reservation)
                Preference.getInstance().putReservations(activity, Constant.RESERVATIONS,reservations)
            }

            dialog.dismiss()
        }
        dialog.show()
        var window =  dialog.getWindow() as Window
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getDetail(){
        val retrofit =
            Retrofit.Builder().baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.getDetails("Bearer ${Constant.API_KEY}", activity.id)
            .enqueue(object : Callback<YelpRestaurantDetail> {
                override fun onResponse(
                    call: Call<YelpRestaurantDetail>,
                    response: Response<YelpRestaurantDetail>
                ) {
                    Log.i("TAG", "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w("TAG", "Did not receive valid response body from Yelp API... exiting")
                        return
                    }
                    bindDetails(body)

                    activity.resUrl = body.url

                    photos.addAll(body.photos.drop(1))
                    photosAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpRestaurantDetail>, t: Throwable) {
                    Log.i("TAG", "onFailure $t")
                }
            })

    }

    fun bindDetails(body: YelpRestaurantDetail) {

        photosAdapter = SliderAdapter(requireContext(), photos)
        imageSlider.setSliderAdapter(photosAdapter)

        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        imageSlider.indicatorSelectedColor = Color.WHITE
        imageSlider.indicatorUnselectedColor = Color.GRAY
        imageSlider.scrollTimeInSec = 4
        imageSlider.startAutoCycle()

        tvName.text = body.name

        ratingBar.rating = body.rating.toFloat()
        tvNumReviews.text = "${body.numReviews} Reviews"
        tvPrice.text = body.price
        tvCategory.text = body.categories.joinToString { c -> c.title }
        tvAddress.text = body.location.address
        tvPhone.text = "Phone: ${body.phone}"
        tvLink.setOnClickListener{
            val url = Intent(android.content.Intent.ACTION_VIEW)
            if (!body.url.isEmpty()){
                url.data = Uri.parse(body.url)
                startActivity(url)
            }
        }
    }
}