package com.detoxyourtv.home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.function.IntConsumer;

final class TileMover {

    private static final int IDLE = -1;
    private static final float WIGGLE_DEG = 1.1f;
    private static final int WIGGLE_MS = 120;
    private static final int HINT_IN_MS = 220;
    private static final int HINT_OUT_MS = 160;

    private final AppAdapter adapter;
    private final View grid;
    private final TextView hint;
    private final int columns;
    private final boolean rtl;
    private final boolean wiggles;
    private final IntConsumer focus;
    private int position = IDLE;
    private View card;
    private ObjectAnimator wiggle;

    TileMover(AppAdapter adapter, View grid, TextView hint, int columns, boolean rtl, boolean wiggles, IntConsumer focus) {
        this.adapter = adapter;
        this.grid = grid;
        this.hint = hint;
        this.columns = columns;
        this.rtl = rtl;
        this.wiggles = wiggles;
        this.focus = focus;
    }

    void start(int position, View card) {
        if (!adapter.movable(position)) return;
        finish();
        this.position = position;
        this.card = card;
        hint.setText(hint.getResources().getString(R.string.tile_move_hint, adapter.labelAt(position)));
        hint.animate().cancel();
        hint.animate().alpha(1f).setDuration(HINT_IN_MS).start();
        if (!wiggles) return;
        wiggle = ObjectAnimator.ofFloat(card, View.ROTATION, -WIGGLE_DEG, WIGGLE_DEG);
        wiggle.setDuration(WIGGLE_MS);
        wiggle.setRepeatMode(ValueAnimator.REVERSE);
        wiggle.setRepeatCount(ValueAnimator.INFINITE);
        wiggle.start();
    }

    boolean active() {
        return position != IDLE;
    }

    boolean onKey(int keyCode, KeyEvent event) {
        if (position == IDLE) return false;
        boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (down) step(target(keyCode));
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BACK:
                if (!down) finish();
                return true;
            default:
                return false;
        }
    }

    void finish() {
        if (position == IDLE) return;
        position = IDLE;
        if (wiggle != null) wiggle.cancel();
        wiggle = null;
        card.setRotation(0f);
        card = null;
        hint.animate().cancel();
        hint.animate().alpha(0f).setDuration(HINT_OUT_MS).start();
        adapter.saveOrder();
    }

    private int target(int keyCode) {
        int last = adapter.getItemCount() - 1;
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            boolean up = keyCode == KeyEvent.KEYCODE_DPAD_UP;
            int to = position + (up ? -columns : columns);
            boolean pastLastRow = !up && last / columns == position / columns;
            return to < 0 || pastLastRow ? IDLE : Math.min(to, last);
        }
        boolean back = (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) != rtl;
        int to = position + (back ? -1 : 1);
        return to >= 0 && to <= last && to / columns == position / columns ? to : IDLE;
    }

    private void step(int to) {
        if (to < 0 || to == position || !adapter.movable(to)) return;
        adapter.move(position, to);
        position = to;
        grid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                grid.getViewTreeObserver().removeOnPreDrawListener(this);
                focus.accept(to);
                return true;
            }
        });
    }
}
