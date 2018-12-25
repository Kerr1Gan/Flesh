package com.ecjtu.flesh.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.ecjtu.flesh.cache.impl.PageDetailCacheHelper;
import com.ecjtu.flesh.db.table.impl.DetailPageTableImpl;
import com.ecjtu.flesh.db.table.impl.LikeTableImpl;
import com.ecjtu.netcore.jsoup.SoupFactory;
import com.ecjtu.netcore.jsoup.impl.PageDetailSoup;
import com.ecjtu.netcore.model.PageDetailModel;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.HttpURLConnection;
import java.util.Map;

public class PageDetailPresenter implements PageDetailContract.Presenter {

    private PageDetailContract.View mView;

    private PageDetailModel mPageModel = null;

    private LikeTableImpl impl = new LikeTableImpl();

    @Override
    public boolean isLike() {
        return impl.isLike(mView.getSQLiteDatabase(), mView.getUrl());
    }

    @Override
    public void deleteLike() {
        impl.deleteLike(mView.getSQLiteDatabase(), mView.getUrl());
    }

    @Override
    public void addLike() {
        impl.addLike(mView.getSQLiteDatabase(), mView.getUrl());
    }

    @Override
    public PageDetailModel readCache() {
        String url = mView.getUrl();
        if (!(mView instanceof Context) || TextUtils.isEmpty(url)) {
            return null;
        }
        Context context = (Context) mView;
        PageDetailCacheHelper helper = new PageDetailCacheHelper(context.getFilesDir().getAbsolutePath());
        String local = url.substring(url.lastIndexOf("/"));
        return mPageModel = helper.get(local);
    }

    @Override
    public void saveCache() {
        String url = mView.getUrl();
        if (!(mView instanceof Context) || TextUtils.isEmpty(url)) {
            return;
        }
        Context context = (Context) mView;
        PageDetailCacheHelper helper = new PageDetailCacheHelper(context.getFilesDir().getAbsolutePath());
        String local = url.substring(url.lastIndexOf("/"));
        helper.put(local, mPageModel);
    }

    @Override
    public PageDetailModel getPageModel() {
        return mPageModel;
    }

    private void saveDetailPage() {
        if (!(mView instanceof Context)) {
            return;
        }
        DetailPageTableImpl impl = new DetailPageTableImpl();
        //todo 获取到高度缓存加入数据库
        impl.addDetailPage(mView.getSQLiteDatabase(), mPageModel);
    }

    @Override
    public void takeView(PageDetailContract.View view) {
        mView = view;
        AsyncNetwork request = new AsyncNetwork();
        request.request(mView.getUrl(), null);
        request.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(@Nullable HttpURLConnection httpURLConnection, @NotNull String response) {
                Map ret = SoupFactory.parseHtml(PageDetailSoup.class, response, mView.getUrl());
                Object model = ret.get(PageDetailSoup.class.getSimpleName());
                if (model != null) {
                    PageDetailModel localModel = (PageDetailModel) model;
                    mPageModel = localModel;
                    mView.loadDataFromInternet(mPageModel);
                    saveDetailPage();
                }
            }
        });
    }

    @Override
    public void dropView() {

    }


}
