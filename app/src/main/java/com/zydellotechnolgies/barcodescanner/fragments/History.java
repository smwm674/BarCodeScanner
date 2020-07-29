package com.zydellotechnolgies.barcodescanner.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.adapter.HistoryAdapter;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;
import com.zydellotechnolgies.barcodescanner.utils.MyDividerItemDecoration;
import com.zydellotechnolgies.barcodescanner.utils.RecyclerTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {
    private static final String ARG_PARAM1 = "Operation";

    // TODO: Rename and change types of parameters
    private boolean isHistory;

    public History() {
        // Required empty public constructor
    }

    public static History newInstance(boolean isHistory) {
        History fragment = new History();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isHistory);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isHistory = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @BindView(R.id.label_1)
    TextView label_1;
    @BindView(R.id.label_2)
    TextView label_2;
    @BindView(R.id.recyclerview)
    RecyclerView today_recyclerview;
    @BindView(R.id.recyclerview_prev)
    RecyclerView previous_recyclerview;
    @BindView(R.id.today)
    CardView today;
    @BindView(R.id.previous)
    CardView previous;
    @BindView(R.id.info)
    TextView info;
    private HistoryAdapter mAdapter, prev_mAdapter;
    private ArrayList<ScanItem> today_list = new ArrayList<>();
    private ArrayList<ScanItem> previous_list = new ArrayList<>();
    private DatabaseHelper mDatabaseHelper = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);
        if (isHistory) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.history);
            info.setText(getString(R.string.no_history));
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.favourite);
            info.setText(getString(R.string.no_favourite));
        }
        mDatabaseHelper = new DatabaseHelper(getActivity());
        setHasOptionsMenu(true);

        mAdapter = new HistoryAdapter(today_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        today_recyclerview.setLayoutManager(mLayoutManager);
        today_recyclerview.setItemAnimator(new DefaultItemAnimator());
        today_recyclerview.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        today_recyclerview.setAdapter(mAdapter);

        prev_mAdapter = new HistoryAdapter(previous_list);
        RecyclerView.LayoutManager previous_mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        previous_recyclerview.setLayoutManager(previous_mLayoutManager);
        previous_recyclerview.setItemAnimator(new DefaultItemAnimator());
        previous_recyclerview.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        previous_recyclerview.setAdapter(prev_mAdapter);

        prepareData();

        today_recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), today_recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ScanItem item = today_list.get(position);
                if (isHistory)
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.history_detailed_frag));
                else
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.favourite_detailed_frag));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        previous_recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), previous_recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ScanItem item = previous_list.get(position);
                if (isHistory)
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.history_detailed_frag));
                else
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.favourite_detailed_frag));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return root;
    }

    void prepareData() {
        today_list.clear();
        previous_list.clear();

        if (mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(getActivity());
        ArrayList<ScanItem> items = null;
        if (isHistory)
            items = mDatabaseHelper.getScanItems();
        else
            items = mDatabaseHelper.getFavouriteScanItems(true);

        for (int i = 0; i < items.size(); i++) {
            Date calendar = Calendar.getInstance().getTime();
            SimpleDateFormat date = new SimpleDateFormat("dd/MMM/yyyy");

            if (items.get(i).getTime().equals(date.format(calendar)))
                today_list.add(items.get(i));
            else
                previous_list.add(items.get(i));

         /*   Log.i("Current", date.format(calendar));
            Log.i("Retrieve", items.get(i).getTime());*/
        }

        mAdapter.notifyDataSetChanged();
        prev_mAdapter.notifyDataSetChanged();

        if (today_list.size() <= 0)
            today.setVisibility(View.GONE);
        if (previous_list.size() <= 0)
            previous.setVisibility(View.GONE);
        if (today_list.size() <= 0 && previous_list.size() <= 0) {
            previous.setVisibility(View.GONE);
            today.setVisibility(View.GONE);
            info.setVisibility(View.VISIBLE);
        }
    }

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }
}
