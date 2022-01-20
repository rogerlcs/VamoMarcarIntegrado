package com.example.vamomarcarintegrado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AllEventsActivity extends AppCompatActivity {
    static  int ADD_EVENT_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        Toolbar toolbar = findViewById(R.id.tbHome);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddNewEvent = findViewById(R.id.fabAddNewEvent);
        fabAddNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AllEventsActivity.this, AddEventActivity.class);
                startActivityForResult(i, ADD_EVENT_ACTIVITY);
            }
        });
        AllEventsFragment allEventsFragment = AllEventsFragment.newInstance();
        setFragment(allEventsFragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.btNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.allEventsViewOp:
                        AllEventsFragment allEventsFragment = AllEventsFragment.newInstance();
                        setFragment(allEventsFragment);
                        break;

                    case R.id.myEventsViewOp:
                        //setFragment(myEventsFragment);
                        break;

                    case R.id.invitationsViewOp:
                        //setFragment(invitationsFragment);
                        break;
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tbmenu, menu);
        return true;
    }



    void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_EVENT_ACTIVITY){
            if(resultCode == RESULT_OK){
                AllEventsViewModel vm = new ViewModelProvider(this, new AllEventsViewModel.AllEventsViewModelFactory(this)).get(AllEventsViewModel.class);
                vm.refreshEvents();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.opPerfil:
                Intent i = new Intent(AllEventsActivity.this,PerfilActivity.class);
                final String id = Config.getId(this);
                i.putExtra("id", id);
                startActivity(i);
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }
}