package ir.hadimohammadi.beharfim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Login extends AppCompatActivity {

    String PhoneNumber;
    TextInputEditText etNumber;
    Button submitButton, sencode;
    LinearLayout codeLayout;
    public static String Code = "";
    public static EditText et4;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    private TextView countdownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 60000; // 10 minutes
    LinearLayout one;
    TextView resend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etNumber = findViewById(R.id.etNumber);
        submitButton = findViewById(R.id.submitButton);
        codeLayout = findViewById(R.id.codeLayout);
        sencode = findViewById(R.id.sendcode);
        one = findViewById(R.id.one);
        resend = findViewById(R.id.resend);
        countdownText = findViewById(R.id.countdownText);
////*****

        et4 = findViewById(R.id.et4);
        sp = getApplicationContext().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
        editor = sp.edit();


        et4.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {


                // checkCode(PhoneNumber,Code);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et4.length() == 4) {
                    sencode.setEnabled(true);
                    sencode.setBackgroundResource(R.color.purple_500);
                } else {
                }
            }
        });
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etNumber.length() == 11) {
                    submitButton.setEnabled(true);
                    submitButton.setBackgroundResource(R.color.purple_500);
                } else {
                    submitButton.setBackgroundResource(R.color.white);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermission();

                    /*
                    if (etNumber.length() == 11) {

                     login();
                    } else {Toast.makeText(Login.this, "شماره تلفن را با صفر وارد کنید", Toast.LENGTH_SHORT).show();}
                    */

                    } else {

                        if (etNumber.length() == 11) {
                            one.setVisibility(View.VISIBLE);
                            login();
                            startTimer();
                        } else {
                            Toast.makeText(Login.this, "شماره تلفن را با صفر وارد کنید", Toast.LENGTH_SHORT).show();
                        }


                    }
                }


            }
        });
        sencode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode(PhoneNumber, et4.getText().toString());
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                resend.setVisibility(View.GONE);
                login();
                timeLeftInMilliseconds = 60000;
                startTimer();
            }
        });

    }

    public void reDo(View view) {

    }


    public void checkCode(String Phone, String Code) {

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url = "https://pgtab.ir/home/checkCode?Phone=" + Phone + "&code=" + Code;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respo = new JSONObject(response);
                            String statue = respo.getString("statue");

                            if (statue.equals("CodeExpired")) {
                                Toast.makeText(Login.this, "کد منقضی شده", Toast.LENGTH_SHORT).show();
                            } else if (statue.equals("Success")) {

                                editor.putString("Users_ID", respo.getString("id"));
                                editor.putString("Users_Name", respo.getString("name"));
                                editor.apply();

                                if (!respo.getString("name").equals("null")) {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(Login.this, register.class));
                                }

                            } else if (statue.equals("InvalidCode")) {
                                Toast.makeText(Login.this, "کد وارد شده اشتباه هست", Toast.LENGTH_SHORT).show();
                            }

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


    public void login() {


        PhoneNumber = etNumber.getText().toString();
        etNumber.setEnabled(false);
        submitButton.setEnabled(false);

        etNumber.setAlpha(0.5f);
        submitButton.setAlpha(0.5f);
        codeLayout.setVisibility(View.VISIBLE);
        // sendcodec.setVisibility(View.VISIBLE);

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url = "https://pgtab.ir/home/login?Phone=" + PhoneNumber;
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


    private void requestPermission() {
        Toast.makeText(this, "جهت ارسال خودکار کد ورود مجوز بدهید", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(Login.this,
                new String[]{Manifest.permission.RECEIVE_SMS}, 2);

    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                countdownText.setText("--:--");
                resend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMilliseconds / 1000) / 60;
        int seconds = (int) (timeLeftInMilliseconds / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdownText.setText(timeLeftFormatted);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


}