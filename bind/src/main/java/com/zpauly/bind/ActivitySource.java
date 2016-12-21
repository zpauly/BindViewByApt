package com.zpauly.bind;

import android.app.Activity;
import android.view.View;

/**
 * Created by zpauly on 2016/12/20.
 */

public class ActivitySource implements Source {
    private Activity mActivity;

    public ActivitySource(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public View findViewById(int id) {
        return mActivity.findViewById(id);
    }
}
