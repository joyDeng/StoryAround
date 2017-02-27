package me.ddfw.storyaround;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.ddfw.storyaround.model.Story;

/**
 * Created by apple on 2017/2/27.
 */

public class StoryListAdapter extends ArrayAdapter<Story> {
    private Context context;
    private List<Story> data;

    public StoryListAdapter(Context context, List<Story> data){
        super(context,0,data);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        Story story = getItem(position);
        StoryListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new StoryListAdapter.ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_story, parent, false);
            // binding view parts to view holder
            viewHolder.dateText = (TextView) convertView.findViewById(R.id.story_date) ;
            viewHolder.user = (TextView) convertView.findViewById(R.id.story_user) ;
            viewHolder.title = (TextView) convertView.findViewById(R.id.story_title) ;
            viewHolder.location = (TextView) convertView.findViewById(R.id.story_location) ;
            viewHolder.tag = (TextView) convertView.findViewById(R.id.story_tag) ;

            viewHolder.dateText.setText(story.getFormattedDate());
            viewHolder.user.setText(story.getUserName());
            viewHolder.title.setText(story.getTitle());
            viewHolder.location.setText(story.getLocation().toString());
            viewHolder.tag.setText(story.getTag());

        }
        return convertView;
    }

    private static class ViewHolder {
        TextView dateText;
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
    }


}
