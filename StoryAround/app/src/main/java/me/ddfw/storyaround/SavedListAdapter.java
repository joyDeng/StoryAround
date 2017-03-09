package me.ddfw.storyaround;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.ddfw.storyaround.fragments.PostFragment;

/**
 * Created by apple on 2017/3/8.
 */

public class SavedListAdapter extends ArrayAdapter<String> {
    private List<String> data;
    private Context context;
    private SharedPreferences mprefs;
    private SharedPreferences.Editor meditor;


    public SavedListAdapter(Context context, List<String> data){
        super(context,R.layout.list_item_saved,data);
        this.data = data;
        this.context = context;
        mprefs = context.getSharedPreferences(PostFragment.PREF_KEY, Context.MODE_PRIVATE);
        meditor = mprefs.edit();
    }

    @Override
    public void add(String string){
        data.add(string);
        notifyDataSetChanged();
    }

    @Override
    public String getItem(int i){
        return data.get(i);
    }

    @Override
    public int getCount(){
        return data.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String s = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_saved, parent, false);
        }
        TextView addr = (TextView) convertView.findViewById(R.id.addr);
        addr.setText(s);





        return convertView;
    }


}
