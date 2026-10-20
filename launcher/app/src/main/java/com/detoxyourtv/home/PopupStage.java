package com.detoxyourtv.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

final class PopupStage {

    private static final float SCRIM_ALPHA = 0.55f;
    private static final int SCRIM_IN_MS = 240;
    private static final int SCRIM_OUT_MS = 180;
    private static final int DROP_MS = 260;
    private static final float DROP_SCALE = 0.94f;
    private static final float DROP_LIFT_DP = 8f;
    private static final float DECELERATION = 1.8f;

    private final View scrim;
    private final float density;
    private final int dropOffset;

    PopupStage(View scrim, float density, int dropOffset) {
        this.scrim = scrim;
        this.density = density;
        this.dropOffset = dropOffset;
    }

    PopupWindow create(View panel, int width, Runnable onDismiss) {
        PopupWindow popup = new PopupWindow(panel, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setAnimationStyle(0);
        popup.setElevation(0f);
        popup.setOnDismissListener(() -> {
            fadeScrim(0f, SCRIM_OUT_MS);
            onDismiss.run();
        });
        return popup;
    }

    void show(PopupWindow popup, View panel, View anchor, int gravity) {
        prepare(popup, panel, gravity, 0f, -DROP_LIFT_DP * density);
        popup.showAsDropDown(anchor, 0, dropOffset, gravity);
        reveal(panel);
    }

    void showAbove(PopupWindow popup, View panel, View anchor, int gravity) {
        panel.measure(View.MeasureSpec.makeMeasureSpec(popup.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED);
        int height = panel.getMeasuredHeight();
        prepare(popup, panel, gravity, height, DROP_LIFT_DP * density);
        popup.showAsDropDown(anchor, 0, -(anchor.getHeight() + height + dropOffset), gravity);
        reveal(panel);
    }

    private void prepare(PopupWindow popup, View panel, int gravity, float pivotY, float lift) {
        panel.setAlpha(0f);
        panel.setPivotX(gravity == Gravity.END ? popup.getWidth() : 0f);
        panel.setPivotY(pivotY);
        panel.setScaleX(DROP_SCALE);
        panel.setScaleY(DROP_SCALE);
        panel.setTranslationY(lift);
    }

    private void reveal(View panel) {
        panel.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(0f).setDuration(DROP_MS)
                .setInterpolator(new DecelerateInterpolator(DECELERATION)).start();
        fadeScrim(SCRIM_ALPHA, SCRIM_IN_MS);
    }

    private void fadeScrim(float alpha, int durationMs) {
        scrim.animate().cancel();
        scrim.animate().alpha(alpha).setDuration(durationMs).start();
    }
}
