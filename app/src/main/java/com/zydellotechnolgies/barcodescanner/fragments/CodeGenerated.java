package com.zydellotechnolgies.barcodescanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.adapter.GeneratedCodeAdapter;
import com.zydellotechnolgies.barcodescanner.adapter.HistoryAdapter;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;
import com.zydellotechnolgies.barcodescanner.model.ScanItemCreated;
import com.zydellotechnolgies.barcodescanner.utils.DatabaseHelper;
import com.zydellotechnolgies.barcodescanner.utils.MyDividerItemDecoration;
import com.zydellotechnolgies.barcodescanner.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodeGenerated#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodeGenerated extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeGenerated() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodeGenerated.
     */
    // TODO: Rename and change types and number of parameters
    public static CodeGenerated newInstance(String param1, String param2) {
        CodeGenerated fragment = new CodeGenerated();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.info)
    TextView info;

    private GeneratedCodeAdapter mAdapter;
    private ArrayList<ScanItemCreated> list = new ArrayList<>();
    private DatabaseHelper mDatabaseHelper = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_code_generated, container, false);
        ButterKnife.bind(this, root);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.generated);

        mDatabaseHelper = new DatabaseHelper(getActivity());

        mAdapter = new GeneratedCodeAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        recyclerview.setAdapter(mAdapter);

        prepareData();

        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
               /* ScanItemCreated item = today_list.get(position);
                if (isHistory)
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.history_detailed_frag));
                else
                    PushFragment(new ScanedText().newInstance(item), getString(R.string.favourite_detailed_frag));
*/
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return root;
    }


    void prepareData() {
        list.clear();

        if (mDatabaseHelper == null)
            mDatabaseHelper = new DatabaseHelper(getActivity());
        ArrayList<ScanItemCreated> items = null;
        items = mDatabaseHelper.getScanItemsCreated();


        for (int i = 0; i < items.size(); i++) {
            list.add(items.get(i));
        }
        mAdapter.notifyDataSetChanged();

        if (list.size() <= 0) {
            info.setVisibility(View.VISIBLE);
        }
    }

    public void PushFragment(Fragment fragment, String string) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, fragment, string);
        ft.commit();
    }
}
