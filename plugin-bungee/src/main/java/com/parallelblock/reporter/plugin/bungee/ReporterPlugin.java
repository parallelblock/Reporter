package com.parallelblock.reporter.plugin.bungee;

import com.parallelblock.reporter.plugin.common.CentralHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class ReporterPlugin extends Plugin {
    private final CentralHandler centralHandler = new CentralHandler();

    @Override
    public void onEnable() {

    }

    public Class[] getHandleTypes() {
        return centralHandler.getHandleTypes();
    }

    public Object register(Object handleOwner, Object handleBase, Object parentHandle, String[] labels) throws Throwable {
        return centralHandler.register(handleOwner, handleBase, parentHandle, labels);
    }
}
