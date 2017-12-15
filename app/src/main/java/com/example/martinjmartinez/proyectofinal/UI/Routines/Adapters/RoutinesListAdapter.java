package com.example.martinjmartinez.proyectofinal.UI.Routines.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.martinjmartinez.proyectofinal.Entities.Routine;
import com.example.martinjmartinez.proyectofinal.R;
import com.example.martinjmartinez.proyectofinal.Services.RoutineService;
import com.example.martinjmartinez.proyectofinal.Utils.DateUtils;
import com.example.martinjmartinez.proyectofinal.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class RoutinesListAdapter extends ArrayAdapter<Routine> {
    private List<Routine> routines;
    Context mContext;
    RoutineService routineService;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView startTime;
        TextView endTime;
        TextView days;
        TextView divider;
        Switch enabled;

    }

    public RoutinesListAdapter(List<Routine> data, Context context) {
        super(context, R.layout.routine_list_item, data);
        this.routines = data;
        this.mContext=context;
        routineService = new RoutineService(Realm.getDefaultInstance());

    }

    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Routine dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.routine_list_item, parent, false);
            viewHolder.name =  convertView.findViewById(R.id.routine_name);
            viewHolder.startTime =  convertView.findViewById(R.id.routine_start_time);
            viewHolder.endTime =  convertView.findViewById(R.id.routine_end_time);
            viewHolder.days =  convertView.findViewById(R.id.routine_week_days);
            viewHolder.divider = convertView.findViewById(R.id.divider_times);
            viewHolder.enabled =  convertView.findViewById(R.id.routine_enable);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.name.setText(dataModel.getName());
        viewHolder.startTime.setText(DateUtils.getTime(dataModel.getStartTime()));
        if (dataModel.getEndTime() == null || dataModel.getEndTime().isEmpty()){
            viewHolder.divider.setVisibility(View.GONE);
        } else{
            viewHolder.divider.setVisibility(View.VISIBLE);
            viewHolder.endTime.setText(DateUtils.getTime(dataModel.getEndTime()));
        }

        viewHolder.days.setText(Utils.formatRoutineDays(dataModel.getDayOfWeek()));
        viewHolder.enabled.setChecked(dataModel.isEnable());

        viewHolder.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                routineService.updateIsEnable(dataModel.get_id(), b);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
