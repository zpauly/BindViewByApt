package com.zpauly.bind;

import android.view.View;

/**
 * Created by zpauly on 2016/12/20.
 */

public class ViewSource implements Source {
    private View mView;

    public ViewSource(View view) {
        this.mView = view;
    }

    @Override
    public View findViewById(int id) {
        return mView.findViewById(id);
    }
}
