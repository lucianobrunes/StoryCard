package com.br.android.storycard.view.sga;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.br.android.storycard.R;

public class LoginFragment extends Fragment {

    private EditText editTextV ;
    private EditText editTextS ;

    //variable for counting two successive up-down events
    int clickCount = 0;
    //variable for storing the time of first click
    long startTime;
    //variable for calculating the total time
    long duration;
    //constant for defining the time duration between the click that can be considered as double-tap
    static final int MAX_DURATION = 500;

    // used to inform the MainActivity when a story is selected
    private LoginFragment.LoginFragmentListener listener;

    // callback method implemented by MainActivity
    public interface LoginFragmentListener {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFields();
    }

    private void initFields() {
        editTextV = (EditText) getActivity().findViewById(R.id.editText1);
        editTextS = (EditText) getActivity().findViewById(R.id.editText2);

        /**
         * Desabilita o teclado nativo ao clicar nos editText (não funciona tentando abaixo desabilitar tudo)
         * getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         */

        /**
         * Configura os campos
         */
        editTextV.requestFocus();
        editTextV.setShowSoftInputOnFocus(false);
        editTextV.setCursorVisible(false);
        //editText1.setEnabled(false); (funciona mas teria que mudar a cor do desabilitado)
        editTextV.setFocusable(true);
        editTextV.setClickable(false);
        editTextV.setSelection(editTextV.getText().length());
        editTextS.setShowSoftInputOnFocus(false);
        editTextS.setClickable(false);
        editTextS.setSelection(editTextS.getText().length());
        editTextS.setCursorVisible(false);

        /**
         * Desabilitar o double tap ou double click que abria o menu de contexto (selection, cut, copy, past)
         */
        editTextV.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        editTextS.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    // set LoginFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (LoginFragment.LoginFragmentListener) context;
    }

    // remove LoginFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public EditText getEditTextV() {
        return editTextV;
    }

    public EditText getEditTextS() {
        return editTextS;
    }
}
