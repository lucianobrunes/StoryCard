package com.br.android.storycard.view.sga;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription;
import com.br.android.storycard.view.sga.util.PrinterBluetooth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class KeyboardFragment extends Fragment {

    private KeyboardView mKeyboardView;
    public static final String STORY_URI = "contact_uri";
    private MenuFragment.MenuFragmentListener menuListener;

    // callback method implemented by MainActivity
    public interface KeyboardFragmentListener {
        void onLogin();
    }

    // used to inform the MainActivity when a story is selected
    private KeyboardFragment.KeyboardFragmentListener listener;

    // configures this fragment's GUI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu items to display
        View view = inflater.inflate(R.layout.keyboard_fragment, container, false);
        return view;
    }

    // set KeyboardFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (KeyboardFragment.KeyboardFragmentListener) context;
    }

    // remove KeyboardFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mKeyboardView= (KeyboardView) getActivity().findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(new Keyboard(getActivity(), R.xml.hexkbd));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        super.onActivityCreated(savedInstanceState);

    }

    /** The key (code) handler. */
    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener;

    {
        mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {


            public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
            public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
            public final static int CodePrev = 55000;
            public final static int CodeAllLeft = 55001;
            public final static int CodeEnter = 55002;
            public final static int CodeRight = 55003;
            public final static int CodeAllRight = 55004;
            public final static int CodeNext = 55005;
            public final static int CodeClear = 55006;

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                /*
                Check which fragment is on the top
                 */
                Fragment topFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.topPaneContainer);
                if (topFragment.getClass().equals(LoginFragment.class)) {
                    inputData(primaryCode);
                } else {
                    if (topFragment.getClass().equals(MenuFragment.class)) {
                            shortcutKey(topFragment, primaryCode, keyCodes);
                            return;
                    } else {
                        if (keyCodes[0] == 55006 ) {
                            getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                            getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                        }
                    }

                }
            }

            /**
             * Method for input data across the keyboard
             * @param primaryCode
             */
            private void inputData(int primaryCode) {
                View focusCurrent = getActivity().getWindow().getCurrentFocus();
                if (focusCurrent == null || focusCurrent.getClass() != android.support.v7.widget.AppCompatEditText.class) return;
                EditText edittext = (EditText) focusCurrent;
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();
                // Apply the key to the edittext
                if (primaryCode == CodeCancel) {
                    hideCustomKeyboard();
                } else if (primaryCode == CodeDelete) {
                    if (editable != null && start > 0) editable.delete(start - 1, start);
                } else if (primaryCode == CodeClear) {
                    if (editable != null) editable.clear();
                } else if (primaryCode == CodeEnter) {
                    if (start > 0) {
                        goToNextField(CodeEnter, edittext, View.FOCUS_FORWARD);
                        edittext.setSelection(start - 1);
                    }
                } else if (primaryCode == CodeRight) {
                    if (start < edittext.length()) edittext.setSelection(start + 1);
                } else if (primaryCode == CodeAllLeft) {
                    edittext.setSelection(0);
                } else if (primaryCode == CodeAllRight) {
                    edittext.setSelection(edittext.length());
                } else if (primaryCode == CodePrev) {
                    goToNextField(CodePrev, edittext, View.FOCUS_BACKWARD);
                } else if (primaryCode == CodeNext) {
                    goToNextField(CodeNext, edittext, View.FOCUS_FORWARD);
                } else { // insert character
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }

            /**
             * Method to go to next fragment with shortkey in keyboard
             * @param obj
             * @param primaryCode
             */
            private void shortcutKey (Fragment obj, int primaryCode, int[] keyCodes) {
                String keyCode = String.valueOf(keyCodes[0]);
                if (keyCode.length() > 3 || keyCodes[0] < 0 ) {
                    if (keyCodes[0] == 55006) {
                        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                        getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                    }
                    return;
                } else {
                    int key = Integer.valueOf(Character.toString((char) primaryCode));
                    MenuFragment menuFragment = (MenuFragment) obj;
                    menuFragment.getListener().onItemMenuSelected(DatabaseDescription.Story.buildStoryUri(key), new Long(key));
                }
            }

            @Override
            public void onPress(int arg0) {
            }

            @Override
            public void onRelease(int primaryCode) {
            }

            @Override
            public void onText(CharSequence text) {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeUp() {
            }
        };
    }


    private void goToNextField(int codKey, EditText edittext, int focusForward) {
        View focusNew = edittext.focusSearch(focusForward);
        if (focusNew != null && focusNew.getClass() != KeyboardView.class) {
            focusNew.requestFocus();
        } else {
            if (codKey == 55002) { //Code Enter
                listener.onLogin();
            }
            edittext.requestFocus();
        }
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
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
                getFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragmentSga);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }


}
