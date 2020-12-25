package com.example.hospital_managment_system.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.models.UserDoctor
import com.squareup.picasso.Picasso

import kotlin.collections.ArrayList


class DoctorCardAdapter(private val context: Context, private val dataSource: ArrayList<UserDoctor>) : BaseAdapter() ,
    Filterable {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mListAppInfoFiltered: ArrayList<UserDoctor> = dataSource
    var tempNameVersionList = ArrayList(mListAppInfoFiltered)
    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }



//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        // Get view for row item
//        val rowView = inflater.inflate(R.layout.doctor_view_cell, parent, false)
//
//        // Get title element
//        val name = rowView.findViewById(R.id.patient_doctor_cell_name) as TextView
//
//        // Get subtitle element
//        val hospital = rowView.findViewById(R.id.patient_doctor_cell_hospital) as TextView
//
//
////        // Get thumbnail element
////        val thumbnailImageView = rowView.findViewById(R.id.recipe_list_thumbnail) as ImageView
//
//
//        // 1
//        val users = getItem(position) as UserDoctor
//
//// 2
//
//        print("name:" + users.name)
//        name.text = users.name
//        print("hospital:" + users.hospital)
//        hospital.text = users.hospital
//
//
//// 3 img
////        Picasso.with(context).load(recipe.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)
//
//
//
//        return rowView
//    }


    //Create A class To hold over View like we taken in list_row.xml
    class ViewHolder {

        var mImageView: ImageView? = null
        var mHeader: TextView? = null
        var mSubHeader: TextView? = null
    }

    //Auto Generated Method
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myview = convertView
        val holder: ViewHolder



        if (convertView == null) {

            //If Over View is Null than we Inflater view using Layout Inflater
            val mInflater = (context as Activity).layoutInflater

            //Inflating our list_row.
            myview = mInflater!!.inflate(R.layout.doctor_view_cell, parent, false)

            //Create Object of ViewHolder Class and set our View to it
            holder = ViewHolder()

            //Find view By Id For all our Widget taken in list_row.

            /*Here !! are use for non-null asserted two prevent From null.
             you can also use Only Safe (?.)

            */
            holder.mImageView = myview!!.findViewById<ImageView>(R.id.patient_doctor_cell_image) as ImageView
            holder.mHeader = myview!!.findViewById<TextView>(R.id.patient_doctor_cell_name) as TextView
            holder.mSubHeader = myview!!.findViewById<TextView>(R.id.patient_doctor_cell_hospital) as TextView

            //Set A Tag to Identify our view.
            myview.setTag(holder)
        } else {

            //If Ouer View in not Null than Just get View using Tag and pass to holder Object.
            holder = myview!!.getTag() as ViewHolder
        }

        //Getting HasaMap At Perticular Position
        val map = mListAppInfoFiltered.get(position)

        //Setting Image to ImageView by position
//        holder.mImageView!!.setImageResource(image[position])

        //Setting name to TextView it's Key from HashMap At Position
        holder.mHeader!!.setText(map.name )

        //Setting version to TextView it's Key from HashMap At Position
        holder.mSubHeader!!.setText(map.hospital)

        Picasso.get().load(map.profileImageUrl).into(holder.mImageView  )


        return myview

    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }



    //Function to set data according to Search Keyword in ListView
    public fun filter(text: String?) {


        //Our Search text
        val text = text!!.toLowerCase()


        //Here We Clear Both ArrayList because We update according to Search query.
        //image.clear()
        if (mListAppInfoFiltered != null) {
            mListAppInfoFiltered.clear()
        }


        if (text.length == 0) {

            /*If Search query is Empty than we add all temp data into our main ArrayList

            We store Value in temp in Starting of Program.

            */

            //image.addAll(tempArrayList)
            if (mListAppInfoFiltered != null) {
                mListAppInfoFiltered.addAll(tempNameVersionList)
            }


        } else {


            for (i in 0..tempNameVersionList.size - 1) {

                /*
                If our Search query is not empty than we Check Our search keyword in Temp ArrayList.
                if our Search Keyword in Temp ArrayList than we add to our Main ArrayList
                */

                if (tempNameVersionList.get(i).name!!.toLowerCase().contains(text)) {

                    //image.add(tempArrayList.get(i))
                    if (mListAppInfoFiltered != null) {
                        mListAppInfoFiltered.add(tempNameVersionList.get(i))
                    }


                }

            }
        }

        //This is to notify that data change in Adapter and Reflect the changes.
        notifyDataSetChanged()


    }


}

