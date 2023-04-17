package ir.hadimohammadi.beharfim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<CardInfo> contactList;
    public static Activity context;



    public ContactsAdapter(List<CardInfo> contactList, Activity activity) {
        this.contactList = contactList;
        context = activity;
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {


        SharedPreferences.Editor editor;
        CardInfo ci = contactList.get(i);

        contactViewHolder.names.setText(ci.Names);
        contactViewHolder.numbers.setText(ci.Numbers);
        contactViewHolder.lastSeen.setText(ci.LastSeen);



        if(ci.inAppStatus == true){
            contactViewHolder.inAppStatus.setText("شروع گفتگو");
            contactViewHolder.inAppStatus.setBackgroundColor(R.color.purple_500);
            Glide   .with(context).load(ci.Avatar).into(contactViewHolder.profile_image);
            contactViewHolder.hhhh.setVisibility(View.GONE);
            contactViewHolder.inAppStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sp;
                    sp = context.getSharedPreferences("Beharfim",Context.MODE_PRIVATE);
                    sp.getString("Users_ID", "");


                    RequestQueue requestQueue;
                    Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
                    Network network = new BasicNetwork(new HurlStack());
                    requestQueue = new RequestQueue(cache, network);
                    requestQueue.start();

                    String url = "https://pgtab.ir/home/CreateChat?id="+ sp.getString("Users_ID", "")+"&idd="+ci.idd ;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                   try {
                                       JSONArray respoobb = new JSONArray(response);
                                       String bhadii = respoobb.getString(0);
                                       String bhadi36 = bhadii.replace("[", "");
                                       String bhadi46 = bhadi36.replace("]", "");
                                       JSONObject respobt = new JSONObject(bhadi46);


                                       Intent i = new Intent(context,Chatroom.class);
                                      i.putExtra("Groups_Title",ci.Names);
                                       i.putExtra("Groups_ID",respobt.getString("Groups_ID"));
                                       context.startActivity(i);




                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "نمیاد", Toast.LENGTH_SHORT).show();
                                }
                            });
                    requestQueue.add(stringRequest);








                }
            });

        }

        else if (ci.inAppStatus == false    ){

            contactViewHolder.hhh.setVisibility(View.GONE);
            contactViewHolder.hhhh.setVisibility(View.GONE);

                 }

        else {
            contactViewHolder.hhh.setVisibility(View.GONE);
            contactViewHolder.hhhh.setVisibility(View.GONE);

        }





    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contactscard, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView names,numbers,lastSeen,inAppStatus,namess,numberss,lastSeenn,inAppStatuss,idd;
        CircleImageView profile_image,profile_imagee;
        CardView hhh,hhhh;

        public ContactViewHolder(final View v) {
            super(v);
          //  sp = getApplicationContext().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
         //   sp.getString("Users_ID", "");
            names = v.findViewById(R.id.names);
            inAppStatus = v.findViewById(R.id.inAppStatus);
            lastSeen = v.findViewById(R.id.lastSeen);
            numbers = v.findViewById(R.id.numbers);
            profile_image = v.findViewById(R.id.profile_image);
            hhh = v.findViewById(R.id.hhh);
            idd = v.findViewById(R.id.idd);


            namess = v.findViewById(R.id.namess);
            inAppStatuss = v.findViewById(R.id.inAppStatuss);
            lastSeenn = v.findViewById(R.id.lastSeenn);
            numberss = v.findViewById(R.id.numberss);
            profile_imagee = v.findViewById(R.id.profile_imagee);
            hhhh = v.findViewById(R.id.hhhh);

        }
    }
}
