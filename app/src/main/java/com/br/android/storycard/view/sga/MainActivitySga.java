package com.br.android.storycard.view.sga;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import com.br.android.storycard.R;
import com.br.android.storycard.view.sga.util.PrinterBluetooth;
import com.br.android.storycard.view.storycard.DetailFragment;

public class MainActivitySga extends AppCompatActivity
        implements KeyboardFragment.KeyboardFragmentListener,
                   LoginFragment.LoginFragmentListener,
                   MenuFragment.MenuFragmentListener {

    public static final String STORY_URI = "contact_uri";
    private KeyboardFragment keyboardFragment; // displays keyboard
    private LoginFragment loginFragment;
    private MenuFragment menuFragment;

    // display KeyboardFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sga);

        // create and display a KeyboardFragment
        if (savedInstanceState == null) {
            // create KeyboardFragment
            initFragments();
        }

    }

    private void initFragments() {
        keyboardFragment = new KeyboardFragment();
        loginFragment = new LoginFragment();

        // add the fragment to the FrameLayout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.keyboardFragment, keyboardFragment);
        transaction.add(R.id.topPaneContainer, loginFragment);
        transaction.commit();

    }

    @Override
    public void onLogin() {
            // removes top of back stack
            getSupportFragmentManager().popBackStack();
            displayMainMenu(R.id.topPaneContainer);
    }

    @Override
    public void onItemMenuSelected(Uri contactUri, Long rowId) {
        if (rowId == 3) {
            PrinterBluetooth printer = new PrinterBluetooth();
            printer.sendToPrint();
        } else {
            displayStory(contactUri, R.id.topPaneContainer);
        }
    }

    // display main menu
    private void displayMainMenu(int viewID) {
        //DoubleTapListViewFragment doubleTapListViewFragment = new DoubleTapListViewFragment();
        Editable v = loginFragment.getEditTextV().getText();
        Editable s = loginFragment.getEditTextS().getText();

        menuFragment = new MenuFragment();

        Bundle arguments = new Bundle();
        arguments.putString("v", String.valueOf(v));
        arguments.putString("s", String.valueOf(s));
        menuFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DoubleTapListViewFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, menuFragment);
        //transaction.replace(viewID, doubleTapListViewFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // display a contact
    private void displayStory(Uri contactUri, int viewID) {
        DetailFragmentSga detailFragmentSga = new DetailFragmentSga();

        // specify story's Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(STORY_URI, contactUri);
        detailFragmentSga.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragmentSga);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }
}
