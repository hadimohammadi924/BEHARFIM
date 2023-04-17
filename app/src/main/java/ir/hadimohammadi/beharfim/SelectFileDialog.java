package ir.hadimohammadi.beharfim;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class SelectFileDialog extends Dialog {

    public Activity context;

    Dialog dialog;
    LinearLayout cameraBtn,selectFromGallery;


    int status = 0;



    public SelectFileDialog(Activity a) {
        super(a);
        this.context = a;
        dialog = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectfiledialog);
        setCancelable(true);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        cameraBtn = findViewById(R.id.cameraBtn);
        selectFromGallery = findViewById(R.id.selectFromGallery);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        selectFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(status == 2) {
                    Chatroom.selectFromGallery();
                }
            }
        });

    }
}
