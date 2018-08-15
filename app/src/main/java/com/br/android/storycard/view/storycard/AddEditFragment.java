// AddEditFragment.java
// Fragment for adding a new story or editing an existing one

package com.br.android.storycard.view.storycard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription.Story;


public class AddEditFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // defines callback method implemented by MainActivity
    public interface AddEditFragmentListener {
        // called when story is saved
        void onAddEditCompleted(Uri contactUri);
    }

    // constant used to identify the Loader
    private static final int STORY_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri storyUri; // Uri of selected story
    private boolean addingNewStory = true; // adding (true) or editing

    // EditTexts for contact information
    private TextInputLayout tituloTextInputLayout;
    private TextInputLayout interessadoTextInputLayout;
    private TextInputLayout necessidadeTextInputLayout;
    private TextInputLayout motivoTextInputLayout;
    private FloatingActionButton saveStoryFAB;


    private CoordinatorLayout coordinatorLayout; // used with SnackBars

    // set AddEditFragmentListener when Fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // remove AddEditFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display

        // inflate GUI and get references to EditTexts
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        tituloTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.tituloTextInputLayout);
        tituloTextInputLayout.getEditText().addTextChangedListener(
                tituloChangedListener);
        interessadoTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.interessadoTextInputLayout);
        necessidadeTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.necessidadeTextInputLayout);
        motivoTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.motivoTextInputLayout);
        // set FloatingActionButton's event listener
        saveStoryFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveStoryFAB.setOnClickListener(saveStoryButtonClicked);
        updateSaveButtonFAB();

        // used to display SnackBars with brief messages
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null if creating new contact

        if (arguments != null) {
            addingNewStory = false;
            storyUri = arguments.getParcelable(MainActivity.STORY_URI);
        }

        // if editing an existing contact, create Loader to get the contact
        if (storyUri != null)
            getLoaderManager().initLoader(STORY_LOADER, null, this);

        return view;
    }

    // detects when the text in the tituloTextInputLayout's EditText changes
    // to hide or show saveButtonFAB
    private final TextWatcher tituloChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {}

        // called when the text in nameTextInputLayout changes
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    // shows saveButtonFAB only if the name is not empty
    private void updateSaveButtonFAB() {
        String input =
                tituloTextInputLayout.getEditText().getText().toString();

        // if there is a name for the contact, show the FloatingActionButton
        if (input.trim().length() != 0)
            saveStoryFAB.show();
        else
            saveStoryFAB.hide();
    }

    // responds to event generated when user saves a story
    private final View.OnClickListener saveStoryButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the virtual keyboard
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveStory(); // save story to the database
                }
            };

    // saves story information to the database
    private void saveStory() {
        // create ContentValues object containing story's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Story.COLUMN_TITULO,
                tituloTextInputLayout.getEditText().getText().toString());
        contentValues.put(Story.COLUMN_INTERESSADO,
                interessadoTextInputLayout.getEditText().getText().toString());
        contentValues.put(Story.COLUMN_NECESSIDADE,
                necessidadeTextInputLayout.getEditText().getText().toString());
        contentValues.put(Story.COLUMN_MOTIVO,
                motivoTextInputLayout.getEditText().getText().toString());
        if (addingNewStory) {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            Uri newStoryUri = getActivity().getContentResolver().insert(
                    Story.CONTENT_URI, contentValues);

            if (newStoryUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.story_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newStoryUri);
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.story_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // use Activity's ContentResolver to invoke
            // insert on the StoryCardContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    storyUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(storyUri);
                Snackbar.make(coordinatorLayout,
                        R.string.story_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.story_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case STORY_LOADER:
                return new CursorLoader(getActivity(),
                        storyUri, // Uri of story to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the contact exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int tituloIndex = data.getColumnIndex(Story.COLUMN_TITULO);
            int interessadoIndex = data.getColumnIndex(Story.COLUMN_INTERESSADO);
            int necessidadeIndex = data.getColumnIndex(Story.COLUMN_NECESSIDADE);
            int motivoIndex = data.getColumnIndex(Story.COLUMN_MOTIVO);

            // fill EditTexts with the retrieved data
            tituloTextInputLayout.getEditText().setText(
                    data.getString(tituloIndex));
            interessadoTextInputLayout.getEditText().setText(
                    data.getString(interessadoIndex));
            necessidadeTextInputLayout.getEditText().setText(
                    data.getString(necessidadeIndex));
            motivoTextInputLayout.getEditText().setText(
                    data.getString(motivoIndex));
            updateSaveButtonFAB();
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
