package com.kotatu.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotatu.android.R;
import com.kotatu.android.entity.User;

import java.util.List;

/**
 * Created by mayuhei on 2017/05/19.
 */

public class DefaultUserListAdapter extends ArrayAdapter<User> {
    private List<User> users;
    private LayoutInflater layoutInflater;
    private Resources resources;

    public DefaultUserListAdapter(Context context, List<User> users){
        super(context, 0, users);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resources = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = layoutInflater.inflate(R.layout.list_user, parent, false);
            User user = getItem(position);
            TextView text = (TextView) view.findViewById(R.id.user_name);
            text.setText(user.getScreenName());
            ImageView image = (ImageView) view.findViewById(R.id.user_image);
            image.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
        }else{
            view = convertView;
        }

        return view;
    }
}
