package us.cannicide.punish;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class PunishCommand extends JavaPlugin {

    public static PunishCommand getPlugin() {
        return PunishCommand.getPlugin(PunishCommand.class);
    }

    @Override
    public void onDisable() {
        getPlugin().getLogger().info(ChatColor.GOLD + "Disabled PunishCommand");
    }

    @Override
    public void onEnable() {
        getPlugin().getConfig().addDefault("player", "");
        getPlugin().getConfig().options().copyDefaults(true);
        getPlugin().saveConfig();

        getPlugin().getLogger().info(ChatColor.GOLD + "Started PunishCommand");
        this.getCommand("punish").setExecutor(new Punish());
        this.getCommand("punish").setTabCompleter(new Punish());

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PunishExpansion(this).register();
        }
        else {
            getPlugin().getLogger().info(ChatColor.LIGHT_PURPLE + "Could not register PunishCMD Placeholder.");
        }
    }

    public static class Punish implements CommandExecutor, TabCompleter {

        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                if (args.length >= 1 && args[0] != null) {
                    Player player = Bukkit.getPlayerExact(args[0]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "The specified player does not exist.");
                    }
                    else {
                        getPlugin().getConfig().set("player", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc open punishmenu " + sender.getName());
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Punishing " + player.getName() + "...");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Specify a player to punish.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
            }

            return true;
        }

        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

            return null;

        }

    }

    public class PunishExpansion extends PlaceholderExpansion {

        private PunishCommand plugin;

        /**
         * Since we register the expansion inside our own plugin, we
         * can simply use this method here to get an instance of our
         * plugin.
         *
         * @param plugin
         *        The instance of our plugin.
         */
        public PunishExpansion(PunishCommand plugin){
            this.plugin = plugin;
        }

        /**
         * Because this is an internal class,
         * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
         * PlaceholderAPI is reloaded
         *
         * @return true to persist through reloads
         */
        @Override
        public boolean persist(){
            return true;
        }

        /**
         * Because this is a internal class, this check is not needed
         * and we can simply return {@code true}
         *
         * @return Always true since it's an internal class.
         */
        @Override
        public boolean canRegister(){
            return true;
        }

        /**
         * The name of the person who created this expansion should go here.
         * <br>For convenience do we return the author from the plugin.yml
         *
         * @return The name of the author as a String.
         */
        @Override
        public String getAuthor(){
            return plugin.getDescription().getAuthors().toString();
        }

        /**
         * The placeholder identifier should go here.
         * <br>This is what tells PlaceholderAPI to call our onRequest
         * method to obtain a value if a placeholder starts with our
         * identifier.
         * <br>The identifier has to be lowercase and can't contain _ or %
         *
         * @return The identifier in {@code %<identifier>_<value>%} as String.
         */
        @Override
        public String getIdentifier(){
            return "punishcmd";
        }

        /**
         * This is the version of the expansion.
         * <br>You don't have to use numbers, since it is set as a String.
         *
         * For convenience do we return the version from the plugin.yml
         *
         * @return The version as a String.
         */
        @Override
        public String getVersion(){
            return plugin.getDescription().getVersion();
        }

        /**
         * This is the method called when a placeholder with our identifier
         * is found and needs a value.
         * <br>We specify the value identifier in this method.
         * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
         *
         * @param  player
         * @param  identifier
         *         A String containing the identifier/value.
         *
         * @return possibly-null String of the requested identifier.
         */
        @Override
        public String onPlaceholderRequest(Player player, String identifier){

            if(player == null){
                return "";
            }

            // %punishcmd_player%
            if(identifier.equals("player")){
                return plugin.getConfig().getString("player", "");
            }

            return null;
        }
    }

}
