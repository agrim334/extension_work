package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class IsReachable
  extends ProjectComponent
  implements Condition
{
  public static final int DEFAULT_TIMEOUT = 30;
  private static final int SECOND = 1000;
  public static final String ERROR_NO_HOSTNAME = "No hostname defined";
  public static final String ERROR_BAD_TIMEOUT = "Invalid timeout value";
  private static final String WARN_UNKNOWN_HOST = "Unknown host: ";
  public static final String ERROR_ON_NETWORK = "network error to ";
  public static final String ERROR_BOTH_TARGETS = "Both url and host have been specified";
  public static final String MSG_NO_REACHABLE_TEST = "cannot do a proper reachability test on this Java version";
  public static final String ERROR_BAD_URL = "Bad URL ";
  public static final String ERROR_NO_HOST_IN_URL = "No hostname in URL ";
  @Deprecated
  public static final String METHOD_NAME = "isReachable";
  private String host;
  private String url;
  private int timeout = 30;
  
  public void setHost(String host)
  {
    this.host = host;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }
  
  private boolean isNullOrEmpty(String string)
  {
    return (string == null) || (string.isEmpty());
  }
  
  public boolean eval()
    throws BuildException
  {
    if ((isNullOrEmpty(host)) && (isNullOrEmpty(url))) {
      throw new BuildException("No hostname defined");
    }
    if (timeout < 0) {
      throw new BuildException("Invalid timeout value");
    }
    String target = host;
    if (!isNullOrEmpty(url))
    {
      if (!isNullOrEmpty(host)) {
        throw new BuildException("Both url and host have been specified");
      }
      try
      {
        URL realURL = new URL(url);
        target = realURL.getHost();
        if (isNullOrEmpty(target)) {
          throw new BuildException("No hostname in URL " + url);
        }
      }
      catch (MalformedURLException e)
      {
        throw new BuildException("Bad URL " + url, e);
      }
    }
    log("Probing host " + target, 3);
    try
    {
      address = InetAddress.getByName(target);
    }
    catch (UnknownHostException e1)
    {
      InetAddress address;
      log("Unknown host: " + target);
      return false;
    }
    InetAddress address;
    log("Host address = " + address.getHostAddress(), 3);
    boolean reachable;
    try
    {
      reachable = address.isReachable(timeout * 1000);
    }
    catch (IOException ioe)
    {
      boolean reachable;
      reachable = false;
      log("network error to " + target + ": " + ioe.toString());
    }
    log("host is" + (reachable ? "" : " not") + " reachable", 3);
    return reachable;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsReachable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */