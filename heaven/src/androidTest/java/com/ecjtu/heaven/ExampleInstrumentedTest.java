package com.ecjtu.heaven;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ecjtu.heaven.cache.PageListCacheHelper;
import com.ecjtu.heaven.db.DatabaseManager;
import com.ecjtu.heaven.db.table.impl.LikeTableImpl;
import com.ecjtu.netcore.model.PageModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.ecjtu.heaven", appContext.getPackageName());

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PageModel.ItemModel> itemModels = new ArrayList<PageModel.ItemModel>();
                for (int i = 0; i < 10000; i++) {
                    itemModels.add(new PageModel.ItemModel("", "", ""));
                }
                long start = System.currentTimeMillis();
                SQLiteDatabase db = DatabaseManager.getInstance(appContext).getHelper(appContext, "test4").getWritableDatabase();
                db.beginTransaction();
                LikeTableImpl impl = new LikeTableImpl();
                for (int i = 0; i < 10000; i++) {
                    impl.addLike(db, "page" + i, "", "", "");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("db vs parcel", "db save time " + (System.currentTimeMillis() - start));

                start = System.currentTimeMillis();
                db.beginTransaction();
                impl.getAllLikes(db);
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.e("db vs parcel", "db read time " + (System.currentTimeMillis() - start));
                db.close();

                start = System.currentTimeMillis();
                PageModel model = new PageModel(itemModels);
                PageListCacheHelper helper = new PageListCacheHelper(appContext.getCacheDir().getAbsolutePath());
                helper.put("test", model);
                Log.e("db vs parcel", "parcel save time " + (System.currentTimeMillis() - start));

                start = System.currentTimeMillis();
                helper.get("test");
                Log.e("db vs parcel", "parcel read time " + (System.currentTimeMillis() - start));
            }
        }).start();
    }
}
