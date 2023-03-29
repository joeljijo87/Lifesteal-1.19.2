package org.joelj.lifesteal1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Main extends JavaPlugin implements Listener {

    public ArrayList<String> banned = new ArrayList<>();

    private String name;

    private Boolean contains = false;

    public ItemStack heart = new ItemStack(Material.NETHER_STAR);

    public ItemMeta heart_meta = heart.getItemMeta();

    public ItemStack revival = new ItemStack(Material.BEACON);

    public ItemMeta rim = revival.getItemMeta();

    @Override
    public void onEnable() {
        //config + listener setup
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();

        //heart settings
        heart.addUnsafeEnchantment(Enchantment.DURABILITY, 255);
        heart_meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Heart");

        heart.setItemMeta(heart_meta);

        //revival settings
        revival.addUnsafeEnchantment(Enchantment.DURABILITY, 255);
        rim.setDisplayName(ChatColor.BOLD + "" + ChatColor.BLUE + "Revival");
        revival.setItemMeta(rim);


        //Withdrawl command
        this.getCommand("withdraw").setExecutor(new WithdrawCommand(this));


        //Revival Recipe
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "revive"), revival);

        recipe.shape("RRR",
                     "RNB",
                     "BBB");
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('B', Material.NETHERITE_BLOCK);

        Bukkit.addRecipe(recipe);





    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = (Player) e.getEntity();

        if(this.getConfig().getStringList("Players").contains(name)) {

            if(e.getEntity().getKiller() instanceof Player) {
                Player killer = player.getKiller();

                if(player.getHealth() == 2.0) {
                    banned.add(player.getName());
                    player.kickPlayer(ChatColor.BOLD + "" + ChatColor.RED + "You took way too may Ls now you have to wait till someone revives you L");
                    killer.setHealth(killer.getHealth() + 2.0);
                } else {
                    player.setHealth(player.getHealth() - 2.0);
                    killer.setHealth(killer.getHealth() + 2.0);
                }
            } else {

                if(player.getHealth() == 2.0) {
                    banned.add(player.getName());
                    player.kickPlayer(ChatColor.BOLD + "" + ChatColor.RED + "You took way too may Ls now you have to wait till someone revives you L");
                    player.getWorld().dropItemNaturally(player.getLastDeathLocation(), heart);
                } else {
                    player.setHealth(player.getHealth() - 2.0);
                    player.getWorld().dropItemNaturally(player.getLastDeathLocation(), heart);
                }
            }

        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        name = e.getPlayer().getName();

        if(banned.contains(name)) {
            e.getPlayer().kickPlayer(ChatColor.BOLD + "" + ChatColor.RED + "You took way too may Ls now you have to wait till someone revives you L");
        } else {

            if (!(this.getConfig().getStringList("Players").contains(name))) {
                this.getConfig().set("Players", name);

                e.getPlayer().setHealth(20);

            } else {
                return;
            }
        }


    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(player.getInventory().getItemInMainHand().equals(revival) &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if(player.getInventory().contains(revival)) {
                contains = true;
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "YOU HAVE ACTIVATED THE REVIVAL TOOL! TYPE THE PERSON YOU WOULD LIKE TO REVIVE IN CHAT NOW!");
                name = player.getName();


            } else {
                contains = false;
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "YOU HAVE MISUSED THIS PRIVILEGE! DO NOT MESS WITH THE GODS AGAIN!");
                player.getWorld().strikeLightning(player.getLocation());
            }

        } else if(player.getInventory().getItemInMainHand().equals(heart) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            player.getInventory().remove(heart);
            player.setHealth(player.getHealth() + 2);
        }


    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if(contains == true && e.getPlayer().getName() == name) {
            if(banned.contains(e.getMessage())) {
                banned.remove(e.getMessage());
                contains = false;
                Bukkit.getPlayer(e.getMessage()).setHealth(14.0);
            } else {
                e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "THAT USER DOES NOT EXIST ON THE BANNED LIST PLEASE TYPE ANOTHER ONE!");
            }
        }


    }





}
