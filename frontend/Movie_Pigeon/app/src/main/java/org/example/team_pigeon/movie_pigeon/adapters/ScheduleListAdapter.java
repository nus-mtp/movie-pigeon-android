package org.example.team_pigeon.movie_pigeon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.example.team_pigeon.movie_pigeon.R;
import org.example.team_pigeon.movie_pigeon.costomizedViews.MultipleColGridView;
import org.example.team_pigeon.movie_pigeon.models.Schedule;

import java.util.ArrayList;
import java.util.TreeSet;

public class ScheduleListAdapter extends BaseAdapter {
    private static final int TYPE_SCHEDULE = 0;
    private static final int TYPE_CINEMA = 1;
    private ArrayList<Schedule> scheduleList;
    private TreeSet cinemaSet = new TreeSet();
    private Context mContext;
    private Schedule schedule;

    public ScheduleListAdapter(ArrayList<Schedule> scheduleList, Context mContext) {
        this.mContext = mContext;
        this.scheduleList = scheduleList;
    }

    @Override
    public int getItemViewType(int position) {
        return cinemaSet.contains(position) ? TYPE_CINEMA : TYPE_SCHEDULE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addCinemaToAdapter(final Schedule cinema) {
        scheduleList.add(cinema);
        cinemaSet.add(scheduleList.size() - 1);
        this.notifyDataSetChanged();
    }


    public void addScheduleItemToAdapter(Schedule schedule) {
        scheduleList.add(schedule);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Schedule getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        schedule = scheduleList.get(position);
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TYPE_SCHEDULE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.type_item_schedule, null);
                    viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_type_schedule);
                    viewHolder.grid = (MultipleColGridView) convertView.findViewById(R.id.grid_showtime_schedule);
                    break;
                case TYPE_CINEMA:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.cinema_item_schedule, null);
                    viewHolder.txt_title = (TextView) convertView.findViewById(R.id.text_cinema_name);
                    break;
            }
            convertView.setClickable(false);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch (type) {
            case TYPE_SCHEDULE:
                if(!schedule.getType().equals("")) {
                    viewHolder.txt_title.setText(schedule.getType());
                }
                else{
                    viewHolder.txt_title.setText("2D");
                }
                viewHolder.grid.setAdapter(new ArrayAdapter<>(this.mContext, R.layout.now_showing_schedule_cell, schedule.getShowTimeArray()));
                break;
            case TYPE_CINEMA:
                viewHolder.txt_title.setText(schedule.getCinemaName());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        MultipleColGridView grid;
    }
}
