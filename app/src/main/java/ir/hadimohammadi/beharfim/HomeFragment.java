package ir.hadimohammadi.beharfim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.android.gms.tasks.OnCompleteListener;
      //import com.google.android.gms.tasks.Task;
      //import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    int groupsType = 0;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;
    Cache cache;
    RecyclerView recyclerView;


    public HomeFragment(int groupsType) {
        // Required empty public constructor
        this.groupsType = groupsType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        TextView text = view.findViewById(R.id.text);
        recyclerView = view.findViewById(R.id.recyclerView);

        sp = getActivity().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);


/*
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FireBase", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                     //   addDevice(token);
                    //    Toast.makeText(getActivity(), token, Toast.LENGTH_SHORT).show();

                    }
                });



 */


        if (groupsType == 0) {
            text.setText("چت های شخصی");
        } else if (groupsType == 1) {
            text.setText("گروه ها");
        } else if (groupsType == 2) {
            text.setText("کانال ها");
        } else if (groupsType == 3) {
            text.setText("عمومی");
        }


        String Users_ID = sp.getString("Users_ID", "");
        cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url ="https://pgtab.ir/home/getChats?Users_ID=" + Users_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            List<CardInfo> cards = new ArrayList<>();
                            JSONObject respo = new JSONObject(response);
                            JSONArray items = respo.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject temp = items.getJSONObject(i);
                                CardInfo ci = new CardInfo();
                                ci.Groups_ID = temp.getString("Groups_ID");
                                ci.Groups_Title = temp.getString("Groups_Title");
                                ci.Groups_Username = temp.getString("Groups_Username");
                                ci.Groups_Avatar = temp.getString("Groups_Avatar");
                                ci.Groups_About = temp.getString("Groups_About");

                                cards.add(ci);
                            }

                            CardAdapter adapter = new CardAdapter(cards,getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);






        return view;
    }

    /*
    public void addDevice(String Token) {

        String DeviceModel = Build.MODEL;
        String DeviceID = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        SharedPreferences sp = getActivity().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
        String Users_ID = sp.getString("Users_ID","");
        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url =  "https://pgtab.ir/home/addDevice?DeviceModel=" + DeviceModel+"&Users_ID="+Users_ID+"&Token="+Token+"&DeviceID="+DeviceID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }


     */

    public void getChats() {

        String Users_ID = sp.getString("Users_ID", "");
        cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url ="https://pgtab.ir/home/getChats?Users_ID=" + Users_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            List<CardInfo> cards = new ArrayList<>();
                            JSONObject respo = new JSONObject(response);
                            JSONArray items = respo.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject temp = items.getJSONObject(i);
                                CardInfo ci = new CardInfo();
                                ci.Groups_ID = temp.getString("Groups_ID");
                                ci.Groups_Title = temp.getString("Groups_Title");
                                ci.Groups_Username = temp.getString("Groups_Username");
                                ci.Groups_Avatar = temp.getString("Groups_Avatar");
                                ci.Groups_About = temp.getString("Groups_About");

                                cards.add(ci);
                            }

                            CardAdapter adapter = new CardAdapter(cards,getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.serach_action);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.LEFT));
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);


    }









}