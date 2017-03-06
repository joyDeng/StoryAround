package me.ddfw.storyaround;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import me.ddfw.storyaround.model.Story;
import me.ddfw.storyaround.model.User;

/**
 * Created by apple on 2017/2/27.
 */

public class StoryListAdapter extends ArrayAdapter<Story> {

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private Context context;
    private List<Story> data;

    public StoryListAdapter(Context context, List<Story> data){
        super(context,0,data);
        this.data = data;
        this.context = context;
    }

    @Override
    public void add(Story story){
        data.add(story);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        Story story = getItem(position);

        if(story == null)
            Log.d("msg", "story is null");

        final StoryListAdapter.ViewHolder viewHolder;
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


            viewHolder.dateText.setText(story.getStoryContent());

            String authorId = story.getStoryAuthorId();

            //according the author id, get user's name in user table
            database.child(User.USER_TABLE).child(authorId).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //if the user exists
                            if(dataSnapshot.exists()){

                                //get user name
                                String authorName = (String) dataSnapshot.child(User.KEY_USER_NAME).getValue();

                                //show the user name
                                viewHolder.user.setText(authorName);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            viewHolder.title.setText(story.getStoryTitle());

            double lat = story.getStoryLat();
            double lng = story.getStoryLng();

            LatLng location = new LatLng(lat, lng);

            viewHolder.location.setText(location.toString());

            //TODO: need to set the story type as string!!!!
            viewHolder.tag.setText(String.valueOf(story.getStoryType()));


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
