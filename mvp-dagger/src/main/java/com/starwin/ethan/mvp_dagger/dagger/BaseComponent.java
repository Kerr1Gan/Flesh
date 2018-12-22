package com.starwin.ethan.mvp_dagger.dagger;

public interface BaseComponent<T> {

    T inject(T inject);
}
