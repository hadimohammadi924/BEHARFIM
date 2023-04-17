package ir.hadimohammadi.beharfim;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Contacts extends AppCompatActivity {


    RecyclerView recyclerView;
    EditText et;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        et = findViewById(R.id.et);
        recyclerView.setLayoutManager(new LinearLayoutManager(Contacts.this));

        getContactList();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        getContactList();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

    }


    private void getContactList() {

        String names = "";
        String number = "";


        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        names += name + ",";
                        number += phoneNo + ",";
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        names = names.substring(0, names.length() - 1);
        number = number.substring(0, number.length() - 1);

        syncContacts(names, number);
    }


    public void syncContacts(String names, String numbers) {

        List<CardInfo> lst = new ArrayList<>();

        String url = "https://pgtab.ir/home/syncContacts";


        Map<String, String> params = new HashMap();
        params.put("names", names);
        params.put("numbers", numbers);

       // et.setText(names+ "\n"+ numbers);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray list = response.getJSONArray("list");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        CardInfo ci = new CardInfo();
                        ci.Names = item.getString("Name");
                        ci.Numbers = item.getString("Number");
                        ci.Avatar = item.getString("Avatar");
                        ci.LastSeen = item.getString("LastSeen");
                        ci.inAppStatus = item.getBoolean("inAppStatus");
                        ci.idd = item.getString("id");

                        lst.add(ci);
                    }


                    ContactsAdapter adapter = new ContactsAdapter(lst, Contacts.this);
                 recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   error.printStackTrace();

            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }



}