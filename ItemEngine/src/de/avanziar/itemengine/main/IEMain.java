package de.avanziar.itemengine.main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.avanziar.itemengine.cmd.CMD_ItemEngine;

public class IEMain extends JavaPlugin
{
	public static IEMain plugin;
	public static File c = null;
	public static File i = null;
	public static File l = null;
	public static File r = null;
	public static YamlConfiguration cfg = new YamlConfiguration();
	public static YamlConfiguration itm = new YamlConfiguration();
	public static YamlConfiguration lgg = new YamlConfiguration();
	public static YamlConfiguration rdm = new YamlConfiguration();
	public static Connection connection;
	public static String host;
	public static String database;
	public static String username;
	public static String password;
	public static String table;
	public static String arguments;
	public static int port;
	
	
	@Override
	public void onEnable()
	{
		plugin = this;
		c = new File(getDataFolder(), "config.yml");
		i = new File(getDataFolder(), "items.yml");
		l = new File(getDataFolder(), "language.yml");
		r = new File(getDataFolder(), "readme.yml");
		
		mkdir();
	    loadYamls();
		
	    getCommand("scc").setExecutor(new CMD_ItemEngine());
	    
	    boolean mysql = cfg.getString("IE.mysql.status").equals("on");
	    
		 if(mysql)
		 {
			 host = cfg.getString("IE.mysql.host"); 
			 port = cfg.getInt("IE.mysql.port");
			 database = cfg.getString("IE.mysql.database");
			 username = cfg.getString("IE.mysql.username");
			 password = cfg.getString("IE.mysql.password");
			 table = "IE_ITEMS";
			 MySQLSetup(plugin, connection);
			 TableSetup("CREATE TABLE IF NOT EXISTS "+table+
		    		"()"); //TODO
		 }
		
		getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Item Engine" + ChatColor.WHITE + " is " + ChatColor.DARK_GREEN + "running" + ChatColor.WHITE + "!");
		getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Item Engine" + ChatColor.WHITE + " is " +
			    ChatColor.DARK_GREEN + "development " + ChatColor.WHITE + "by " + ChatColor.GOLD + "Avankziar"+ ChatColor.WHITE+ "!");
	}
	
	@Override
	public void onDisable()
	{
		saveC();
		saveL();
	}
	
	private void mkdir() 
	{
		if(!c.exists()) 
		{
			saveResource("config.yml", false);
		}
		if(!i.exists()) 
		{
			saveResource("items.yml", false);
		}
		if(!l.exists())
		{
			saveResource("language.yml", false);
		}
		if(!r.exists())
		{
			saveResource("readme.yml", false);
		}
	}
	
	public void saveC() 
	{
	    try 
	    {
	        cfg.save(c);
	    } catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	}
	
	public void saveL() 
	{
	    try 
	    {
	        lgg.save(l);
	    } catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	}
	
	public void LoadI() 
	{
		try 
		{
			itm.load(i);
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
	}
	
	public void loadYamls() 
	{
		try 
		{
			cfg.load(c);
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		try 
		{
			itm.load(i);
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
		try 
		{
			lgg.load(l);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		try 
		{
			rdm.load(r);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void MySQLSetup(Plugin plugin, Connection connection)
	{
		try
		{
			synchronized (plugin)
			{
				if(connection != null && !connection.isClosed())
				{
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database,username,password));
			}
		} catch(SQLException e)
		{
			e.printStackTrace();
		} catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	 }
	 
	public void TableSetup(String qyr)
	{
		try
		{
			PreparedStatement statement = getConnection()
					.prepareStatement(qyr);
			statement.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	 
	public Connection getConnection()
	{
		return connection;
	}
		
	public static void setConnection(Connection connection)
	{
		IEMain.connection = connection;
	}
}
