package com.zhou.common.view.recycle;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by zhouLei03 on 2023/4/24
 * recyclerView的GridItem间距
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 行间距
     */
    private final int lineSpace;
    /**
     * 列的数量
     */
    private final int columnNum;
    /**
     * item内宽度
     */
    private final int itemWidthInside;

    public GridItemDecoration(int lineSpace, int columnNum, int itemWidthInside, Context mContext) {
        this.lineSpace = dip2px(lineSpace, mContext);
        this.columnNum = columnNum;
        this.itemWidthInside = dip2px(itemWidthInside, mContext);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % columnNum;
        //item外宽度
        int itemWidth = parent.getWidth() / columnNum;
        //item的padding
        int padding = itemWidth - itemWidthInside;
        //需要达到的space
        int space = (parent.getWidth() - columnNum * itemWidthInside) / (columnNum - 1);

        //第一个设置left为0，right为padding，因为space一定比padding大
        if (column == 0) {
            outRect.left = 0;
            outRect.right = padding;
        } else if (column == columnNum - 1) {//最有一个设置left为padding
            outRect.left = padding;
            outRect.right = 0;
        } else {//（因为item外宽度不会变，只是移动内item的位置）
            //保持这一列的left与上一列的right的间距为space（以为第一个已经设置了padding，所以用space-padding）
            outRect.left = space * column - padding * column;
            //如果column=1，相当于1个padding减去一个（space-padding）差值
            outRect.right = padding * (column + 1) - space * column;
        }
        //设置行间距
        outRect.bottom = lineSpace;
    }

    public int dip2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
