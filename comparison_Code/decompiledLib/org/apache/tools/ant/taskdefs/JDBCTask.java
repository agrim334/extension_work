package org.apache.tools.ant.taskdefs;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public abstract class JDBCTask
  extends Task
{
  private static final int HASH_TABLE_SIZE = 3;
  private static final Hashtable<String, AntClassLoader> LOADER_MAP = new Hashtable(3);
  private boolean caching = true;
  private Path classpath;
  private AntClassLoader loader;
  private boolean autocommit = false;
  private String driver = null;
  private String url = null;
  private String userId = null;
  private String password = null;
  private String rdbms = null;
  private String version = null;
  private boolean failOnConnectionError = true;
  private List<Property> connectionProperties = new ArrayList();
  
  public void setClasspath(Path classpath)
  {
    this.classpath = classpath;
  }
  
  public void setCaching(boolean enable)
  {
    caching = enable;
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    return classpath.createPath();
  }
  
  public void setClasspathRef(Reference r)
  {
    createClasspath().setRefid(r);
  }
  
  public void setDriver(String driver)
  {
    this.driver = driver.trim();
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public void setAutocommit(boolean autocommit)
  {
    this.autocommit = autocommit;
  }
  
  public void setRdbms(String rdbms)
  {
    this.rdbms = rdbms;
  }
  
  public void setVersion(String version)
  {
    this.version = version;
  }
  
  public void setFailOnConnectionError(boolean b)
  {
    failOnConnectionError = b;
  }
  
  protected boolean isValidRdbms(Connection conn)
  {
    if ((rdbms == null) && (version == null)) {
      return true;
    }
    try
    {
      DatabaseMetaData dmd = conn.getMetaData();
      if (rdbms != null)
      {
        String theVendor = dmd.getDatabaseProductName().toLowerCase();
        
        log("RDBMS = " + theVendor, 3);
        if ((theVendor == null) || (!theVendor.contains(rdbms)))
        {
          log("Not the required RDBMS: " + rdbms, 3);
          return false;
        }
      }
      if (version != null)
      {
        String theVersion = dmd.getDatabaseProductVersion().toLowerCase(Locale.ENGLISH);
        
        log("Version = " + theVersion, 3);
        if ((theVersion == null) || (
          (!theVersion.startsWith(version)) && 
          (!theVersion.contains(" " + version))))
        {
          log("Not the required version: \"" + version + "\"", 3);
          return false;
        }
      }
    }
    catch (SQLException e)
    {
      log("Failed to obtain required RDBMS information", 0);
      return false;
    }
    return true;
  }
  
  protected static Hashtable<String, AntClassLoader> getLoaderMap()
  {
    return LOADER_MAP;
  }
  
  protected AntClassLoader getLoader()
  {
    return loader;
  }
  
  public void addConnectionProperty(Property var)
  {
    connectionProperties.add(var);
  }
  
  protected Connection getConnection()
    throws BuildException
  {
    if (userId == null) {
      throw new BuildException("UserId attribute must be set!", getLocation());
    }
    if (password == null) {
      throw new BuildException("Password attribute must be set!", getLocation());
    }
    if (url == null) {
      throw new BuildException("Url attribute must be set!", getLocation());
    }
    try
    {
      log("connecting to " + getUrl(), 3);
      Properties info = new Properties();
      info.put("user", getUserId());
      info.put("password", getPassword());
      for (Property p : connectionProperties)
      {
        String name = p.getName();
        String value = p.getValue();
        if ((name == null) || (value == null))
        {
          log("Only name/value pairs are supported as connection properties.", 1);
        }
        else
        {
          log("Setting connection property " + name + " to " + value, 3);
          
          info.put(name, value);
        }
      }
      Connection conn = getDriver().connect(getUrl(), info);
      if (conn == null) {
        throw new SQLException("No suitable Driver for " + url);
      }
      conn.setAutoCommit(autocommit);
      return conn;
    }
    catch (SQLException e)
    {
      if (failOnConnectionError) {
        throw new BuildException(e, getLocation());
      }
      log("Failed to connect: " + e.getMessage(), 1);
    }
    return null;
  }
  
  private Driver getDriver()
    throws BuildException
  {
    if (driver == null) {
      throw new BuildException("Driver attribute must be set!", getLocation());
    }
    try
    {
      Class<? extends Driver> dc;
      Class<? extends Driver> dc;
      if (classpath != null)
      {
        synchronized (LOADER_MAP)
        {
          if (caching) {
            loader = ((AntClassLoader)LOADER_MAP.get(driver));
          }
          if (loader == null)
          {
            log("Loading " + driver + " using AntClassLoader with classpath " + classpath, 3);
            
            loader = getProject().createClassLoader(classpath);
            if (caching) {
              LOADER_MAP.put(driver, loader);
            }
          }
          else
          {
            log("Loading " + driver + " using a cached AntClassLoader.", 3);
          }
        }
        dc = loader.loadClass(driver).asSubclass(Driver.class);
      }
      else
      {
        log("Loading " + driver + " using system loader.", 3);
        
        dc = Class.forName(driver).asSubclass(Driver.class);
      }
      driverInstance = (Driver)dc.newInstance();
    }
    catch (ClassNotFoundException e)
    {
      Driver driverInstance;
      throw new BuildException("Class Not Found: JDBC driver " + driver + " could not be loaded", e, getLocation());
    }
    catch (IllegalAccessException e)
    {
      throw new BuildException("Illegal Access: JDBC driver " + driver + " could not be loaded", e, getLocation());
    }
    catch (InstantiationException e)
    {
      throw new BuildException("Instantiation Exception: JDBC driver " + driver + " could not be loaded", e, getLocation());
    }
    Driver driverInstance;
    return driverInstance;
  }
  
  public void isCaching(boolean value)
  {
    caching = value;
  }
  
  public Path getClasspath()
  {
    return classpath;
  }
  
  public boolean isAutocommit()
  {
    return autocommit;
  }
  
  public String getUrl()
  {
    return url;
  }
  
  public String getUserId()
  {
    return userId;
  }
  
  public void setUserid(String userId)
  {
    this.userId = userId;
  }
  
  public String getPassword()
  {
    return password;
  }
  
  public String getRdbms()
  {
    return rdbms;
  }
  
  public String getVersion()
  {
    return version;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.JDBCTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */