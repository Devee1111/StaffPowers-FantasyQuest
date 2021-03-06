package me.Devee1111.Bungee;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


import me.Devee1111.Bungee.StaffPowers;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffSql {
	
	//Storing variables and such for use
	private static StaffPowers inst = StaffPowers.getInstance();
	private static String prefix = "[Staff] ";
	private static String db = "/data.db";
	private static String file = ("jdbc:sqlite:"+inst.getDataFolder().getAbsolutePath()+db);
	
	//Making an instance of our main class
	public StaffPowers instance;
	public StaffSql(StaffPowers p) {
		this.instance = p;
	}
	
	//Globalizing the error for cleaner stacktraces
	private static void error(SQLException ex, String method) {
		inst.getLogger().log(Level.SEVERE,"################################################################");
		inst.getLogger().log(Level.SEVERE,prefix+"SilkySql has encountered an error. The following was given:");
		inst.getLogger().log(Level.SEVERE,"---------------------- [ Function ] ----------------------");
		inst.getLogger().log(Level.SEVERE, method+"()");
		inst.getLogger().log(Level.SEVERE,"---------------------- [ Stacktrace ] ----------------------");
		ex.printStackTrace();
		inst.getLogger().log(Level.SEVERE,"################################################################");
		inst.getLogger().log(Level.SEVERE,prefix+"A fatal error has occured while operating the database. Plugin may not function as desired.");
	}
	
	// Used for other method to get a connection and adjust file as desired.
	public static Connection connect() {
		String url = file;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			error(ex,"connect");
		}
		if(conn == null) {
			inst.getLogger().log(Level.SEVERE,prefix+"Failed to connect to database file - "+file);
		}
		return conn;
	}
	
	/*
	 * Section - PlayerDataChecks 
	 * Created by - Devee1111 on 8/6/19
	 */
	
	public static void setDuty(String uuid, boolean status) {
		String sql = "update playerdata set onduty = ? where uuid = ?;";
		
		try {
			Connection con = connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setBoolean(1,status);
			ps.setString(2,uuid);
			ps.executeUpdate();
			ps.close();
			con.close();
		} catch(SQLException ex) { error(ex,"setDuty"); }
	}
	
	public static boolean onDuty(String uuid) {
		boolean onduty = false;
		String sql = "select onduty from playerdata where uuid = ?;";
		try {
			Connection con = connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1,uuid);
			ResultSet rs = ps.executeQuery();
			onduty = rs.getBoolean(1);
			rs.close();
			ps.close();
			con.close();
		} catch (SQLException ex) { error(ex,"onDuty"); }
		return onduty;
	}
	
	/*
	 * Section is to ensure playerdata is stored
	 */
	
	public static boolean playerExists(String uuid) {
		boolean exists = false;
		String sql = "select * from playerdata where uuid = ?;";
		try {
			Connection con = connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, uuid);
			ResultSet rs = ps.executeQuery();
			if(rs.isBeforeFirst()) {
				exists = true;
			}
			rs.close();
			ps.close();
			con.close();
		} catch(SQLException ex) {error(ex,"playerExists");}
		return exists;
	}
	
	public static void newPlayer(ProxiedPlayer p) {
		String sql = "insert into playerdata(uuid,ign,vanished,onduty) values(?,?,?,?);";
		try {
			Connection con = connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, p.getName());
			ps.setBoolean(3, false);
			ps.setBoolean(4, true);
			ps.executeUpdate();
			ps.close();
			con.close();
		} catch(SQLException ex) {error(ex,"newPlayer");}
	}
	
	public static void updateName(ProxiedPlayer p) {
		String sql = "update playerdata set ign = ? where uuid = ?;";
		try {
			Connection con = connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, p.getName());
			ps.setString(2, p.getUniqueId().toString());
			ps.close();
			con.close();
		} catch(SQLException ex) {error(ex,"updateName");}
	}
	
	/*
	 * This Section is to ensure our SQL database exists, and has been setup.
	 */
	
	/* This is called onEnabled(), it makes sure we have a file, and that the file is working. */
	public static void loadSqlFile() {
		//Loading Drivers for bungee
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			//TODO announce failed to load drivers
		}
		File File = new File(inst.getDataFolder().getAbsolutePath()+db);
		if(!File.exists()) {
			try {
				File.createNewFile();
			} catch (IOException ex) {
				inst.getLogger().log(Level.SEVERE,prefix+"Error occured creating database file!");
				ex.printStackTrace();
			}
		}
		Connection conn = null;
		try {
			String url = file;
			conn = DriverManager.getConnection(url);
			inst.getLogger().log(Level.INFO,prefix+"Connection to SQL database has been established.");
			
		} catch (SQLException ex) {
			error(ex,"loadSqlFile");
		} finally {
			try {
				if (conn != null) {
					createPlayerData();
					conn.close();
				}
			} catch (SQLException ex) {
				error(ex,"loadSqlFile");
			}
		}
	}
	/* Method designed for loadSql, it ensures our main data table is created */
	public static void createPlayerData() {
		String url = file;
		String sql = "create table if not exists playerdata(\"uuid\" text, \"ign\" text,\"searchign\" text, \"vanished\" boolean, \"onduty\" boolean);";
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			stat.execute(sql);
			stat.close();
			conn.close();
		} catch(SQLException ex) { 
			error(ex,"createData");
		}
	}
}