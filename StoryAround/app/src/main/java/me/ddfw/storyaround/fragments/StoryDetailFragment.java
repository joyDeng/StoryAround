package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.ddfw.storyaround.R;
import me.ddfw.storyaround.model.Story;

/**
 * Created by apple on 2017/2/27.
 */

public class StoryDetailFragment extends DialogFragment {
    //public static final int DETAILS_DIALOG = 1;
    private static final String STORY_DATA_KEY = "story";
    private Story story;



    public static StoryDetailFragment buildDialog(Story story){
        StoryDetailFragment dialog = new StoryDetailFragment();
        Bundle args = new Bundle();
        //args.putInt(DIALOG_ID_KEY, dialog_id);
        args.putParcelable(STORY_DATA_KEY, story);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Story story = getArguments().getParcelable(STORY_DATA_KEY);
        final Activity parent = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        LayoutInflater i = parent.getLayoutInflater();
        View v;
        ViewHolder viewHolder = new ViewHolder();
        final TextView text;
        v = i.inflate(R.layout.dialog_detail_story,null);
        viewHolder.user = (TextView) v.findViewById(R.id.story_user) ;
        viewHolder.title = (TextView) v.findViewById(R.id.story_title) ;
        viewHolder.location = (TextView) v.findViewById(R.id.story_location) ;
        viewHolder.tag = (TextView) v.findViewById(R.id.story_tag) ;
        viewHolder.content = (TextView) v.findViewById(R.id.story_content) ;
        viewHolder.like = (TextView) v.findViewById(R.id.story_like);

        try{
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(story.getStoryLat(), story.getStoryLng(), 1);
            Address address = addresses.get(0);
            String line=address.getAddressLine(0);
            viewHolder.location.setText(String.valueOf(line));
        }catch (IOException e){

        }


        //viewHolder.user.setText(story.);
        viewHolder.title.setText(story.getStoryTitle());
        viewHolder.user.setText(story.getStoryAuthorId());
        viewHolder.like.setText(String.valueOf(story.getStoryLikes()));
        //viewHolder.location.setText(String.valueOf(story.getStoryLat()));
        viewHolder.tag.setText(story.getStoryType()+"");
        viewHolder.content.setText(story.getStoryContent());

        builder.setView(v);

        /*String url = "http://cs.dartmouth.edu/~wzl/projectWebpage";
        viewHolder.content .setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                //System.out.println("hello");
                return true;
            }
        });
        viewHolder.content.loadUrl(url);
        */
        return builder.create();


    }

    private static class ViewHolder {
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
        TextView content;
        TextView like;
    }
}
