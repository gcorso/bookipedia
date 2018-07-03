package com.gcappslab.bookipedia.Library.Browse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * From Stack Overflow
 * Used for the grid of themes in the Browse activity, a normal gridview cannot be used since it
 * is inside a ScrollView (two vertically scrollable items cannot be one inside the other).
 */

public class ExpandableHeightGridView extends GridView {

    private boolean expanded = false;

    public ExpandableHeightGridView(Context context) {
        super(context);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isExpanded()) {
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}