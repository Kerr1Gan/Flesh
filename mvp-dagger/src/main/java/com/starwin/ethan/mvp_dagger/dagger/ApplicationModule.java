package com.starwin.ethan.mvp_dagger.dagger;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

/**
 * -@Component.Builder
 * interface Builder {
 *  -@BindsInstance
 *  XXComponent.Builder application(Application application);
 *  XXComponent build();
 * }
 */
@Module
public abstract class ApplicationModule {
    @Binds
    abstract Context provideContext(Application application);
}
