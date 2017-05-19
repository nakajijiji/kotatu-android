package com.kotatu.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kotatu.android.R;
import com.kotatu.android.entity.Room;
import com.kotatu.android.entity.User;
import com.kotatu.android.intent.IntentKey;
import com.kotatu.android.top.DefaultRoomListAdapter;
import com.kotatu.android.top.api.DummyGetRoomsApi;
import com.kotatu.android.top.api.GetRoomsApi;
import com.kotatu.android.top.api.GetRoomsApiResult;
import com.kotatu.android.util.JsonSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GetRoomsApi api = new DummyGetRoomsApi();
    private User DUMMY_USER = User.from(1l, "Nakaji Kohei", "http://hogehoge");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api.get(DUMMY_USER, new GetRoomsApi.Listener() {
            @Override
            public void call(GetRoomsApiResult result) {
                List<ListView> listViews = new ArrayList<ListView>();
                ListView homeView = (ListView) findViewById(R.id.home_list);
                setupAdapter(homeView, Arrays.asList(result.getMyRoom()));
                ListView loungeView = (ListView) findViewById(R.id.lounge_list);
                setupAdapter(loungeView, result.getLounges());
                ListView frinedView = (ListView) findViewById(R.id.friend_list);
                setupAdapter(frinedView, result.getUserRooms());
                listViews.add(homeView);
                listViews.add(loungeView);
                listViews.add(frinedView);
                for(ListView view : listViews){
                    view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Room room = (Room) adapterView.getItemAtPosition(position);
                            Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                            intent.putExtra(IntentKey.ROOM, JsonSerializer.serialize(room));
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void setupAdapter(ListView view, List<Room> rooms){
        DefaultRoomListAdapter adapter = new DefaultRoomListAdapter(getApplicationContext(), rooms);
        view.setAdapter(adapter);
    }
}
