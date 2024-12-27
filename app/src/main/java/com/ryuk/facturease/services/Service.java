package com.ryuk.facturease.services;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Service {

    public static void swapActivityOnClick(Button btn, final Activity activity, final Class<?> clsTo, boolean beforeCanFinish) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, clsTo));
                if (beforeCanFinish){
                    activity.finish();
                }
            }
        });
    }
}
