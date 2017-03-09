package me.ddfw.storyaround;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.ddfw.storyaround.model.Story;
import me.ddfw.storyaround.model.User;



public class StoryListAdapter extends ArrayAdapter<Story> {
    private String DATE_FORMAT = "MMM/dd";
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private Context context;
    private List<Story> data;


    // Some basic setup and override functions
    public StoryListAdapter(Context context, List<Story> data){
        super(context,R.layout.list_item_story,data);
        this.data = data;
        this.context = context;
    }

    @Override
    public void add(Story story){
        data.add(story);
        notifyDataSetChanged();
    }

    @Override
    public Story getItem(int i){
        return data.get(i);
    }

    @Override
    public int getCount(){
        return data.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item from data list
        Story story = getItem(position);

        if(story == null)
            Log.d("msg", "story is null");

        // find view of this list item
        final StoryListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new StoryListAdapter.ViewHolder();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_story, parent, false);
            // binding view parts to view holder
            viewHolder.dateText = (TextView) convertView.findViewById(R.id.story_date);
            viewHolder.user = (TextView) convertView.findViewById(R.id.story_user);
            viewHolder.title = (TextView) convertView.findViewById(R.id.story_title);
            viewHolder.location = (TextView) convertView.findViewById(R.id.story_location);
            viewHolder.tag = (TextView) convertView.findViewById(R.id.story_tag);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        // set view content for list item
        String authorId = story.getStoryAuthorId();

        //according the author id, get user's name in user table
        database.child(User.USER_TABLE).child(authorId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // check all likes in database
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            //get user name
                            String authorName = (String) dataSnapshot.child
                                    (User.KEY_USER_NAME).getValue();
                            //show the user name
                            viewHolder.user.setText(authorName);
                            Log.d("debug", "" + authorName);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        viewHolder.title.setText(story.getStoryTitle());
        viewHolder.location.setText(story.getStoryAddress());
        viewHolder.tag.setText("# " + Global.STORY_TYPE[story.getStoryType()]);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        viewHolder.dateText.setText(dateFormat.format(new Date(story.getStoryDateTime())));
        viewHolder.location.setText(story.getStoryAddress());

        return convertView;
    }

    // view holder for this list item
    private class ViewHolder {
        TextView dateText;
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
    }


}
