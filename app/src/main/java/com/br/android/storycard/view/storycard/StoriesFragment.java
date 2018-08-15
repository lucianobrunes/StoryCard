package com.br.android.storycard.view.storycard;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription;
import com.br.android.storycard.data.DatabaseDescription.Story;
import com.br.android.storycard.view.util.ItemClickSupport;

public class StoriesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // callback method implemented by MainActivity
    public interface StoriesFragmentListener {
        // called when story selected
        void onStorySelected(Uri storyUri);

        // called when add button is pressed
        void onAddStory();
    }

    private static final int STORIES_LOADER = 0; // identifies Loader

    // used to inform the MainActivity when a story is selected
    private StoriesFragmentListener listener;

    private StoriesAdapter storiesAdapter; // adapter for recyclerView

    // configures this fragment's GUI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // inflate GUI and get reference to the RecyclerView
        View view = inflater.inflate(R.layout.fragment_stories, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView should display items in a vertical list
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                1, GridLayoutManager.VERTICAL, false));

        // create recyclerView's adapter and item click listener
        storiesAdapter = new StoriesAdapter(
                new StoriesAdapter.StoryClickListener() {
                    @Override
                    public void onClick(Uri storyUri) {
                        listener.onStorySelected(storyUri);
                    }
                }
        );
        recyclerView.setAdapter(storiesAdapter); // set the adapter

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("ITEM CLICK", "Item single clicked " + storiesAdapter.getItemCount());
                    }

                    @Override
                    public void onItemDoubleClicked(RecyclerView recyclerView, int position, View v) {
                        long rowId = position;
                        if (rowId <= 0) {
                            rowId = 1;
                        } else {
                            rowId = position + 1;
                        }
                        storiesAdapter.clickListener.onClick(DatabaseDescription.Story.buildStoryUri(rowId));
                        Log.d("ITEM CLICK", "Item double clicked " + storiesAdapter.getItemCount());
                    }
                });

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes
        recyclerView.setHasFixedSize(true);

        // get the FloatingActionButton and configure its listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // displays the AddEditFragment when FAB is touched
                    // displays the AddEditFragment when FAB is touched
                    @Override
                    public void onClick(View view) {
                        listener.onAddStory();
                    }
                }
        );

        return view;
    }

    // set StoriesFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (StoriesFragmentListener) context;
    }

    // remove StoriesFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // initialize a Loader when this fragment's activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STORIES_LOADER, null, this);
    }

    // called from MainActivity when other Fragment's update database
    public void updateStoriesList() {
        storiesAdapter.notifyDataSetChanged();
    }

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case STORIES_LOADER:
                return new CursorLoader(getActivity(),
                         Story.CONTENT_URI, // Uri of stories table
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        Story.COLUMN_TITULO + " COLLATE NOCASE ASC"); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        storiesAdapter.swapCursor(data);
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        storiesAdapter.swapCursor(null);
    }


}
