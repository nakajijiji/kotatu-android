package com.kotatu.android.top;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotatu.android.R;
import com.kotatu.android.entity.Room;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mayuhei on 2017/05/19.
 */

public class DefaultRoomListAdapter extends ArrayAdapter<Room> {
    private List<Room> rooms;
    private LayoutInflater layoutInflater;
    private Resources resources;

    public DefaultRoomListAdapter(Context context, List<Room> rooms){
        super(context, 0, rooms);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resources = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = layoutInflater.inflate(R.layout.list_room, parent, false);
            Room room = getItem(position);
            TextView text = (TextView) view.findViewById(R.id.room_name);
            text.setText(room.getName());
            ImageView image = (ImageView) view.findViewById(R.id.room_image);
            image.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
        }else{
            view = convertView;
        }

        return view;
    }
}
