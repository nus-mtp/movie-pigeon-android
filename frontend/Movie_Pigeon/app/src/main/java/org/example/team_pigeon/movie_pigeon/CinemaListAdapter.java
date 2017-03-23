package org.example.team_pigeon.movie_pigeon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.example.team_pigeon.movie_pigeon.models.Cinema;

import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 23/3/2017.
 */

public class CinemaListAdapter extends BaseAdapter {
    private ArrayList<Cinema> cinemaList;
    private Cinema cinema;
    private Context mContext;

    public CinemaListAdapter(ArrayList<Cinema> cinemaList, Context context) {
        this.cinemaList = cinemaList;
        mContext = context;
    }
    @Override
    public int getCount() {
        int count = cinemaList.size();
        return count;
    }

    @Override
    public Cinema getItem(int position) {
        return cinemaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        cinema = cinemaList.get(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.cinema_list_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.cinema_name);
        name.setText(cinema.getName());
        TextView distance = (TextView) convertView.findViewById(R.id.cinema_distance);
        // TODO set distance
        distance.setText("Distance");
        return convertView;
    }
}
