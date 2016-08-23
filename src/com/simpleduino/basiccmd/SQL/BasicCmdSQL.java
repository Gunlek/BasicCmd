package com.simpleduino.basiccmd.SQL;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by Simple-Duino on 17/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class BasicCmdSQL {

    private File cfgFile = new File("plugins/BasicCmd/config.yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);
    private Connection con = null;

    public BasicCmdSQL()
    {
        String hostname = cfg.get("sql.hostname").toString();
        String database = cfg.get("sql.database").toString();
        String username = cfg.get("sql.username").toString();
        String password = cfg.get("sql.password").toString();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.con = DriverManager.getConnection("jdbc:mysql://"+hostname+":3306/"+database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!this.isInit())
        {
            this.initDb();
        }
    }

    private void initDb()
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("CREATE TABLE `"+cfg.get("sql.database").toString()+"`.`ban` ( `id` INT NOT NULL AUTO_INCREMENT , `pseudo` VARCHAR(255) NOT NULL , `uuid` TEXT NULL DEFAULT NULL , `duration` VARCHAR(255) NOT NULL , `bandate` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB");
            statement.execute("CREATE TABLE `main_management`.`mute` ( `id` INT NOT NULL AUTO_INCREMENT , `pseudo` VARCHAR(255) NOT NULL , `uuid` TEXT NULL DEFAULT NULL , `active` VARCHAR(255) NOT NULL , `duration` VARCHAR(255) NOT NULL , `mutedate` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isInit()
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_NAME` = \"ban\"");
            ResultSet result = statement.getResultSet();
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void banPlayer(String pseudo, int duration, String reason)
    {
        Calendar cal = Calendar.getInstance();
        String bandate = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(cal.get(Calendar.MONTH))+"-"+Integer.toString(cal.get(Calendar.YEAR))+" "+Integer.toString(cal.get(Calendar.HOUR_OF_DAY))+":"+Integer.toString(cal.get(Calendar.MINUTE))+":"+Integer.toString(cal.get(Calendar.SECOND));

        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                statement.execute("UPDATE ban SET duration=\""+Integer.toString(duration)+"\", bandate=\""+bandate+"\", reason=\""+reason+"\" WHERE pseudo=\""+pseudo+"\"");
            }
            else
            {
                statement.execute("INSERT INTO ban(pseudo, duration, bandate, reason) VALUES(\""+pseudo+"\", \""+Integer.toString(duration)+"\", \""+bandate+"\", \""+reason+"\")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mutePlayer(String pseudo, int duration, String reason)
    {
        Calendar cal = Calendar.getInstance();
        String mutedate = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))+"-"+Integer.toString(cal.get(Calendar.MONTH))+"-"+Integer.toString(cal.get(Calendar.YEAR))+" "+Integer.toString(cal.get(Calendar.HOUR_OF_DAY))+":"+Integer.toString(cal.get(Calendar.MINUTE))+":"+Integer.toString(cal.get(Calendar.SECOND));
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {

            }
            else
            {
                statement.execute("INSERT INTO mute(pseudo, duration, mutedate, reason) VALUES(\""+pseudo+"\", \""+Integer.toString(duration)+"\", \""+mutedate+"\", \""+reason+"\")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMutingUUID(String pseudo, UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                statement.execute("UPDATE mute SET uuid=\""+uuid.toString()+"\" WHERE pseudo=\""+pseudo+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unmute(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                statement.execute("DELETE FROM mute WHERE pseudo=\""+pseudo+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unmute(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                statement.execute("DELETE FROM mute WHERE uuid=\""+uuid.toString()+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getMuteDuration(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                return Integer.parseInt(result.getString("duration"));
            }
            else
            {
                return -10;
            }
        } catch (SQLException e) {
            return -10;
        }
    }

    public int getMuteDuration(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                return Integer.parseInt(result.getString("duration"));
            }
            else
            {
                return -10;
            }
        } catch (SQLException e) {
            return -10;
        }
    }

    public Calendar getMuteDate(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                String banDate = result.getString("mutedate");

                int year = Integer.parseInt(banDate.split(" ")[0].split("-")[2]);
                int month = Integer.parseInt(banDate.split(" ")[0].split("-")[1]);
                int day = Integer.parseInt(banDate.split(" ")[0].split("-")[0]);
                int hours = Integer.parseInt(banDate.split(" ")[1].split(":")[0]);
                int minutes = Integer.parseInt(banDate.split(" ")[1].split(":")[1]);
                int seconds = Integer.parseInt(banDate.split(" ")[1].split(":")[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, seconds);

                return calendar;
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Calendar getMuteDate(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                String banDate = result.getString("mutedate");

                int year = Integer.parseInt(banDate.split(" ")[0].split("-")[2]);
                int month = Integer.parseInt(banDate.split(" ")[0].split("-")[1]);
                int day = Integer.parseInt(banDate.split(" ")[0].split("-")[0]);
                int hours = Integer.parseInt(banDate.split(" ")[1].split(":")[0]);
                int minutes = Integer.parseInt(banDate.split(" ")[1].split(":")[1]);
                int seconds = Integer.parseInt(banDate.split(" ")[1].split(":")[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, seconds);

                return calendar;
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public void unban(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                statement.execute("DELETE FROM ban WHERE pseudo=\""+pseudo+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unban(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                statement.execute("DELETE FROM ban WHERE uuid=\""+uuid.toString()+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMuted(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isMuted(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM mute WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void addUUID(String pseudo, String uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                statement.execute("UPDATE ban SET uuid=\""+uuid+"\" WHERE pseudo=\""+pseudo+"\"");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isBanned(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isBanned(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public int getBanDuration(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                return Integer.parseInt(result.getString("duration"));
            }
            else
            {
                return -10;
            }
        } catch (SQLException e) {
            return -10;
        }
    }

    public int getBanDuration(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                return Integer.parseInt(result.getString("duration"));
            }
            else
            {
                return -10;
            }
        } catch (SQLException e) {
            return -10;
        }
    }

    public Date getBanDate(String pseudo)
    {
        try
        {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"");
            if(result.next())
            {
                String banDate = result.getString("bandate");
                int year = Integer.parseInt(banDate.split("-")[2]);
                int month = Integer.parseInt(banDate.split("-")[1]);
                int day = Integer.parseInt(banDate.split("-")[0]);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                return calendar.getTime();
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Calendar getBanDate(UUID uuid)
    {
        try
        {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM ban WHERE uuid=\""+uuid.toString()+"\"");
            if(result.next())
            {
                String banDate = result.getString("bandate");

                int year = Integer.parseInt(banDate.split(" ")[0].split("-")[2]);
                int month = Integer.parseInt(banDate.split(" ")[0].split("-")[1]);
                int day = Integer.parseInt(banDate.split(" ")[0].split("-")[0]);
                int hours = Integer.parseInt(banDate.split(" ")[1].split(":")[0]);
                int minutes = Integer.parseInt(banDate.split(" ")[1].split(":")[1]);
                int seconds = Integer.parseInt(banDate.split(" ")[1].split(":")[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);
                calendar.set(Calendar.SECOND, seconds);

                return calendar;
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBanReason(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM ban WHERE pseudo=\""+pseudo+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("reason");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBanReason(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM ban WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("reason");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMuteReason(String pseudo)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM mute WHERE pseudo=\""+pseudo+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("reason");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMuteReason(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM mute WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("reason");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
