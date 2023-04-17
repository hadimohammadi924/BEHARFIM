package ir.hadimohammadi.beharfim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class register extends AppCompatActivity {

Date current;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());

    SharedPreferences sp;
    TextView picselection;
    TextInputEditText rusername2,ruserid2,rbio2;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sp = getApplicationContext().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);

     //   current=Calendar.getInstance().getTime();
        current=Calendar.getInstance().getTime();

        Toast.makeText(this, currentDateandTime, Toast.LENGTH_SHORT).show();
        rusername2=findViewById(R.id.rusername2);
        ruserid2=findViewById(R.id.ruserid2);
        rbio2=findViewById(R.id.rbio2);
        save=findViewById(R.id.save);
        picselection=findViewById(R.id.picselection);
        picselection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url = "https://pgtab.ir/home/completeUser?Users_ID="+sp.getString("Users_ID","")+"&Users_Name="+rusername2.getText().toString()+"&Users_Username="+ruserid2.getText().toString()+"&Users_Bio="+rbio2.getText().toString()+"&Users_Avatar=https://pgtab.info/user/hadi.jpg";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respo = new JSONObject(response);
                            String statue = respo.getString("statue");

                            if(statue.equals("Success")) {
                                Toast.makeText(register.this, "اطلاعات شما با موفقیت ثبت گردید", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(register.this,MainActivity.class));
                            }
                                else {
                                Toast.makeText(register.this, "مشکلی در ارتباط با سرور بوجود امده", Toast.LENGTH_SHORT).show();
                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(register.this, "عیبی نداره مشدی خدا بزرگه", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);


    }
});




    }

    @Override
    public void onBackPressed() {
        finish();
    }

}