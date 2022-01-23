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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable;


    public MyEventsFragment() {
        // Required empty public constructor
    }


    public static MyEventsFragment newInstance() {
        MyEventsFragment fragment = new MyEventsFragment();
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
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyEventsViewModel vm = new ViewModelProvider(getActivity(), new MyEventsViewModel.MyEventsViewModelFactory(getActivity())).get(MyEventsViewModel.class);
        LiveData<List<Event>> events = vm.getEvents();
        RecyclerView rvAllEvents = getView().findViewById(R.id.rvMyEvents);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvAllEvents.getContext(), DividerItemDecoration.VERTICAL);
        rvAllEvents.addItemDecoration(dividerItemDecoration);

        events.observe(getActivity(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                Log.i("Evento:", events.get(0).description);
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
                MyEventsViewModel vm = new ViewModelProvider(getActivity(), new MyEventsViewModel.MyEventsViewModelFactory(getActivity())).get(MyEventsViewModel.class);
                vm.refreshEvents();
                handler.postDelayed(runnable, 3000);
            }
        }, 3000);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}