package com.example.arcmessenger.manageChatAndImage.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.arcmessenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class DialogReviewSendImage {
    private Context context;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private ImageView imageView;
    private FloatingActionButton floatingActionButton;

    public DialogReviewSendImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.dialog = new Dialog(context);

        initialize();
    }

    private void initialize() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_image);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);

        imageView = dialog.findViewById(R.id.sendImage);
        floatingActionButton = dialog.findViewById(R.id.buttonSend);
    }

    public void showImage(final OnCallBack onCallBack){
        dialog.show();
        imageView.setImageBitmap(bitmap);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallBack.onButtonSend();
                dialog.dismiss();
            }
        });
    }

    public interface OnCallBack{
        void onButtonSend();
    }
}
