package tv.danmaku.ijk.media.exo.video;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class AndroidMediaController extends SimpleMediaController implements IMediaController {
    private ActionBar mActionBar;

    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AndroidMediaController(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
    }

    public void setSupportActionBar( ActionBar actionBar) {
        mActionBar = actionBar;
        if (isShowing()) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    @Override
    public void show() {
        super.show();
        if (mActionBar != null)
            mActionBar.show();
    }

    @Override
    public void hide() {
        super.hide();
        if (mActionBar != null)
            mActionBar.hide();
        for (View view : mShowOnceArray)
            view.setVisibility(View.GONE);
        mShowOnceArray.clear();
    }

    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    public void showOnce( View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }

}
