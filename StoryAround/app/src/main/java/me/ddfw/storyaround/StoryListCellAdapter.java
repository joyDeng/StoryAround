package me.ddfw.storyaround;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

import me.ddfw.storyaround.model.Story;

/**
 * Created by apple on 2017/2/27.
 */

public class StoryListCellAdapter extends ArrayAdapter<Story> {


    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;


    public StoryListCellAdapter(Context context, List<Story> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        Story story = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_item_story, parent, false);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((FoldingCell)view).toggle(false);
                }
            });
            // binding view parts to view holder
            viewHolder.dateText = (TextView) cell.findViewById(R.id.story_date) ;
            viewHolder.user = (TextView) cell.findViewById(R.id.story_user) ;
            viewHolder.titile = (TextView) cell.findViewById(R.id.story_title) ;
            viewHolder.location = (TextView) cell.findViewById(R.id.story_location) ;
            viewHolder.tag = (TextView) cell.findViewById(R.id.story_tag) ;
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        // bind data from selected element to view through view holder
//
//        viewHolder.dateText.setText(story.getFormattedDate());
//        viewHolder.user.setText(story.getUserName());
//        viewHolder.titile.setText(story.getTitle());
//        viewHolder.location.setText(story.getLocation().toString());
//        viewHolder.tag.setText(story.getTag());


        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }


    // View lookup cache
    private static class ViewHolder {
        TextView dateText;
        TextView user;
        TextView titile;
        TextView location;
        TextView tag;
    }

}
