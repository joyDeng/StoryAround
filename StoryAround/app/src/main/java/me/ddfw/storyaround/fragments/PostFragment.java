package me.ddfw.storyaround.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TextView;

import jp.wasabeef.richeditor.RichEditor;
import me.ddfw.storyaround.R;

public class PostFragment extends Fragment{

    private RichEditor mEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        mEditor = (RichEditor) rootView.findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(getResources().getColor(R.color.color3));
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Tell us your story...");
        mEditor.setBackgroundColor(Color.TRANSPARENT);

        editorSetListener(rootView);

        TableLayout tabLayout= (TableLayout ) rootView.findViewById(R.id.tab);
        //tabLayout.setVisibility(View.GONE);

        return rootView;
    }


    private void editorSetListener(View view) {

        view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        view.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        view.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        view.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        view.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        view.findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        view.findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        view.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        view.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        view.findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        view.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        view.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        view.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        view.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        view.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        view.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        view.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");
            }
        });

        view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        view.findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }





}
