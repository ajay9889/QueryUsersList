package com.usersinformation.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.usersinformation.R;
/**
 * Created by Ajay on 07/09/18.
 * View holder to access the widgets
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView name ,gender,email;
    public ImageView user_photo;
    public LinearLayout user_row;
    public RecyclerViewHolder(View view) {
        super(view);
        this.name = (TextView) view.findViewById(R.id.name);
        this.user_photo = (ImageView) view.findViewById(R.id.user_photo);
        this.gender = (TextView) view.findViewById(R.id.gender);
        this.email = (TextView) view.findViewById(R.id.email);
        this.user_row = (LinearLayout) view.findViewById(R.id.user_row);
    }
}
