package com.detoxyourtv.home;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

final class ControlCenter {

    private static final String TV_SETTINGS = "com.android.tv.settings";
    private static final String PICTURE = ".picture.ui.PictureActivity";
    private static final String ACCESSORIES = ".accessories.AddAccessoryActivity";
    private static final int COLUMNS = 2;
    private static final float FOCUS_SCALE = 1.03f;
    private static final int FOCUS_MS = 160;

    private final Activity activity;
    private final PopupStage stage;
    private final int gap;

    ControlCenter(Activity activity, PopupStage stage) {
        this.activity = activity;
        this.stage = stage;
        this.gap = activity.getResources().getDimensionPixelSize(R.dimen.module_gap);
    }

    void openAssistant() {
        launch(new Intent(Intent.ACTION_ASSIST));
    }

    PopupWindow show(View anchor, ViewGroup root) {
        View panel = activity.getLayoutInflater().inflate(R.layout.popup_inputs, root, false);
        LinearLayout rows = panel.findViewById(R.id.rows);
        int width = activity.getResources().getDimensionPixelSize(R.dimen.control_center_width);
        PopupWindow popup = stage.create(panel, width, () -> { });
        addModules(rows, popup);
        stage.show(popup, panel, anchor, Gravity.END);
        ((ViewGroup) rows.getChildAt(0)).getChildAt(0).requestFocus();
        return popup;
    }

    private void addModules(LinearLayout rows, PopupWindow popup) {
        addModule(rows, popup, R.drawable.ic_mic, R.string.cc_assistant, this::openAssistant);
        addModule(rows, popup, R.drawable.ic_picture, R.string.cc_picture, () -> launch(tvSettings(PICTURE)));
        addModule(rows, popup, R.drawable.ic_sound, R.string.cc_sound,
                () -> launch(new Intent(Settings.ACTION_SOUND_SETTINGS)));
        addModule(rows, popup, R.drawable.ic_bluetooth, R.string.cc_bluetooth, () -> launch(tvSettings(ACCESSORIES)));
        addModule(rows, popup, R.drawable.ic_wifi, R.string.cc_network,
                () -> launch(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        addModule(rows, popup, R.drawable.ic_settings, R.string.cc_all_settings,
                () -> launch(new Intent(Settings.ACTION_SETTINGS)));
    }

    private void addModule(LinearLayout rows, PopupWindow popup, int icon, int title, Runnable action) {
        LinearLayout line = openLine(rows);
        View module = activity.getLayoutInflater().inflate(R.layout.item_module, line, false);
        ((ImageView) module.findViewById(R.id.icon)).setImageResource(icon);
        ((TextView) module.findViewById(R.id.title)).setText(title);
        if (line.getChildCount() > 0) {
            ((LinearLayout.LayoutParams) module.getLayoutParams()).setMarginStart(gap);
        }
        module.setOnFocusChangeListener((v, focused) -> v.animate()
                .scaleX(focused ? FOCUS_SCALE : 1f).scaleY(focused ? FOCUS_SCALE : 1f)
                .setDuration(FOCUS_MS).setInterpolator(new DecelerateInterpolator()).start());
        module.setOnClickListener(v -> {
            popup.dismiss();
            action.run();
        });
        line.addView(module);
    }

    private LinearLayout openLine(LinearLayout rows) {
        int count = rows.getChildCount();
        if (count > 0) {
            LinearLayout last = (LinearLayout) rows.getChildAt(count - 1);
            if (last.getChildCount() < COLUMNS) return last;
        }
        LinearLayout line = new LinearLayout(activity);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setClipChildren(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (count > 0) params.topMargin = gap;
        rows.addView(line, params);
        return line;
    }

    private static Intent tvSettings(String activityName) {
        return new Intent().setComponent(new ComponentName(TV_SETTINGS, TV_SETTINGS + activityName));
    }

    private void launch(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(activity, R.string.not_available, Toast.LENGTH_SHORT).show();
        }
    }
}
