package com.niuedu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

/**
 * Created by nkm on 30/12/2017.
 */
public class ListTreeViewHolder extends RecyclerView.ViewHolder{
    protected ViewGroup containerView;
    protected ImageView arrowIcon;
    protected Space headSpace;

    public ListTreeViewHolder(View itemView) {
        super(itemView);
    }
}