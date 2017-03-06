package me.ddfw.storyaround.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

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
        viewHolder.dateText = (TextView) v.findViewById(R.id.story_date) ;
        viewHolder.user = (TextView) v.findViewById(R.id.story_user) ;
        viewHolder.title = (TextView) v.findViewById(R.id.story_title) ;
        viewHolder.location = (TextView) v.findViewById(R.id.story_location) ;
        viewHolder.tag = (TextView) v.findViewById(R.id.story_tag) ;
        viewHolder.content = (WebView) v.findViewById(R.id.story_content) ;

        viewHolder.dateText.setText(story.getStoryContent());
//        viewHolder.user.setText(story.getUserName());
        viewHolder.title.setText(story.getStoryTitle());
//        viewHolder.location.setText(story.getLocation().toString());
        viewHolder.tag.setText(story.getStoryType());


        builder.setView(v);

        String url = "http://cs.dartmouth.edu/~wzl/projectWebpage";
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
        return builder.create();


    }

    private static class ViewHolder {
        TextView dateText;
        TextView user;
        TextView title;
        TextView location;
        TextView tag;
        WebView content;
    }
}
