package com.br.android.storycard.view.sga;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription.Story;

import java.util.Arrays;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private int user;
    private List<String> menuItems999 = null;

    /**
     * Check if the user is 9999. If true go to different menu
     * @param context
     * @param user
     */
    public MenuAdapter(Context context, int user) {
        this.user = user;
        if (getUser() == 9999) {
            menuItems999= Arrays.asList(context.getResources().getStringArray(R.array.main_menu9999));
        }
    }

    // interface implemented by MenuFragment to respond
    // when the user touches an item in the RecyclerView
/*    public interface MenuClickListener {
        void onClick(Uri contactUri, Long rowId);
    }*/

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;
        private long rowID;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }

        // set the database row ID for the contact in this ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }

    }

    // StoriesAdapter instance variables
    private Cursor cursor = null;

    // sets up new list item and its ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the android.R.layout.simple_list_item_1 layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        return new MenuAdapter.ViewHolder(view); // return current item's ViewHolder
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        if (getUser() != 9999) {
            cursor.moveToPosition(position);
            Long idItem = cursor.getLong(cursor.getColumnIndex(Story._ID));
            holder.setRowID(idItem);
            String nameItem = cursor.getString(cursor.getColumnIndex(Story.COLUMN_TITULO));
            holder.textView.setText(idItem.toString() + " - " + nameItem);
        } else {
            final String item = menuItems999.get(position);
            holder.textView.setText(item.toString());
        }
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        if (getUser() != 9999) {
            return (cursor != null) ? cursor.getCount() : 0;
        } else {
            return menuItems999.size();
        }
    }

    // swap this adapter's current Cursor for a new one
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
