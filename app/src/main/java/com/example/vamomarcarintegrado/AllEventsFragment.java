package com.example.vamomarcarintegrado;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllEventsFragment extends Fragment {
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;


    public AllEventsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AllEventsFragment newInstance() {
        AllEventsFragment fragment = new AllEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AllEventsViewModel vm = new ViewModelProvider(getActivity(), new AllEventsViewModel.AllEventsViewModelFactory(getActivity())).get(AllEventsViewModel.class);
        LiveData<List<Event>> events = vm.getEvents();
        RecyclerView rvAllEvents = getView().findViewById(R.id.rvAllEvents);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAllEvents.getContext(), DividerItemDecoration.VERTICAL);
        rvAllEvents.addItemDecoration(dividerItemDecoration);

        events.observe(getActivity(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                AllEventsAdapter allEventsAdapter = new AllEventsAdapter(getContext(), events);
                rvAllEvents.setAdapter(allEventsAdapter);
                rvAllEvents.setLayoutManager(new LinearLayoutManager(getContext()));
                }
        });



    }

    @Override
    public void onResume() {
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                AllEventsViewModel vm = new ViewModelProvider(getActivity(), new AllEventsViewModel.AllEventsViewModelFactory(getActivity())).get(AllEventsViewModel.class);
                vm.refreshEvents();
                handler.postDelayed(runnable, 5000);
            }
        }, 5000);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}

