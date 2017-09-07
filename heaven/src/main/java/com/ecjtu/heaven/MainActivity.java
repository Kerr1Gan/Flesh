package com.ecjtu.heaven;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ecjtu.netcore.Constants;
import com.ecjtu.netcore.jsoup.PageDetailSoup;
import com.ecjtu.netcore.jsoup.PageSoup;
import com.ecjtu.netcore.jsoup.SoupFactory;
import com.ecjtu.netcore.model.PageModel;
import com.ecjtu.sharebox.network.AsyncNetwork;
import com.ecjtu.sharebox.network.IRequestCallback;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncNetwork request = new AsyncNetwork();
        request.request(Constants.HOST_URL);
        request.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                Map<String, Object> values = SoupFactory.parseHtml(PageSoup.class, response);
                if (values != null) {
                    PageModel soups = (PageModel) values.get(PageSoup.class.getSimpleName());
                    final String url = soups.getItemList().get(0).getHref();
                    AsyncNetwork local = new AsyncNetwork();
                    local.request(url);
                    local.setRequestCallback(new IRequestCallback() {
                        @Override
                        public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                            SoupFactory.parseHtml(PageDetailSoup.class,response,url);
                        }

                        @Override
                        public void onError(HttpURLConnection httpURLConnection, Exception exception) {

                        }
                    });
                }
            }

            @Override
            public void onError(HttpURLConnection httpURLConnection, Exception exception) {

            }
        });
    }
}
