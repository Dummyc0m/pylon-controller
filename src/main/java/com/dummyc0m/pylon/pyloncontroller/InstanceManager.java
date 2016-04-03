package com.dummyc0m.pylon.pyloncontroller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dummyc0m on 3/24/16.
 */
public class InstanceManager {
    private Map<String, PylonServer> pylonServerMap;

    public InstanceManager() {
        this.pylonServerMap = new ConcurrentHashMap<>();
    }

    public boolean canAddServer(PylonServer server) {
        return !pylonServerMap.containsKey(server.getIdentifier());
    }
}
