package com.kotatu.android.top.api;

import com.github.nkzawa.emitter.Emitter;
import com.kotatu.android.entity.Lounge;
import com.kotatu.android.entity.Room;
import com.kotatu.android.entity.User;
import com.kotatu.android.entity.UserRoom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mayuhei on 2017/05/18.
 */

public class DummyGetRoomsApi implements GetRoomsApi {
    @Override
    public GetRoomsApiResult get(User user, Listener listener) {
        GetRoomsApiResult result = new GetRoomsApiResult();
        List<Room> lounges = new ArrayList<>();
        List<Room> userRooms = new ArrayList<>();
        result.setMyRoom(Room.userRoom("huga", user));
        result.setLounges(lounges);
        lounges.add(Room.lounge("xxxx", "WoodPecker T.C", "http://circle", Arrays.asList(user, toshi, hiro)));
        lounges.add(Room.lounge("xxyy", "Toast T.C", "http://circle", Arrays.asList(user, toshi, kazu)));
        result.setUserRooms(Arrays.asList(Room.userRoom("abcd", toshi), Room.userRoom("efgh", hiro), Room.userRoom("addgda", saki), Room.userRoom("ffdf", mayumi), Room.userRoom("gdfa", ikarosu), Room.userRoom("fdhh", mike), Room.userRoom("hjhjk", sakura)));
        listener.call(result);
        return result;
    }

    private User toshi = User.from(1000l, "toshi", "http://toshi");
    private User hiro = User.from(1001l, "hiro", "http://hiro");
    private User kazu = User.from(1002l, "kazu", "http://kazu");
    private User saki = User.from(1003l, "saki", "http://saki");
    private User mayumi = User.from(1004l, "mayumi", "http://mayumi");
    private User ikarosu = User.from(1005l, "ikarosu", "http://ikarosu");
    private User mike = User.from(1006l, "mike", "http://mike");
    private User sakura = User.from(1006l, "sakura", "http://sakura");

}
