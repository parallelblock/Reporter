package com.parallelblock.reporter.api.bungee;

import com.parallelblock.reporter.api.MetricRoot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMetricRoot extends MetricRoot implements Listener {

    // for bungee, the handleOwner is the plugin object. However, bungee cannot hot reload plugins, so we just see if
    // Reporter is out there somewhere, and attempt to bind.
    public void attach(Plugin plugin) {
        if (handleOwner != null) {
            if (handleOwner == plugin) {
                throw new IllegalStateException("Attempted double attach with the same plugin context");
            }
            Plugin genHandle;
            if ((genHandle = ProxyServer.getInstance().getPluginManager().getPlugin("Reporter")) != null) {
                // got the handle!
                try {
                    updateCentralAPI(genHandle, handleOwner);
                } catch (Throwable throwable) {
                    throw new RuntimeException("Failed to bind to Reporter plugin!");
                }
            }
        }
        handleOwner = plugin;
    }
}
