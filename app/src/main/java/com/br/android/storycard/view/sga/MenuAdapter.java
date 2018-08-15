package com.br.android.storycard.view.sga;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.br.android.storycard.data.DatabaseDescription.Story;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

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

            // attach listener to itemView
/*            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        // executes when the contact in this ViewHolder is clicked
                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(Story.buildStoryUri(rowID));
                        }
                    }
            );*/
        }

        // set the database row ID for the contact in this ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }

    }

    // StoriesAdapter instance variables
    private Cursor cursor = null;
   // public final MenuClickListener clickListener;

    // constructor
/*    public MenuAdapter(MenuAdapter.MenuClickListener clickListener) {
        this.clickListener = clickListener;
    }*/

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
        cursor.moveToPosition(position);
        Long idItem = cursor.getLong(cursor.getColumnIndex(Story._ID));
        holder.setRowID(idItem);
        String nameItem = cursor.getString(cursor.getColumnIndex(Story.COLUMN_TITULO));
        holder.textView.setText(idItem.toString() + " - " + nameItem);
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // swap this adapter's current Cursor for a new one
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }



}
