package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.models.Cinema;

import java.util.ArrayList;

/**
 * Created by SHENGX on 2017/3/4.
 */

public class CinemaAdapter extends BaseAdapter{
    private ArrayList<Cinema> list;
    private Context mContext;

    public CinemaAdapter(Context context, ArrayList<Cinema> list){
        this.list = list;
        mContext = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Cinema getItem(int position) {
        return list.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.spinner_list_item,null);
        if(convertView != null){
            TextView name = (TextView) convertView.findViewById(R.id.txt_spinner);
            if(position==0){
                name.setText("Please Choose Location");
            }
            else {
                name.setText(list.get(position-1).getName());
            }
        }
        return convertView;
    }
}
