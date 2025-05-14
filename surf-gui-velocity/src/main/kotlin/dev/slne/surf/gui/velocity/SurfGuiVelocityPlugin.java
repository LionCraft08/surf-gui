package dev.slne.surf.gui.velocity;
//
//
//import com.github.retrooper.packetevents.PacketEvents;
//import com.google.inject.Inject;
//import com.velocitypowered.api.command.CommandMeta;
//import com.velocitypowered.api.event.Subscribe;
//import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
//import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
//import com.velocitypowered.api.plugin.Plugin;
//import com.velocitypowered.api.plugin.annotation.DataDirectory;
//import com.velocitypowered.api.proxy.ProxyServer;
//import dev.slne.surf.gui.SurfGuiApi;
//import dev.slne.surf.gui.velocity.commands.MenuCommand;
//import dev.slne.surf.gui.velocity.commands.menuSubCommands.OpenSpecificCommand;
//import dev.slne.surf.gui.velocity.eventlistener.PlayerConnectionListeners;
//import dev.slne.surf.gui.velocity.eventlistener.PluginMessageReceiver;
//import io.github.retrooper.packetevents.velocity.factory.VelocityPacketEventsBuilder;
//import org.slf4j.Logger;
//import java.nio.file.Path;
//
//@Plugin(id = "surf-gui-velocity", name = "surf-gui-velocity", version = "1.0",description = "The Velocity implementation of SurfGUI",authors = {"LionCraft"})
//public class SurfGuiVelocityPlugin  {
//    public final ProxyServer server;
//    public final Logger logger;
//    public final Path dataDirectory;
//
//    @Inject
//    public SurfGuiVelocityPlugin(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
//        instance = this;
//        this.server = server;
//        this.logger = logger;
//        this.dataDirectory = dataDirectory;
//        logger.info("Created Proxy-Side Plugin Surf-GUI");
//    }
//
//
//    @Subscribe
//    public void onProxyInitialization( ProxyInitializeEvent e) {
//        PacketEvents.setAPI(VelocityPacketEventsBuilder.build(server, server.getPluginManager().ensurePluginContainer(this), logger, dataDirectory));
//
//        //Plugin-Messaging
//        server.getChannelRegistrar().register(PluginMessageReceiver.Companion.getMenu_open_requests());
//
//        //Listeners
//        server.getEventManager().register(this,new  PluginMessageReceiver());
//        server.getEventManager().register(this, new PlayerConnectionListeners());
//
//        //Commands
//        server.getCommandManager().register(server.getCommandManager().metaBuilder("debug").plugin(this).build(),
//                new OpenSpecificCommand());
//
//        server.getCommandManager().register(server.getCommandManager().metaBuilder("menu").aliases("surf-menu").plugin(this).build(),new MenuCommand());
//
//        logger.debug("Initialized Proxy-Side Plugin Surf-GUI");
//    }
//    @Subscribe
//    public void onShutdown(ProxyShutdownEvent e ){
//        SurfGuiApi.Companion.getInstance().terminate();
//    }
//
//
//    private static SurfGuiVelocityPlugin instance;
//    public static SurfGuiVelocityPlugin getInstance(){return instance;}
//
//}
