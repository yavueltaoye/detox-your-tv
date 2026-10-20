package com.detoxyourtv.home;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.function.BiFunction;

final class TileMenu {

    private static final float MIN_WIDTH_DP = 220f;

    private final Activity activity;
    private final PopupStage stage;
    private final BiFunction<View, Integer, PopupWindow> popups;
    private final AppAdapter adapter;
    private final TileMover mover;

    TileMenu(Activity activity, PopupStage stage, BiFunction<View, Integer, PopupWindow> popups,
             AppAdapter adapter, TileMover mover) {
        this.activity = activity;
        this.stage = stage;
        this.popups = popups;
        this.adapter = adapter;
        this.mover = mover;
    }

    void show(int position, View card, ViewGroup root) {
        View panel = activity.getLayoutInflater().inflate(R.layout.popup_inputs, root, false);
        LinearLayout rows = panel.findViewById(R.id.rows);
        int minWidth = Math.round(MIN_WIDTH_DP * activity.getResources().getDisplayMetrics().density);
        PopupWindow popup = popups.apply(panel, minWidth);
        if (adapter.movable(position)) {
            MenuRow.add(activity.getLayoutInflater(), rows, popup, R.string.tile_move, () -> mover.start(position, card));
        }
        MenuRow.add(activity.getLayoutInflater(), rows, popup, R.string.tile_info, () -> adapter.openDetails(position));
        panel.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popup.setWidth(Math.max(minWidth, panel.getMeasuredWidth()));
        stage.showAbove(popup, panel, card, Gravity.START);
        rows.getChildAt(0).requestFocus();
    }
}
