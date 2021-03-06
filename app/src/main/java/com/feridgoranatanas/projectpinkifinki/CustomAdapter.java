package com.feridgoranatanas.projectpinkifinki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ferid on 02-Sep-16.
 */
public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<Run> runs;
    private static LayoutInflater layoutInflater = null;

    public CustomAdapter(Context context, List<Run> runs) {
        this.context = context;
        this.runs = runs;
        layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return runs.size();
    }

    @Override
    public Object getItem(int position) {
        return runs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return runs.size() - 1 - position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = layoutInflater.inflate(R.layout.list_item, null);
        Run currentRun = runs.get(position);

        TextView tvDate = (TextView)listItem.findViewById(R.id.tvDate);
        tvDate.setText(currentRun.getDate());
        TextView tvStats = (TextView)listItem.findViewById(R.id.tvStats);
        tvStats.setText(String.format("%.2fkm in %s.", currentRun.getDistance(), getCurrentTimeString(currentRun.getSeconds())));

        return listItem;
    }


    private String getCurrentTimeString(int currentTime) {
        int hours = currentTime / 3600;
        int minutes = (currentTime % 3600) / 60;
        int seconds = currentTime % 60;

        StringBuilder stringBuilder = new StringBuilder()
                .append((hours != 0) ? hours : "")
                .append((hours != 0) ? "h:" : "")
                .append((minutes != 0) ? minutes : "")
                .append((minutes != 0) ? "m: " : "")
                .append(seconds)
                .append("s");

        return stringBuilder.toString();
    }
}
