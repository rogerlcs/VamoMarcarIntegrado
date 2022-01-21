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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InviteEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteEventsFragment extends Fragment {


    public InviteEventsFragment() {
        // Required empty public constructor
    }


    public static InviteEventsFragment newInstance() {
        InviteEventsFragment fragment = new InviteEventsFragment();
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
        return inflater.inflate(R.layout.fragment_invite_events, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InviteEventsViewModel vm = new ViewModelProvider(getActivity(), new InviteEventsViewModel.InviteEventsViewModelFactory(getActivity())).get(InviteEventsViewModel.class);
        LiveData<List<Event>> events = vm.getEvents();
        RecyclerView rvAllEvents = getView().findViewById(R.id.rvInviteEvents);
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
}