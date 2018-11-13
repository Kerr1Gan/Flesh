package com.starwin.ethan.mvp_dagger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.starwin.ethan.mvp_dagger.dagger.BaseComponent;

public abstract class DaggerMvpActivity<T extends BaseComponent<Y>, Y> extends AppCompatActivity {

    private T mDaggerComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDaggerComponent = initDaggerComponent();
        if (mDaggerComponent != null && getComponentInject() != null) {
            getDaggerComponent().inject(getComponentInject());
        }
    }

    public T getDaggerComponent() {
        return mDaggerComponent;
    }

    protected abstract T initDaggerComponent();

    protected abstract Y getComponentInject();

}
