package com.ecjtu.netcore;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;
import com.ecjtu.netcore.network.IRequestCallbackV2;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;

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
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.ecjtu.netcore.test", appContext.getPackageName());
    }
}
