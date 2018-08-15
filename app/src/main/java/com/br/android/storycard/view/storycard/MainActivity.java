package com.br.android.storycard.view.storycard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.br.android.storycard.R;

public class MainActivity extends AppCompatActivity
        implements StoriesFragment.StoriesFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener{

    // key for storing a story's Uri in a Bundle passed to a fragment
    public static final String STORY_URI = "contact_uri";
    private StoriesFragment storiesFragment; // displays stories list



    // display StoriesFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if layout contains fragmentContainer, the phone layout is in use;
        // create and display a StoriesFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create StoriesFragment
            storiesFragment = new StoriesFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, storiesFragment);
            transaction.commit(); // display StoriesFragment
        }
        else {
            storiesFragment =
                    (StoriesFragment) getSupportFragmentManager().
                            findFragmentById(R.id.storiesFragment);
        }
    }

    @Override
    public void onStorySelected(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayStory(contactUri, R.id.fragmentContainer);
        else { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            displayStory(contactUri, R.id.rightPaneContainer);
        }
    }

    // display a contact
    private void displayStory(Uri contactUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify story's Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(STORY_URI, contactUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }


    // display AddEditFragment to add a new story
    @Override
    public void onAddStory() {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // display fragment for adding a new or editing an existing story
    private void displayAddEditFragment(int viewID, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing story, provide storyUri as an argument
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(STORY_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // return to story list when displayed contact deleted
    @Override
    public void onStoryDeleted() {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        storiesFragment.updateStoriesList(); // refresh stories
    }

    // display the AddEditFragment to edit an existing story
    @Override
    public void onEditStory(Uri storyUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, storyUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, storyUri);
    }

    // update GUI after new story or updated story saved
    @Override
    public void onAddEditCompleted(Uri storyUri) {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        storiesFragment.updateStoriesList(); // refresh stories

        if (findViewById(R.id.fragmentContainer) == null) { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            // on tablet, display contact that was just added or edited
            displayStory(storyUri, R.id.rightPaneContainer);
        }
    }
}
