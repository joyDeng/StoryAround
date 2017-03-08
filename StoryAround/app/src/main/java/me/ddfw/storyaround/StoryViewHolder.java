package me.ddfw.storyaround;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.model.Story;

/**
 * Created by xinbeifu on 3/8/17.
 */

public class StoryViewHolder extends RecyclerView.ViewHolder {

    public TextView dateView;
    public TextView authorView;
    public TextView titleView;
    public TextView locationView;
    public TextView tagView;

    public StoryViewHolder(View itemView){
        super(itemView);

        dateView = (TextView) itemView.findViewById(R.id.story_date);
        authorView = (TextView) itemView.findViewById(R.id.story_user);
        titleView = (TextView) itemView.findViewById(R.id.story_title);
        locationView = (TextView) itemView.findViewById(R.id.story_location);
        tagView = (TextView) itemView.findViewById(R.id.story_tag);
    }

    public void bindToStory(Story story, View.OnClickListener startClickListener) {

        Date date = new Date(story.getStoryDateTime());

        String pattern = "mmm-dd";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String dateString = simpleDateFormat.format(date);

        dateView.setText(dateString);
        authorView.setText(story.getStoryAuthorId());
        titleView.setText(story.getStoryTitle());

        locationView.setText("location");


//        try{
//            Geocoder geocoder = new Geocoder(, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(story.getStoryLat(), story.getStoryLng(), 1);
//            Address address = addresses.get(0);
//            String line = "";
//            for(int c=0; c<address.getMaxAddressLineIndex(); c++){
//                line += address.getAddressLine(c)+",";
//            }
//            line.substring(0,line.length()-1);
//            locationView.setText(String.valueOf(line));
//        }catch (IOException e){}

    }
}
