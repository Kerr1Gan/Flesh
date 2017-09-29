package com.ecjtu.flesh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ecjtu.flesh.cache.impl.PageListCacheHelper;
import com.ecjtu.flesh.db.DatabaseManager;
import com.ecjtu.flesh.db.table.impl.LikeTableImpl;
import com.ecjtu.flesh.util.file.FileUtil;
import com.ecjtu.netcore.model.PageModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public static class TestItemModel implements Serializable {
        int id;
        String href;
        String description;
        String imgUrl;
        int height;

        public TestItemModel(String href, String description, String imgUrl) {
            this.href = href;
            this.description = description;
            this.imgUrl = imgUrl;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setHeight(int height){
            this.height = height;
        }

        public int getHeight(){
            return this.height;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof TestItemModel)){
                return false;
            }
            TestItemModel other = (TestItemModel) o;
            return other.href.equals(this.href);
        }
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.ecjtu.heaven", appContext.getPackageName());

        List<PageModel.ItemModel> itemModels = new ArrayList<PageModel.ItemModel>();
        for (int i = 0; i < 50000; i++) {
            itemModels.add(new PageModel.ItemModel("", "", ""));
        }

        //db begin
        appContext.deleteDatabase("test4");
        SQLiteDatabase db = DatabaseManager.getInstance(appContext).getHelper(appContext, "test4").getWritableDatabase();
        long start = System.currentTimeMillis();
        db.beginTransaction();
        LikeTableImpl impl = new LikeTableImpl();
        for (int i = 0; i < 50000; i++) {
            impl.addLike(db, "page" + i, "", "", "");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.e("cache speed", "db save time " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        db.beginTransaction();
        impl.getAllLikes(db);
        db.setTransactionSuccessful();
        db.endTransaction();
        Log.e("cache speed", "db read time " + (System.currentTimeMillis() - start));
        db.close();
        //db end

        //parcel begin
        start = System.currentTimeMillis();
        PageModel model = new PageModel(itemModels);
        PageListCacheHelper helper = new PageListCacheHelper(appContext.getCacheDir().getAbsolutePath());
        helper.put("test", model);
        Log.e("cache speed", "parcel save time " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        helper.get("test");
        Log.e("cache speed", "parcel read time " + (System.currentTimeMillis() - start));
        //parcel end

        //Serializable
        List<TestItemModel> testModels = new ArrayList<TestItemModel>();
        for (int i = 0; i < 50000; i++) {
            testModels.add(new TestItemModel("", "", ""));
        }
        start = System.currentTimeMillis();
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(appContext.getCacheDir().getAbsolutePath(), "serializable")));
        os.writeObject(testModels);
        Log.e("cache speed", "serializable save time " + (System.currentTimeMillis() - start));
        os.close();
        start = System.currentTimeMillis();
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(appContext.getCacheDir().getAbsolutePath(), "serializable")));
        is.readObject();
        Log.e("cache speed", "serializable read time " + (System.currentTimeMillis() - start));
        is.close();
    }

    @Test
    public void deleteCache() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();

        File[] list = new File(appContext.getFilesDir().getAbsolutePath()).listFiles();
        for (File child : list) {
//            if (child.getName().endsWith("性感妹子")) {
//                child.delete();
//                break;
//            }

            child.delete();
        }
    }

    @Test
    public void copyDb2Sdcard() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();
        File files = appContext.getFilesDir();
        files = files.getParentFile();
        files = new File(files, "databases");
        File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "heaven.db");
        if (!dest.exists()) dest.createNewFile();
        FileUtil.INSTANCE.copyFile2Path(new File(files, "heaven"), dest);
    }

    @Test
    public void copyDb2Internal() throws Exception {
        // Context of the app under test.
        final Context appContext = InstrumentationRegistry.getTargetContext();
        File files = appContext.getFilesDir();
        files = files.getParentFile();
        files = new File(files, "databases");
        File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "heaven.db");
        if (!dest.exists()) dest.createNewFile();
        FileUtil.INSTANCE.copyFile2Path(dest, new File(files, "heaven"));
    }

    @Test
    public void copyBg() throws Exception {
        final Context appContext = InstrumentationRegistry.getTargetContext();
        File file = new File(appContext.getFilesDir(), "bg.png");
        FileUtil.INSTANCE.copyFile2Path(file, new File("/sdcard/bg.png"));
    }
}
