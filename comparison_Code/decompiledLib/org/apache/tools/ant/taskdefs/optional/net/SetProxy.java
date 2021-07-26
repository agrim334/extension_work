package org.apache.tools.ant.taskdefs.optional.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SetProxy
  extends Task
{
  private static final int HTTP_PORT = 80;
  private static final int SOCKS_PORT = 1080;
  protected String proxyHost = null;
  protected int proxyPort = 80;
  private String socksProxyHost = null;
  private int socksProxyPort = 1080;
  private String nonProxyHosts = null;
  private String proxyUser = null;
  private String proxyPassword = null;
  
  public void setProxyHost(String hostname)
  {
    proxyHost = hostname;
  }
  
  public void setProxyPort(int port)
  {
    proxyPort = port;
  }
  
  public void setSocksProxyHost(String host)
  {
    socksProxyHost = host;
  }
  
  public void setSocksProxyPort(int port)
  {
    socksProxyPort = port;
  }
  
  public void setNonProxyHosts(String nonProxyHosts)
  {
    this.nonProxyHosts = nonProxyHosts;
  }
  
  public void setProxyUser(String proxyUser)
  {
    this.proxyUser = proxyUser;
  }
  
  public void setProxyPassword(String proxyPassword)
  {
    this.proxyPassword = proxyPassword;
  }
  
  public void applyWebProxySettings()
  {
    boolean settingsChanged = false;
    boolean enablingProxy = false;
    Properties sysprops = System.getProperties();
    if (proxyHost != null)
    {
      settingsChanged = true;
      if (!proxyHost.isEmpty())
      {
        traceSettingInfo();
        enablingProxy = true;
        sysprops.put("http.proxyHost", proxyHost);
        String portString = Integer.toString(proxyPort);
        sysprops.put("http.proxyPort", portString);
        sysprops.put("https.proxyHost", proxyHost);
        sysprops.put("https.proxyPort", portString);
        sysprops.put("ftp.proxyHost", proxyHost);
        sysprops.put("ftp.proxyPort", portString);
        if (nonProxyHosts != null)
        {
          sysprops.put("http.nonProxyHosts", nonProxyHosts);
          sysprops.put("https.nonProxyHosts", nonProxyHosts);
          sysprops.put("ftp.nonProxyHosts", nonProxyHosts);
        }
        if (proxyUser != null)
        {
          sysprops.put("http.proxyUser", proxyUser);
          sysprops.put("http.proxyPassword", proxyPassword);
        }
      }
      else
      {
        log("resetting http proxy", 3);
        sysprops.remove("http.proxyHost");
        sysprops.remove("http.proxyPort");
        sysprops.remove("http.proxyUser");
        sysprops.remove("http.proxyPassword");
        sysprops.remove("https.proxyHost");
        sysprops.remove("https.proxyPort");
        sysprops.remove("ftp.proxyHost");
        sysprops.remove("ftp.proxyPort");
      }
    }
    if (socksProxyHost != null)
    {
      settingsChanged = true;
      if (!socksProxyHost.isEmpty())
      {
        enablingProxy = true;
        sysprops.put("socksProxyHost", socksProxyHost);
        sysprops.put("socksProxyPort", Integer.toString(socksProxyPort));
        if (proxyUser != null)
        {
          sysprops.put("java.net.socks.username", proxyUser);
          sysprops.put("java.net.socks.password", proxyPassword);
        }
      }
      else
      {
        log("resetting socks proxy", 3);
        sysprops.remove("socksProxyHost");
        sysprops.remove("socksProxyPort");
        sysprops.remove("java.net.socks.username");
        sysprops.remove("java.net.socks.password");
      }
    }
    if (proxyUser != null) {
      if (enablingProxy) {
        Authenticator.setDefault(new ProxyAuth(proxyUser, proxyPassword, null));
      } else if (settingsChanged) {
        Authenticator.setDefault(new ProxyAuth("", "", null));
      }
    }
  }
  
  private void traceSettingInfo()
  {
    log("Setting proxy to " + (
      proxyHost != null ? proxyHost : "''") + ":" + proxyPort, 3);
  }
  
  public void execute()
    throws BuildException
  {
    applyWebProxySettings();
  }
  
  private static final class ProxyAuth
    extends Authenticator
  {
    private PasswordAuthentication auth;
    
    private ProxyAuth(String user, String pass)
    {
      auth = new PasswordAuthentication(user, pass.toCharArray());
    }
    
    protected PasswordAuthentication getPasswordAuthentication()
    {
      return auth;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.net.SetProxy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */