package com.br.android.storycard.view.sga;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription;

public class DetailFragmentSga extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STORY_LOADER = 0; // identifies the Loader

    private Uri storyUri; // Uri of selected story

    private TextView tituloTextView; // displays story's titulo
    private TextView interessadoTextView; // displays story's interessado
    private TextView necessidadeTextView; // displays story's necessidade
    private TextView motivoTextView; // displays story's motivo

    // set DetailFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // remove DetailFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
    }

    // called when DetailFragmentListener's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // this fragment has menu items to display

        // get Bundle of arguments then extract the contact's Uri
        Bundle arguments = getArguments();

        if (arguments != null)
            storyUri = arguments.getParcelable(MainActivitySga.STORY_URI);

        // inflate DetailFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_detail, container, false);

        // get the EditTexts
        tituloTextView = (TextView) view.findViewById(R.id.tituloTextView);
        interessadoTextView = (TextView) view.findViewById(R.id.interessadoTextView);
        necessidadeTextView = (TextView) view.findViewById(R.id.necessidadeTextView);
        motivoTextView = (TextView) view.findViewById(R.id.motivoTextView);

        // load the contact
        getLoaderManager().initLoader(STORY_LOADER, null, this);
        return view;
    }


    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        CursorLoader cursorLoader;

        switch (id) {
            case STORY_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        storyUri, // Uri of story to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the story exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int tituloIndex = data.getColumnIndex(DatabaseDescription.Story.COLUMN_TITULO);
            int interessadoIndex = data.getColumnIndex(DatabaseDescription.Story.COLUMN_INTERESSADO);
            int necessidadeIndex = data.getColumnIndex(DatabaseDescription.Story.COLUMN_NECESSIDADE);
            int motivoIndex = data.getColumnIndex(DatabaseDescription.Story.COLUMN_MOTIVO);


            // fill TextViews with the retrieved data
            tituloTextView.setText(data.getString(tituloIndex));
            interessadoTextView.setText(data.getString(interessadoIndex));
            necessidadeTextView.setText(data.getString(necessidadeIndex));
            motivoTextView.setText(data.getString(motivoIndex));
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

}
