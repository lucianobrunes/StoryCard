package com.br.android.storycard.view.sga;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.br.android.storycard.R;
import com.br.android.storycard.view.sga.util.PrinterBluetooth;
import com.br.android.storycard.view.storycard.AddEditFragment;

public class MainActivitySga extends AppCompatActivity
        implements KeyboardFragment.KeyboardFragmentListener,
                   LoginFragment.LoginFragmentListener,
                   MenuFragment.MenuFragmentListener,
                   AddEditFragment.AddEditFragmentListener {

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
    public void getDataInput(EditText editText, int primaryCode) {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.topPaneContainer);

        // Search and return de user login 9999
        EditText editTextV = (EditText) loginFragment.getActivity().findViewById(R.id.editTextV);
        int user = 0;
        if (editText.getText().length() == 4) {
            user = Integer.valueOf(editText.getText().toString());
        }

        int CodeEnter = 55002;
        int intName= editText.getId();
        String stringName= getResources().getResourceEntryName(intName);

        if (topFragment.getClass().equals(LoginFragment.class)) {
            if (primaryCode == CodeEnter) {
                 if ((editText.getText().length() == 6 && stringName.equals("editTextS") || user == 9999)) {
                    // removes top of back stack
                    getSupportFragmentManager().popBackStack();
                    displayMainMenu(R.id.topPaneContainer);
                }
            }
        }
    }

    /**
     * Get the primaryCode and define the enable rules of views of login fragment
     * @param primaryCode
     */
    @Override
    public void getPrimaryCode(int primaryCode) {
        int CodeEnter = 55002; // Enter Code
        int CodeClear = 55006; // Cancel Code
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.topPaneContainer);

        if (primaryCode == CodeEnter) {
            loginFragment.enableFields(true);
        } else { // Code Cancel
            loginFragment.enableFields(false);
            if (!topFragment.getClass().equals(LoginFragment.class)) {
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            } else {
            }
        }
    }

    @Override
    public void onItemMenuSelected(Uri contactUri, Long rowId) {
        if (rowId == 3) {
            PrinterBluetooth printer = new PrinterBluetooth();
            printer.sendToPrint();
        } else {
            if (rowId == 10) {
                this.finish();
            } else {
                displayItemMenu(contactUri, R.id.topPaneContainer);
            }
        }
    }

    // display main menu
    private void displayMainMenu(int viewID) {
        Editable v = loginFragment.getEditTextV().getText();
        Editable s = loginFragment.getEditTextS().getText();

        menuFragment = new MenuFragment();

        Bundle arguments = new Bundle();
        arguments.putString("v", String.valueOf(v));
        arguments.putString("s", String.valueOf(s));
        menuFragment.setArguments(arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, menuFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // display item menu
    private void displayItemMenu(Uri contactUri, int viewID) {
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

    // update GUI after new menu or updated menu saved
    @Override
    public void onAddEditCompleted(Uri storyUri) {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        menuFragment.updateMenuList(); // refresh menu
        displayMainMenu(R.id.topPaneContainer);
    }

    // display AddEditFragment to add a new story
    @Override
    public void onAddMenu() {
         displayAddEditFragment(R.id.topPaneContainer, null);
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


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
