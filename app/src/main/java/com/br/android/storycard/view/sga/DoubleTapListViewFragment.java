package com.br.android.storycard.view.sga;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.br.android.storycard.R;

import java.util.Arrays;
import java.util.List;

public class DoubleTapListViewFragment extends Fragment implements CustomListView.OnItemDoubleTapLister {
	    private CustomListView mListView = null;
	    private List<String> mList = null;
	    private ArrayAdapter<String> mAdapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListView = new CustomListView(getActivity());
        mListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mList= Arrays.asList(getResources().getStringArray(R.array.main_menu));
        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemDoubleClickListener(this);
        return mListView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


	@Override
	public void OnDoubleTap(AdapterView<?> parent, View view, int position,
                            long id) {
/*        if (position == 2) {
            PrinterBluetooth printer = new PrinterBluetooth();
            printer.sendToPrint();
        } else {
            //Toast.makeText(getActivity().getApplication(), "Double tap occured on " + mList.get(position), Toast.LENGTH_LONG).show();
        }*/


	}
	@Override
	public void OnSingleTap(AdapterView<?> parent, View view, int position,
                            long id) {
		 //Toast.makeText(getActivity().getApplication(), "Single tap occured on "+mList.get(position), Toast.LENGTH_LONG).show();
		
	}
}