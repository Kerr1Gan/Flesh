package com.ecjtu.flesh.mvp.presenter;

import android.database.sqlite.SQLiteDatabase;

import com.ecjtu.flesh.mvp.IPresenter;
import com.ecjtu.flesh.mvp.IView;
import com.ecjtu.netcore.model.PageDetailModel;

public class PageDetailContract {

    public interface View extends IView<Presenter> {
        String getUrl();
        SQLiteDatabase getSQLiteDatabase();
        void loadDataFromInternet(PageDetailModel model);
    }

    public interface Presenter extends IPresenter<View> {
        boolean isLike();

        void deleteLike();

        void addLike();

        PageDetailModel readCache();

        void saveCache();

        PageDetailModel getPageModel();
    }
}
