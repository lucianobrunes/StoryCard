package com.br.android.storycard.view.sga;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription;
import com.br.android.storycard.data.DatabaseDescription.Story;
import com.br.android.storycard.view.util.ItemClickSupport;

public class MenuFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STORIES_LOADER = 0; // identifies Loader
    private static final String HEADER_TERMINAL = "Terminal";
    private int user;

    // callback method implemented by MainActivity
    public interface MenuFragmentListener {
        // called when story selected
        void onItemMenuSelected(Uri storyUri, Long rowId);
        void onAddMenu();
    }

    // used to inform the MainActivity when um item menu is selected
    private MenuFragment.MenuFragmentListener listener;

    private MenuAdapter menuAdapter; // adapter for recyclerView

    // configures this fragment's GUI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        String user =  getArguments().getString("v");
        setUser(Integer.valueOf(user));

        // inflate GUI and get reference to the RecyclerView
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMenu);

        // recyclerView should display items in a vertical list
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        // create recyclerView's adapter and item click listener
        menuAdapter = new MenuAdapter(getContext(), getUser());
        recyclerView.setAdapter(menuAdapter); // set the adapter


        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("ITEM CLICK", "Item single clicked " + menuAdapter.getItemCount());
                    }

                    @Override
                    public void onItemDoubleClicked(RecyclerView recyclerView, int position, View v) {
                        long rowId = position;
                        if (rowId <= 0) {
                            rowId = 1;
                        } else {
                            rowId = position + 1;
                        }
                        listener.onItemMenuSelected(DatabaseDescription.Story.buildStoryUri(rowId), rowId);
                        //menuAdapter.clickListener.onClick(DatabaseDescription.Story.buildStoryUri(rowId));
                        Log.d("ITEM CLICK", "Item double clicked " + menuAdapter.getItemCount());
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
                        listener.onAddMenu();
                    }
                }
        );

        return view;
    }

    // set StoriesFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (MenuFragment.MenuFragmentListener) context;
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

        /**
         * Setup the header after enter
         */
        TextView textViewHeader = (TextView) getActivity().findViewById(R.id.textViewHeader);
        String header="";

        if (getUser() == 9999) {
            header = HEADER_TERMINAL;
        } else {
            if (getUser() == 0) {
                header = String.format("%04d", getUser());
                header = header + " " + header;
                header = header + "/" + String.format("%04d", getUser());
            } else {
                header = String.valueOf(getUser());
            }
        }
        textViewHeader.setText(header);
    }


    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case STORIES_LOADER:
                return new CursorLoader(getActivity(),
                        DatabaseDescription.Story.CONTENT_URI, // Uri of stories table
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        DatabaseDescription.Story.COLUMN_TITULO + " COLLATE NOCASE ASC"); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        menuAdapter.swapCursor(data);
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        menuAdapter.swapCursor(null);
    }

    // called from MainActivity when other Fragment's update database
    public void updateMenuList() {
        menuAdapter.notifyDataSetChanged();
    }

    public MenuFragmentListener getListener() {
        return listener;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
