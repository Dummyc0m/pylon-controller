package com.dummyc0m.pylon.pyloncontroller;

import com.dummyc0m.pylon.pyloncore.DBConnectionFactory;

/**
 * Created by Dummyc0m on 3/23/16.
 */
public class PylonSQLSource {
    private DBConnectionFactory connectionFactory;

    public PylonSQLSource(String type, String url, String username, String password) {
        connectionFactory = new DBConnectionFactory(type, url, username, password);
    }

    public void load() {

    }

    public void save() {

    }


}
