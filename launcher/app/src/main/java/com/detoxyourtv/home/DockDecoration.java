package com.detoxyourtv.home;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Draws the tvOS style glass shelf behind the first (dock) row and adds a gap below it: a translucent
 * white fill that lets the ambient colour through, brighter at the top, with an edge that catches the
 * light along its top and fades towards the bottom.
 */
public class DockDecoration extends RecyclerView.ItemDecoration {

    private static final int FILL_TOP = 0x26FFFFFF;
    private static final int FILL_BOTTOM = 0x0DFFFFFF;
    private static final int EDGE_TOP = 0x66FFFFFF;
    private static final int EDGE_BOTTOM = 0x14FFFFFF;
    private static final float OFFSET_TOP_DP = 6f;
    private static final float OFFSET_BOTTOM_DP = 12f;

    private final int dockSize;
    private final float density;
    private final float pad;
    private final float radius;
    private final Paint band = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private float shaderTop = Float.NaN;
    private float shaderBottom = Float.NaN;

    DockDecoration(int dockSize, float density, float pad, float radius) {
        this.dockSize = dockSize;
        this.density = density;
        this.pad = pad;
        this.radius = radius;
        edge.setStyle(Paint.Style.STROKE);
        edge.setStrokeWidth(density);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (pos >= 0 && pos < dockSize) {
            outRect.top = (int) (OFFSET_TOP_DP * density);
            outRect.bottom = (int) (OFFSET_BOTTOM_DP * density);
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (!dockBounds(parent)) return;
        rect.inset(-pad, -pad);
        updateShaders();
        c.drawRoundRect(rect, radius, radius, band);
        float half = edge.getStrokeWidth() / 2f;
        rect.inset(half, half);
        c.drawRoundRect(rect, radius - half, radius - half, edge);
    }

    private boolean dockBounds(RecyclerView parent) {
        boolean any = false;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            if (pos < 0 || pos >= dockSize) continue;
            View card = child.findViewById(R.id.card);
            if (card == null) continue;
            float l = child.getLeft() + card.getLeft();
            float t = child.getTop() + card.getTop() + child.getTranslationY();
            float r = l + card.getWidth();
            float b = t + card.getHeight();
            if (any) {
                rect.union(l, t, r, b);
            } else {
                rect.set(l, t, r, b);
                any = true;
            }
        }
        return any;
    }

    private void updateShaders() {
        if (rect.top == shaderTop && rect.bottom == shaderBottom) return;
        shaderTop = rect.top;
        shaderBottom = rect.bottom;
        band.setShader(new LinearGradient(0, rect.top, 0, rect.bottom, FILL_TOP, FILL_BOTTOM, Shader.TileMode.CLAMP));
        edge.setShader(new LinearGradient(0, rect.top, 0, rect.bottom, EDGE_TOP, EDGE_BOTTOM, Shader.TileMode.CLAMP));
    }
}
