package com.detoxyourtv.home;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

final class MenuRow {

    private MenuRow() {
    }

    static void add(LayoutInflater inflater, LinearLayout rows, PopupWindow popup, int title, Runnable action) {
        View row = inflater.inflate(R.layout.item_input, rows, false);
        row.findViewById(R.id.icon).setVisibility(View.GONE);
        row.findViewById(R.id.dot).setVisibility(View.GONE);
        row.findViewById(R.id.status).setVisibility(View.GONE);
        ((TextView) row.findViewById(R.id.title)).setText(title);
        Runnable run = () -> {
            popup.dismiss();
            action.run();
        };
        row.setOnClickListener(v -> run.run());
        row.setOnKeyListener(activateOnFreshPress(run));
        rows.addView(row);
    }

    private static View.OnKeyListener activateOnFreshPress(Runnable run) {
        final boolean[] pressed = {false};
        return (v, code, event) -> {
            if (code != KeyEvent.KEYCODE_DPAD_CENTER && code != KeyEvent.KEYCODE_ENTER) return false;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getRepeatCount() == 0) pressed[0] = true;
                return true;
            }
            boolean activate = pressed[0];
            pressed[0] = false;
            if (activate) run.run();
            return true;
        };
    }
}
