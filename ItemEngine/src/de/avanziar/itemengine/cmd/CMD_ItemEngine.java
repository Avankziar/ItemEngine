package de.avanziar.itemengine.cmd;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.avanziar.itemengine.main.IEMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CMD_ItemEngine implements CommandExecutor 
{
	
	public String tl(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public TextComponent tc(String s)
	{
		return new TextComponent(s);
	}
	
	public void noperm(Player p)
	{
		p.sendMessage(tl(lgg.getString(l+"msg01")));
		return;
	}
	
	public void unvalidArgs(Player p, String cmd)
	{
		p.sendMessage(tl(lgg.getString(l+"msg02").replaceAll("%cmd%", cmd)));
		return;
	}
	
	public void nomysql(Player p)
	{
		p.sendMessage(tl(lgg.getString(l+"msg03")));
		return;
	}
	
	public void nonumber(Player p)
	{
		p.sendMessage(tl(lgg.getString(l+"msg04")));
		return;
	}
	
	public YamlConfiguration lgg = IEMain.lgg;
	public YamlConfiguration itm = IEMain.itm;
	public static String l = "IE."+IEMain.cfg.getString("IE.language")+".";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("&cOnly Player can execute this command!");
			return false;
		}
		Player p = (Player) sender;
		if(!p.hasPermission("itemengine.cmd"))
		{
			noperm(p);
			return false;
		}
		if(args.length==0)
		{
			
		} else if(args[0].equalsIgnoreCase("give")) //Giving the Item per ID from items.yml
		{
			if(args.length==2)
			{
				String m = args[1];
				Material mat = Material.valueOf(m);
				p.getInventory().addItem(createSimpleItem(mat,1));
				
				return true;
			} else if(args.length==3)
			{
				
			} else
			{
				unvalidArgs(p, "/ie give <Material> [Amout]");
			}
		} else if(args[0].equalsIgnoreCase("new")) //Adding a new Item to the items.yml
		{
			
		} else if(args[0].equalsIgnoreCase("set")) //Setting the specs from the <itemname> in the items.yml
		{
			
		} else if(args[0].equalsIgnoreCase("list")) //Shows per page 10 Items from the config
		{
			
		} else if(args[0].equalsIgnoreCase("ymltomysql")) //Load the items from the yml to the mysql (The ID will be always immediately saves in the mysql) Do not override the mysql
		{
			
		} else if(args[0].equalsIgnoreCase("mysqltoyml")) //Load the items from the mysql to the items.yml. Override the yml!
		{
			
		} else if(args[0].equalsIgnoreCase("reload")) //reload the items.yml or the full plugin
		{
			if(args.length!=2)
			{
				unvalidArgs(p, "/ie reload <all/items>");
			}
			if(args[1].equalsIgnoreCase("items"))
			{
				
			} else if(args[1].equals("all"))
			{
				
			}
		}
		return false;
	}
	
	public ItemStack createSimpleItem(Material mat, int amount)
	{
		ItemStack is = new ItemStack(mat, amount);
		return is;
	}
	
	public ItemStack createItemFromYaml(int id, Player p)
	{
		String ID = String.valueOf(id);
		ItemStack is = null;
		if(itm.getString(ID+".material") != null)
		{
			is = new ItemStack(Material.matchMaterial(itm.getString(ID+".material")));
		}
		ItemMeta im = is.getItemMeta();
		if(itm.getString(ID+".name") != null)
		{
			String name = replacer(itm.getString(ID+".name"), p);
			im.setDisplayName(name);
		}
		if(itm.getString(ID+".isunbreakable") != null)
		{
			if(itm.getBoolean(ID+".isunbreakable"))
			{
				im.setUnbreakable(true);
			}
		}
		if(itm.getStringList(ID+".attributes") != null)
		{
			ArrayList<String> atb = (ArrayList<String>) itm.getStringList(ID+".attributes");
			for(int i = 0 ; i < atb.size() ; i++)
			{
				String[] a = atb.get(i).split(";");
				Attribute at = Attribute.valueOf(a[0]);
				double d = new Double(a[1]);
				im.addAttributeModifier(at, new AttributeModifier(at.toString(), d, AttributeModifier.Operation.ADD_NUMBER));
			}
		}
		if(itm.getStringList(ID+".lore") != null)
		{
			ArrayList<String> desc = replacer((ArrayList<String>) itm.getStringList(ID+".lore"), p);
			im.setLore(desc);
		}
		if(itm.getStringList(ID+".itemflag") != null)
		{
			ArrayList<String> itf = (ArrayList<String>) itm.getStringList(ID+".itemflag");
			for(int i = 0 ; i < itf.size() ; i++)
			{
				ItemFlag it = ItemFlag.valueOf(itf.get(i));
				im.addItemFlags(it);
			}
		}
		if(itm.getStringList(ID+".enchantments") != null)
		{
			ArrayList<String> ech = (ArrayList<String>) itm.getStringList(ID+".enchantments");
			for(int i = 0 ; i < ech.size() ; i++)
			{
				String[] a = ech.get(i).split(";");
				Enchantment eh = Enchantment.getByKey(new NamespacedKey(IEMain.plugin, a[0]));
				int d = new Integer(a[1]);
				im.addEnchant(eh,d,true);
			}
		}
		is.setItemMeta(im);
		return is;
	}
	
	public String replacer(String name, Player p)
	{
		name.replaceAll("PLAYER", p.getName());
		name.replaceAll("DATUM", p.getName());
		name.replaceAll("TIME", p.getName());
		return name;
	}
	
	public ArrayList<String> replacer(ArrayList<String> list, Player p)
	{
		list.replaceAll(s-> s.replaceAll("PLAYER", p.getName()));
		list.replaceAll(s-> s.replaceAll("DATUM", p.getName()));
		list.replaceAll(s-> s.replaceAll("TIME", p.getName()));
		return list;
	}

}
