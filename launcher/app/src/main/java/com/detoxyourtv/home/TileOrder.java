package com.detoxyourtv.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class TileOrder {

    private static final String PREFS = "tile_order";
    private static final String KEY = "order";
    private static final String SEPARATOR = "\n";

    private final SharedPreferences prefs;

    TileOrder(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    List<String> load() {
        String raw = prefs.getString(KEY, "");
        if (raw == null || raw.isEmpty()) return Collections.emptyList();
        return Arrays.asList(raw.split(SEPARATOR));
    }

    void save(List<String> keys) {
        prefs.edit().putString(KEY, TextUtils.join(SEPARATOR, keys)).apply();
    }
}
