package com.duckwarlocks.klutz.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.duckwarlocks.klutz.R;
import com.duckwarlocks.klutz.adapters.LocationAdapter;
import com.duckwarlocks.klutz.callbacks.SimpleItemTouchHelperCallback;
import com.duckwarlocks.klutz.daos.LocationsDAO;
import com.duckwarlocks.klutz.utilities.GpsCoordinatesHelper;
import com.duckwarlocks.klutz.vo.LocationVO;

import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class SavedLocationsFragment extends Fragment
        implements LocationAdapter.OnStartDragListener{
    private RecyclerView mLocationView;
    private EditText mInputSearch;
    private LocationAdapter adapter;
    private List<LocationVO> mLocationList;
    private LocationsDAO mLocationDAO;
    private ItemTouchHelper mItemTouchHelper;

    private View rootView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_saved_locations,container,false);
        mContext = rootView.getContext().getApplicationContext();
        mLocationView = (RecyclerView) rootView.findViewById(R.id.recordList);

        adapter = new LocationAdapter(mContext,R.layout.list_piece);

        mLocationDAO = new LocationsDAO(mContext);

        try{
            mLocationDAO.open();

            mLocationList = mLocationDAO.getAllVideos();

            mLocationDAO.close();
        }catch (SQLException e){
            //TODO do something later
            Log.e(getClass().getName(), e.toString());
            e.printStackTrace();
        }

        adapter.setLocationVOList(mLocationList);
        adapter.updateList();
        mLocationView.setAdapter(adapter);
        mLocationView.setLayoutManager(new LinearLayoutManager(mContext));
        mLocationView.setItemAnimator(new DefaultItemAnimator());

        mInputSearch = (EditText)rootView.findViewById(R.id.inputSearch);
        //Add search functionality to editText
        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.updateList();
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mLocationView);

//       return inflater.inflate(R.layout.fragment_saved_locations,container,false);
        return rootView;
    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


}
