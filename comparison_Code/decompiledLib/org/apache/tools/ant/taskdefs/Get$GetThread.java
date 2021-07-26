package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

class Get$GetThread
  extends Thread
{
  private final URL source;
  private final File dest;
  private final boolean hasTimestamp;
  private final long timestamp;
  private final Get.DownloadProgress progress;
  private final int logLevel;
  private boolean success = false;
  private IOException ioexception = null;
  private BuildException exception = null;
  private InputStream is = null;
  private OutputStream os = null;
  private URLConnection connection;
  private int redirections = 0;
  private String userAgent = null;
  
  Get$GetThread(Get paramGet, URL source, File dest, boolean h, long t, Get.DownloadProgress p, int l, String userAgent)
  {
    this.source = source;
    this.dest = dest;
    hasTimestamp = h;
    timestamp = t;
    progress = p;
    logLevel = l;
    this.userAgent = userAgent;
  }
  
  public void run()
  {
    try
    {
      success = get();
    }
    catch (IOException ioex)
    {
      ioexception = ioex;
    }
    catch (BuildException bex)
    {
      exception = bex;
    }
  }
  
  private boolean get()
    throws IOException, BuildException
  {
    connection = openConnection(source);
    if (connection == null) {
      return false;
    }
    boolean downloadSucceeded = downloadFile();
    if ((downloadSucceeded) && (Get.access$000(this$0))) {
      updateTimeStamp();
    }
    return downloadSucceeded;
  }
  
  private boolean redirectionAllowed(URL aSource, URL aDest)
  {
    if ((!aSource.getProtocol().equals(aDest.getProtocol())) && (
      (!"http".equals(aSource.getProtocol())) || (!"https".equals(aDest
      .getProtocol()))))
    {
      String message = "Redirection detected from " + aSource.getProtocol() + " to " + aDest.getProtocol() + ". Protocol switch unsafe, not allowed.";
      if (Get.access$100(this$0))
      {
        this$0.log(message, logLevel);
        return false;
      }
      throw new BuildException(message);
    }
    redirections += 1;
    if (redirections > 25)
    {
      String message = "More than 25 times redirected, giving up";
      if (Get.access$100(this$0))
      {
        this$0.log("More than 25 times redirected, giving up", logLevel);
        return false;
      }
      throw new BuildException("More than 25 times redirected, giving up");
    }
    return true;
  }
  
  private URLConnection openConnection(URL aSource)
    throws IOException
  {
    URLConnection connection = aSource.openConnection();
    if (hasTimestamp) {
      connection.setIfModifiedSince(timestamp);
    }
    connection.addRequestProperty("User-Agent", userAgent);
    String up;
    if ((Get.access$200(this$0) != null) || (Get.access$300(this$0) != null))
    {
      up = Get.access$200(this$0) + ":" + Get.access$300(this$0);
      
      Get.Base64Converter encoder = new Get.Base64Converter();
      String encoding = encoder.encode(up.getBytes());
      connection.setRequestProperty("Authorization", "Basic " + encoding);
    }
    if (Get.access$400(this$0)) {
      connection.setRequestProperty("Accept-Encoding", "gzip");
    }
    for (Map.Entry<String, String> header : Get.access$500(this$0).entrySet())
    {
      this$0.log(String.format("Adding header '%s' ", new Object[] { header.getKey() }));
      connection.setRequestProperty((String)header.getKey(), (String)header.getValue());
    }
    if ((connection instanceof HttpURLConnection))
    {
      ((HttpURLConnection)connection).setInstanceFollowRedirects(false);
      connection.setUseCaches(Get.access$600(this$0));
    }
    try
    {
      connection.connect();
    }
    catch (NullPointerException e)
    {
      throw new BuildException("Failed to parse " + source.toString(), e);
    }
    if ((connection instanceof HttpURLConnection))
    {
      HttpURLConnection httpConnection = (HttpURLConnection)connection;
      int responseCode = httpConnection.getResponseCode();
      if (isMoved(responseCode))
      {
        String newLocation = httpConnection.getHeaderField("Location");
        
        String message = aSource + (responseCode == 301 ? " permanently" : "") + " moved to " + newLocation;
        this$0.log(message, logLevel);
        URL newURL = new URL(aSource, newLocation);
        if (!redirectionAllowed(aSource, newURL)) {
          return null;
        }
        return openConnection(newURL);
      }
      long lastModified = httpConnection.getLastModified();
      if ((responseCode == 304) || ((lastModified != 0L) && (hasTimestamp) && (timestamp >= lastModified)))
      {
        this$0.log("Not modified - so not downloaded", logLevel);
        return null;
      }
      if (responseCode == 401)
      {
        String message = "HTTP Authorization failure";
        if (Get.access$100(this$0))
        {
          this$0.log("HTTP Authorization failure", logLevel);
          return null;
        }
        throw new BuildException("HTTP Authorization failure");
      }
    }
    return connection;
  }
  
  private boolean isMoved(int responseCode)
  {
    return (responseCode == 301) || (responseCode == 302) || (responseCode == 303) || (responseCode == 307);
  }
  
  private boolean downloadFile()
    throws IOException
  {
    for (int i = 0; i < Get.access$700(this$0); i++) {
      try
      {
        is = connection.getInputStream();
      }
      catch (IOException ex)
      {
        this$0.log("Error opening connection " + ex, logLevel);
      }
    }
    if (is == null)
    {
      this$0.log("Can't get " + source + " to " + dest, logLevel);
      if (Get.access$100(this$0)) {
        return false;
      }
      throw new BuildException("Can't get " + source + " to " + dest, this$0.getLocation());
    }
    if ((Get.access$400(this$0)) && 
      ("gzip".equals(connection.getContentEncoding()))) {
      is = new GZIPInputStream(is);
    }
    os = Files.newOutputStream(dest.toPath(), new OpenOption[0]);
    progress.beginDownload();
    boolean finished = false;
    try
    {
      byte[] buffer = new byte[102400];
      int length;
      while ((!isInterrupted()) && ((length = is.read(buffer)) >= 0))
      {
        os.write(buffer, 0, length);
        progress.onTick();
      }
      finished = !isInterrupted();
    }
    finally
    {
      FileUtils.close(os);
      FileUtils.close(is);
      if (!finished) {
        dest.delete();
      }
    }
    progress.endDownload();
    return true;
  }
  
  private void updateTimeStamp()
  {
    long remoteTimestamp = connection.getLastModified();
    if (Get.access$800(this$0))
    {
      Date t = new Date(remoteTimestamp);
      this$0.log("last modified = " + t.toString() + (
        remoteTimestamp == 0L ? " - using current time instead" : ""), logLevel);
    }
    if (remoteTimestamp != 0L) {
      Get.access$900().setFileLastModified(dest, remoteTimestamp);
    }
  }
  
  boolean wasSuccessful()
    throws IOException, BuildException
  {
    if (ioexception != null) {
      throw ioexception;
    }
    if (exception != null) {
      throw exception;
    }
    return success;
  }
  
  void closeStreams()
  {
    interrupt();
    FileUtils.close(os);
    FileUtils.close(is);
    if ((!success) && (dest.exists())) {
      dest.delete();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Get.GetThread
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */