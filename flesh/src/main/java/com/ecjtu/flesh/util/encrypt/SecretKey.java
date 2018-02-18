package com.ecjtu.flesh.util.encrypt;

import java.io.Serializable;
import java.security.Key;

public class SecretKey implements Serializable {

    private static final long serialVersionUID = 20180218L;

    private String dummy1 = "dummy1";

    private String dummy2 = "dummy2";

    private String dummy3 = "dummy3";

    private String dummy4 = "dummy4";

    private int dummyInt = 20180218;

    private Key key;

    private long dymmyLong = System.currentTimeMillis();

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }
}
