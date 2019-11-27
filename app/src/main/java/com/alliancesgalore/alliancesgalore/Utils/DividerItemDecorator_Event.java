package com.alliancesgalore.alliancesgalore.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alliancesgalore.alliancesgalore.R;

public class DividerItemDecorator_Event extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DividerItemDecorator_Event(Context context) {
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = 0;
        int right = parent.getWidth();
        View child = parent.getChildAt(parent.getChildCount()-1);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int top = child.getBottom() + params.bottomMargin;
        int bottom = top + mDivider.getIntrinsicHeight();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}
