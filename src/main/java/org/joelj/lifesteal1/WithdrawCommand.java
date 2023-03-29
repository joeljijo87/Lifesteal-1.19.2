package org.joelj.lifesteal1;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WithdrawCommand implements CommandExecutor {

    private Main main;

    public WithdrawCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 1) {

                if(Double.parseDouble(args[0]) >= player.getHealth()) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "You do not have enough hearts for this");
                } else {
                    player.getInventory().setItem(Integer.parseInt(args[0]), main.heart);
                    player.setHealth(player.getHealth() - Integer.parseInt(args[0]));
                }

            }


        }

        return false;
    }
}
