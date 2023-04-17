package ir.hadimohammadi.beharfim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chatroom extends AppCompatActivity {

    public static String Groups_ID;
    String Groups_Title;
    TextView groupName;
    public static EditText text;
    TextView percentText;
    FloatingActionButton sendFab;
    public static RecyclerView recyclerView;
    public static Activity context;
    int PICK_IMAGE = 1;
    TextView uploadFileText;
    RelativeLayout uploadBar;
    private ArrayList<FileUtils> selectedFiles2 = new ArrayList<>();
    SharedPreferences sp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = data.getData();
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        FileUtils fileUtils = FileUtils.getMetaData(Chatroom.this, contentUri);
        fileUtils.mimeType = "image/jpeg";
        FileUtils.resizeIfMedia(getApplicationContext(), fileUtils);
        selectedFiles2.add(fileUtils);


        String url = "https://pgtab.ir/home/UploadFile";
        Log.d("url", url);

        uploadBar.setVisibility(View.VISIBLE);
        uploadFileText.setText(getFileName(data.getData()));

        MultiPartConnection multipart = new MultiPartConnection(url,
                new MultiPartConnection.FileUploadListener() {
                    @Override
                    public void onUpdateProgress(int percentage, int i) {
                        //Toast.makeText(Chatroom.this, percentage, Toast.LENGTH_SHORT).show();
                        try {
                            percentText.setText(percentage + "%");
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onResponseListener(String response) {
                        Log.d("multiPartResp", response);
                        //Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        try {
                            uploadBar.setVisibility(View.GONE);
                        }catch (Exception e){

                        }
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                    @Override
                    public void onErrorListener(String Error) {

                    }
                });
        for (int i = 0; i < selectedFiles2.size(); i++) {
            multipart.addFile("file", new File(selectedFiles2.get(i).path));
        }
        String Users_ID = sp.getString("Users_ID", "");
        multipart.addData("SenderID", Users_ID);
        multipart.addData("Group_ID", Groups_ID);
        multipart.Execute();


    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        context = Chatroom.this;

        groupName = findViewById(R.id.groupName);
        text = findViewById(R.id.text);
        sendFab = findViewById(R.id.sendFab);
        recyclerView = findViewById(R.id.recyclerView);
        uploadFileText = findViewById(R.id.uploadFileText);
        uploadBar = findViewById(R.id.uploadBar);
        percentText = findViewById(R.id.percentText);
        sp = getSharedPreferences("Beharfim", Context.MODE_PRIVATE);

        Groups_Title = getIntent().getExtras().getString("Groups_Title");
        Groups_ID = getIntent().getExtras().getString("Groups_ID");

        recyclerView.setLayoutManager(new LinearLayoutManager(Chatroom.this));

        groupName.setText(Groups_Title);

        getMessages(true);


        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().equals("")) {
                    sendMessageToGroup(text.getText().toString());
                }
            }
        });

        text.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    sendFab.show();
                } else {
                    sendFab.hide();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });


    }


    public void sendMessageToGroup(String messageBody) {

        SharedPreferences sp = getApplicationContext().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
        String Users_ID = sp.getString("Users_ID", "");
        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url ="https://pgtab.ir/home/sendMessageToGroup?Text=" + messageBody + "&SenderID=" + Users_ID + "&Group_ID=" + Groups_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getMessages(true);
                        text.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }

    public static void getMessages(boolean sendStatus) {
        if (!sendStatus) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.bubble);
            mp.start();
        }


        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url = "https://pgtab.ir/home/getMessages?Group_ID=" + Groups_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respo = new JSONObject(response);
                            JSONArray items = respo.getJSONArray("items");
                            List<CardInfo> lst = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                CardInfo ci = new CardInfo();
                                JSONObject temp = items.getJSONObject(i);
                                ci.GroupMessages_ID = temp.getString("GroupMessages_ID");
                                ci.GroupMessages_Insert_Date = temp.getString("GroupMessages_Insert_Date");
                                ci.GroupMessages_Sender_ID = temp.getString("GroupMessages_Sender_ID");
                                ci.GroupMessages_Text = temp.getString("GroupMessages_Text");
                                ci.GroupMessages_File = temp.getString("GroupMessages_File");
                                ci.UserImage = temp.getString("UserImage");
                                ci.UserName = temp.getString("UserName");
                                lst.add(ci);
                            }
                            Collections.reverse(lst);
                            MessagesAdapter adapter = new MessagesAdapter(lst, context);
                            LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true); // last argument (true) is flag for reverse layout
                            recyclerView.setLayoutManager(lm);
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

    public void OpenFileDialog(View view) {
        SelectFileDialog dialog = new SelectFileDialog(Chatroom.this);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    public static void selectFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(Intent.createChooser(intent, "انتخاب تصویر از گالری"), 1);
    }



}