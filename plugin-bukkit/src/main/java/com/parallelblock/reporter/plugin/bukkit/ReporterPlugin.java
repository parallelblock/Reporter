package com.parallelblock.reporter.plugin.bukkit;

import com.parallelblock.reporter.plugin.common.CentralHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReporterPlugin extends JavaPlugin implements Listener {

    // damn you java for non-multiple-inheritance!!!
    private final CentralHandler centralHandler = new CentralHandler();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        // todo - set up exposure configuration
    }

    public Class[] getHandleTypes() {
        return centralHandler.getHandleTypes();
    }

    public Object register(Object handleOwner, Object handleBase, Object parentHandle, String[] labels) throws Throwable {
        return centralHandler.register(handleOwner, handleBase, parentHandle, labels);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        centralHandler.unregisterHandlesFromOwner(e.getPlugin());
    }
}
