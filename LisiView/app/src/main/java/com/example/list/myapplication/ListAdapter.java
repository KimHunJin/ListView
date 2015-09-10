package com.example.list.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by HunJin on 2015-09-11.
 */
public class ListAdapter extends ArrayAdapter<ListItem>{
    private LayoutInflater mInflater = null;
    private Context mContext = null;
    private ImageLoader loader = null;

    public ListAdapter(Context context, int resource, List<ListItem> objects) {
        super(context, resource, objects);

        mInflater = LayoutInflater.from(context);
        mContext = context;
        loader = ImageLoader.getInstance();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;

        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.item, parent, false);
            holder.mRoomNameView = (TextView) convertView.findViewById(R.id.roomName);
            holder.mRoomInfoView = (TextView) convertView.findViewById(R.id.roomInfo);
            holder.mRoomLanguageView = (TextView) convertView.findViewById(R.id.roomLanguage);
            holder.mRoomLocationView = (TextView) convertView.findViewById(R.id.roomLocation);
            holder.mRoomImageView = (ImageView) convertView.findViewById(R.id.roomImage);
            holder.mRoomMemberView = (TextView)convertView.findViewById(R.id.roomMemberCount);
            convertView.setTag(holder);
        }

        holder = (Holder) convertView.getTag();
        ListItem item = getItem(position);
        holder.mRoomNameView.setText(item.getmRoomName());
        holder.mRoomInfoView.setText(item.getmRoomInfo());
        holder.mRoomLocationView.setText(item.getmRoomLocation());
        holder.mRoomLanguageView.setText(item.getmRoomLanguage());
        holder.mRoomMemberView.setText(item.getmRoomMember());
        loader.displayImage(Img_Path.IMG_PATH + item.getmRoomImage(), holder.mRoomImageView);

        return convertView;
    }
}
