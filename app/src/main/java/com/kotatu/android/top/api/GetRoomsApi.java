package com.kotatu.android.top.api;

import com.github.nkzawa.emitter.Emitter;
import com.kotatu.android.entity.User;

/**
 * Created by mayuhei on 2017/05/18.
 */

public interface GetRoomsApi {
    GetRoomsApiResult get(User user, Listener callback);

    public static interface Listener {
        void call(GetRoomsApiResult result);
    }
}
