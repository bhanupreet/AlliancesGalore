package com.alliancesgalore.alliancesgalore.Adapters;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alliancesgalore.alliancesgalore.R;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;

public class MonthHeader extends ViewContainer {

    public ConstraintLayout header;

    public MonthHeader(@NotNull View view) {
        super(view);
        header = view.findViewById(R.id.month_header);
    }
}
