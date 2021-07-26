package org.apache.tools.ant.taskdefs.condition;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class Http
  extends ProjectComponent
  implements Condition
{
  private static final int ERROR_BEGINS = 400;
  private static final String DEFAULT_REQUEST_METHOD = "GET";
  private String spec = null;
  private String requestMethod = "GET";
  private boolean followRedirects = true;
  private int errorsBeginAt = 400;
  private int readTimeout = 0;
  
  public void setUrl(String url)
  {
    spec = url;
  }
  
  public void setErrorsBeginAt(int errorsBeginAt)
  {
    this.errorsBeginAt = errorsBeginAt;
  }
  
  public void setRequestMethod(String method)
  {
    requestMethod = (method == null ? "GET" : method.toUpperCase(Locale.ENGLISH));
  }
  
  public void setFollowRedirects(boolean f)
  {
    followRedirects = f;
  }
  
  public void setReadTimeout(int t)
  {
    if (t >= 0) {
      readTimeout = t;
    }
  }
  
  public boolean eval()
    throws BuildException
  {
    if (spec == null) {
      throw new BuildException("No url specified in http condition");
    }
    log("Checking for " + spec, 3);
    try
    {
      URL url = new URL(spec);
      try
      {
        URLConnection conn = url.openConnection();
        if ((conn instanceof HttpURLConnection))
        {
          HttpURLConnection http = (HttpURLConnection)conn;
          http.setRequestMethod(requestMethod);
          http.setInstanceFollowRedirects(followRedirects);
          http.setReadTimeout(readTimeout);
          int code = http.getResponseCode();
          log("Result code for " + spec + " was " + code, 3);
          
          return (code > 0) && (code < errorsBeginAt);
        }
      }
      catch (ProtocolException pe)
      {
        throw new BuildException("Invalid HTTP protocol: " + requestMethod, pe);
      }
      catch (SocketTimeoutException ste)
      {
        return false;
      }
      catch (IOException e)
      {
        return false;
      }
    }
    catch (MalformedURLException e)
    {
      throw new BuildException("Badly formed URL: " + spec, e);
    }
    return true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Http
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */