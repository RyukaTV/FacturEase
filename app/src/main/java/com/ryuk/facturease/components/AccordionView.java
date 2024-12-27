package com.ryuk.facturease.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryuk.facturease.R;

import java.util.List;

public class AccordionView extends LinearLayout {

    private TextView titleView;
    private LinearLayout contentLayout;
    private boolean isExpanded= false;

    public AccordionView(Context context) {
        super(context);
        init(context);
    }

    public AccordionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AccordionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_accordion, this, true);
        titleView = findViewById(R.id.titleView);
        contentLayout = findViewById(R.id.contentLayout);
        contentLayout.setVisibility(GONE);

        titleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    collapse(contentLayout);
                } else {
                    expand(contentLayout);
                }
                isExpanded = !isExpanded;
            }
        });
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void addContent(View v) {
        contentLayout.addView(v);
    }

    public void addContent(List<View> lv) {
        for (View v: lv) {
            addContent(v);
        }
    }

    private void expand(final View v) {
        v.setVisibility(VISIBLE);
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
