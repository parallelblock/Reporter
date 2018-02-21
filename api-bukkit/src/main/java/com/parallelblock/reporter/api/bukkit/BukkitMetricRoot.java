package com.parallelblock.reporter.api.bukkit;

import com.parallelblock.reporter.api.MetricRoot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class BukkitMetricRoot extends MetricRoot implements Listener {

    // For bukkit, the handleOwner is the plugin object, so that the reporter plugin can track its lifecycle
    public void attach(Plugin plugin) {
        if (handleOwner != null) {
            if (handleOwner == plugin) {
                throw new IllegalStateException("Attempted double attach with same plugin context");
            }
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        handleOwner = plugin;
    }

    @EventHandler
    public void onEnable(PluginEnableEvent e) throws Throwable {
        if (e.getPlugin() == handleOwner) {
            // something went wrong
        } else if (isReporter(e.getPlugin())) {
            // attempt to register ourselves into the plugin
            MethodHandle h = MethodHandles.lookup().findVirtual(e.getPlugin().getClass(),
                    "registerHandle", MethodType.methodType(Void.class, Object.class));
            h.invoke(e.getPlugin(), handleOwner);
            updateCentralAPI(e.getPlugin(), handleOwner);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        if (isReporter(e.getPlugin()))
            attach(null);
    }

    private boolean isReporter(Plugin plugin) {
        return plugin.getName().equals("Reporter");
    }
}
