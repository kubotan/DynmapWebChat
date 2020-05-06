package space.gorogoro.dynmapwebchat;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.DynmapWebChatEvent;

import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.japanize.JapanizeType;
import com.gmail.nossr50.api.ChatAPI;

public class DynmapWebChat
  extends JavaPlugin
  implements Listener
{
  DynmapAPI dynmapAPI;
  LunaChat lunaChat;
  
  public void onEnable() {
    try {
      getLogger().info("The Plugin Has Been Enabled!");

      
      if (!getDataFolder().exists()) {
        getDataFolder().mkdir();
      }
      
      File configFile = new File(getDataFolder(), "config.yml");
      if (!configFile.exists()) {
        saveDefaultConfig();
      }
      
      PluginManager pm = getServer().getPluginManager();
      this.dynmapAPI = (DynmapAPI)pm.getPlugin("dynmap");
      this.lunaChat = (LunaChat)pm.getPlugin("LunaChat");
      pm.registerEvents(this, (Plugin)this);
    }
    catch (Exception e) {
      e.printStackTrace();
    } 
  }

  
  public void onDisable() {
    try {
      getLogger().info("The Plugin Has Been Disabled!");
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  @EventHandler
  public void onDynmapWebChat(DynmapWebChatEvent e) {
    String message = e.getMessage();
    String name = e.getName();
    String source = e.getSource();
    for (Player p : getServer().getOnlinePlayers())
    {
      
      p.sendMessage("[" + source.toUpperCase() + "] " + name + ": " + message);
    }
  }
  
  @EventHandler
  public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
    Player p = e.getPlayer();
    if (!(p instanceof Player)) {
      return;
    }
    
    String message = e.getMessage();
    if (message.isEmpty()) {
      return;
    }
    
    if (this.dynmapAPI.getPlayerVisbility(p)) {
      return;
    }

    
    if (this.lunaChat.getLunaChatAPI().getDefaultChannel(p.getName()) != null) {
      return;
    }

    
    if (ChatAPI.isUsingPartyChat(p)) {
      return;
    }

    
    String jpmessage = message;
    if (this.lunaChat.getLunaChatAPI().isPlayerJapanize(p.getName()) && (message.getBytes()).length == message.length() && !message.matches("[ \\uFF61-\\uFF9F]+")) {
      jpmessage = String.valueOf(this.lunaChat.getLunaChatAPI().japanize(
            message, 
            JapanizeType.fromID(getConfig().getString("japanizeType"), JapanizeType.KANA))) + 
        ChatColor.GRAY + " (" + message + ")" + ChatColor.RESET;
    }
    
    for (Player cur : getServer().getOnlinePlayers()) {
      cur.sendMessage(String.valueOf(p.getName()) + ": " + jpmessage);
    }
    e.setCancelled(true);
    getServer().getConsoleSender().sendMessage(String.valueOf(p.getName()) + ": " + jpmessage);
  }
}
