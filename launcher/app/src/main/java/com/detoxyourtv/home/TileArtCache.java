package com.detoxyourtv.home;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

final class TileArtCache {

    static final class Art {
        final Drawable banner;
        final Drawable icon;

        Art(Drawable banner, Drawable icon) {
            this.banner = banner;
            this.icon = icon;
        }
    }

    private Map<String, Art> art = new HashMap<>();
    private Map<String, Art> next = new HashMap<>();

    Art get(ActivityInfo info, Function<ActivityInfo, Art> load) {
        String key = keyOf(info);
        Art found = art.get(key);
        if (found == null) found = load.apply(info);
        next.put(key, found);
        return found;
    }

    void commit() {
        art = next;
        next = new HashMap<>();
    }

    static boolean sameTiles(List<AppAdapter.Entry> a, List<AppAdapter.Entry> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            AppAdapter.Entry x = a.get(i), y = b.get(i);
            boolean same = x.kind == y.kind && x.component.equals(y.component) && x.name.equals(y.name)
                    && (x.kind == AppAdapter.Entry.HOME_SETTINGS || (x.banner == y.banner && x.icon == y.icon));
            if (!same) return false;
        }
        return true;
    }

    private static String keyOf(ActivityInfo info) {
        return info.packageName + '/' + info.name + '@' + Objects.hashCode(info.applicationInfo.sourceDir)
                + ':' + info.applicationInfo.uid;
    }
}
