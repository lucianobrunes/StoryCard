package com.br.android.storycard.view.sga;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.br.android.storycard.R;
import com.br.android.storycard.view.sga.util.TaskScheduler;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class LoginFragment extends Fragment {

    private TextView textViewV;
    private EditText editTextV ;
    private EditText editTextS ;
    private TextView textViewS;
    private TextView textViewDateTime;


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
        textViewS = (TextView) getActivity().findViewById(R.id.textViewS);
        textViewV = (TextView) getActivity().findViewById(R.id.textViewV);
        editTextV = (EditText) getActivity().findViewById(R.id.editTextV);
        editTextS = (EditText) getActivity().findViewById(R.id.editTextS);
        textViewDateTime = (TextView) getActivity().findViewById(R.id.textViewCurrentDateTime);
        textViewS.setVisibility(View.INVISIBLE);
        editTextS.setVisibility(View.INVISIBLE);
        textViewV.setVisibility(View.INVISIBLE);
        editTextV.setVisibility(View.INVISIBLE);


        timer();


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
        editTextV.setFocusable(true);
        editTextV.setClickable(false);
        editTextV.setSelection(editTextV.getText().length());

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

    private void timer() {
        TaskScheduler timer = new TaskScheduler();
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String s;
                Format formatter;
                Date date = new Date();

                Locale localeBR = new Locale("pt", "BR");
                SimpleDateFormat fmt = new SimpleDateFormat("E dd/MM/yy HH:mm:ss", localeBR);
                s = fmt.format(date);
                String currentDateTimeString = s.substring(0,1).toUpperCase().concat(s.substring(1));
                textViewDateTime.setText(currentDateTimeString);
            }
        },1000);
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


    /**
     * Method to control enable (visibility and text) of EditText (user and password)
     * @param enable
     */
    public void enableFields (Boolean enable) {
        if (enable) {
            if (textViewV.getVisibility() == View.INVISIBLE) {
                textViewV.setVisibility(View.VISIBLE);
                editTextV.setVisibility(View.VISIBLE);
                editTextV.getText().clear();
                textViewDateTime.setVisibility(View.INVISIBLE);
                editTextV.requestFocus();
            }
            if (editTextV.getText().length() == 4 ) {
                int user = Integer.valueOf(editTextV.getText().toString());
                if (textViewS.getVisibility() == View.INVISIBLE && user != 9999) {
                    textViewS.setVisibility(View.VISIBLE);
                    editTextS.setVisibility(View.VISIBLE);
                    editTextS.getText().clear();
                    editTextS.setShowSoftInputOnFocus(false);
                    editTextS.setClickable(false);
                    editTextS.setSelection(editTextS.getText().length());
                    editTextS.setCursorVisible(false);
                }
            }
        } else {


            if (textViewS.getVisibility() == View.VISIBLE) {
                editTextS.getText().clear();
                textViewS.setVisibility(View.INVISIBLE);
                editTextS.setVisibility(View.INVISIBLE);
                return;
            }

            if (textViewV.getVisibility() == View.VISIBLE) {
                textViewDateTime.setVisibility(View.VISIBLE);
                editTextV.getText().clear();
                textViewV.setVisibility(View.INVISIBLE);
                editTextV.setVisibility(View.INVISIBLE);
            }
        }
    }
}
