package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.email.Header;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.URLProvider;
import org.apache.tools.ant.types.resources.URLResource;
import org.apache.tools.ant.util.Base64Converter;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

public class Get
  extends Task
{
  private static final int NUMBER_RETRIES = 3;
  private static final int DOTS_PER_LINE = 50;
  private static final int BIG_BUFFER_SIZE = 102400;
  private static final FileUtils FILE_UTILS = ;
  private static final int REDIRECT_LIMIT = 25;
  private static final int HTTP_MOVED_TEMP = 307;
  private static final String HTTP = "http";
  private static final String HTTPS = "https";
  private static final String DEFAULT_AGENT_PREFIX = "Apache Ant";
  private static final String GZIP_CONTENT_ENCODING = "gzip";
  private final Resources sources = new Resources();
  private File destination;
  private boolean verbose = false;
  private boolean quiet = false;
  private boolean useTimestamp = false;
  private boolean ignoreErrors = false;
  private String uname = null;
  private String pword = null;
  private long maxTime = 0L;
  private int numberRetries = 3;
  private boolean skipExisting = false;
  private boolean httpUseCaches = true;
  private boolean tryGzipEncoding = false;
  private Mapper mapperElement = null;
  private String userAgent = System.getProperty("ant.http.agent", "Apache Ant/" + 
  
    Main.getShortAntVersion());
  private Map<String, String> headers = new LinkedHashMap();
  
  public void execute()
    throws BuildException
  {
    checkAttributes();
    for (Resource r : sources)
    {
      URLProvider up = (URLProvider)r.as(URLProvider.class);
      URL source = up.getURL();
      
      File dest = destination;
      if (destination.isDirectory()) {
        if (mapperElement == null)
        {
          String path = source.getPath();
          if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
          }
          int slash = path.lastIndexOf('/');
          if (slash > -1) {
            path = path.substring(slash + 1);
          }
          dest = new File(destination, path);
        }
        else
        {
          FileNameMapper mapper = mapperElement.getImplementation();
          String[] d = mapper.mapFileName(source.toString());
          if (d == null)
          {
            log("skipping " + r + " - mapper can't handle it", 1);
            
            continue;
          }
          if (d.length == 0)
          {
            log("skipping " + r + " - mapper returns no file name", 1);
            
            continue;
          }
          if (d.length > 1)
          {
            log("skipping " + r + " - mapper returns multiple file names", 1);
            
            continue;
          }
          dest = new File(destination, d[0]);
        }
      }
      int logLevel = 2;
      DownloadProgress progress = null;
      if (verbose) {
        progress = new VerboseProgress(System.out);
      }
      try
      {
        doGet(source, dest, 2, progress);
      }
      catch (IOException ioe)
      {
        log("Error getting " + source + " to " + dest);
        if (!ignoreErrors) {
          throw new BuildException(ioe, getLocation());
        }
      }
    }
  }
  
  @Deprecated
  public boolean doGet(int logLevel, DownloadProgress progress)
    throws IOException
  {
    checkAttributes();
    return doGet(((URLProvider)((Resource)sources.iterator().next()).as(URLProvider.class)).getURL(), destination, logLevel, progress);
  }
  
  public boolean doGet(URL source, File dest, int logLevel, DownloadProgress progress)
    throws IOException
  {
    if ((dest.exists()) && (skipExisting))
    {
      log("Destination already exists (skipping): " + dest
        .getAbsolutePath(), logLevel);
      return true;
    }
    if (progress == null) {
      progress = new NullProgress();
    }
    log("Getting: " + source, logLevel);
    log("To: " + dest.getAbsolutePath(), logLevel);
    
    long timestamp = 0L;
    
    boolean hasTimestamp = false;
    if ((useTimestamp) && (dest.exists()))
    {
      timestamp = dest.lastModified();
      if (verbose)
      {
        Date t = new Date(timestamp);
        log("local file date : " + t.toString(), logLevel);
      }
      hasTimestamp = true;
    }
    GetThread getThread = new GetThread(source, dest, hasTimestamp, timestamp, progress, logLevel, userAgent);
    
    getThread.setDaemon(true);
    getProject().registerThreadTask(getThread, this);
    getThread.start();
    try
    {
      getThread.join(maxTime * 1000L);
    }
    catch (InterruptedException ie)
    {
      log("interrupted waiting for GET to finish", 3);
    }
    if (getThread.isAlive())
    {
      String msg = "The GET operation took longer than " + maxTime + " seconds, stopping it.";
      if (ignoreErrors) {
        log(msg);
      }
      getThread.closeStreams();
      if (!ignoreErrors) {
        throw new BuildException(msg);
      }
      return false;
    }
    return getThread.wasSuccessful();
  }
  
  public void log(String msg, int msgLevel)
  {
    if ((!quiet) || (msgLevel <= 0)) {
      super.log(msg, msgLevel);
    }
  }
  
  private void checkAttributes()
  {
    if ((userAgent == null) || (userAgent.trim().isEmpty())) {
      throw new BuildException("userAgent may not be null or empty");
    }
    if (sources.size() == 0) {
      throw new BuildException("at least one source is required", getLocation());
    }
    for (Resource r : sources)
    {
      URLProvider up = (URLProvider)r.as(URLProvider.class);
      if (up == null) {
        throw new BuildException("Only URLProvider resources are supported", getLocation());
      }
    }
    if (destination == null) {
      throw new BuildException("dest attribute is required", getLocation());
    }
    if ((destination.exists()) && (sources.size() > 1) && 
      (!destination.isDirectory())) {
      throw new BuildException("The specified destination is not a directory", getLocation());
    }
    if ((destination.exists()) && (!destination.canWrite())) {
      throw new BuildException("Can't write to " + destination.getAbsolutePath(), getLocation());
    }
    if ((sources.size() > 1) && (!destination.exists())) {
      destination.mkdirs();
    }
  }
  
  public void setSrc(URL u)
  {
    add(new URLResource(u));
  }
  
  public void add(ResourceCollection rc)
  {
    sources.add(rc);
  }
  
  public void setDest(File dest)
  {
    destination = dest;
  }
  
  public void setVerbose(boolean v)
  {
    verbose = v;
  }
  
  public void setQuiet(boolean v)
  {
    quiet = v;
  }
  
  public void setIgnoreErrors(boolean v)
  {
    ignoreErrors = v;
  }
  
  public void setUseTimestamp(boolean v)
  {
    useTimestamp = v;
  }
  
  public void setUsername(String u)
  {
    uname = u;
  }
  
  public void setPassword(String p)
  {
    pword = p;
  }
  
  public void setMaxTime(long maxTime)
  {
    this.maxTime = maxTime;
  }
  
  public void setRetries(int r)
  {
    if (r <= 0) {
      log("Setting retries to " + r + " will make the task not even try to reach the URI at all", 1);
    }
    numberRetries = r;
  }
  
  public void setSkipExisting(boolean s)
  {
    skipExisting = s;
  }
  
  public void setUserAgent(String userAgent)
  {
    this.userAgent = userAgent;
  }
  
  public void setHttpUseCaches(boolean httpUseCache)
  {
    httpUseCaches = httpUseCache;
  }
  
  public void setTryGzipEncoding(boolean b)
  {
    tryGzipEncoding = b;
  }
  
  public void addConfiguredHeader(Header header)
  {
    if (header != null)
    {
      String key = StringUtils.trimToNull(header.getName());
      String value = StringUtils.trimToNull(header.getValue());
      if ((key != null) && (value != null)) {
        headers.put(key, value);
      }
    }
  }
  
  public Mapper createMapper()
    throws BuildException
  {
    if (mapperElement != null) {
      throw new BuildException("Cannot define more than one mapper", getLocation());
    }
    mapperElement = new Mapper(getProject());
    return mapperElement;
  }
  
  public void add(FileNameMapper fileNameMapper)
  {
    createMapper().add(fileNameMapper);
  }
  
  public static class VerboseProgress
    implements Get.DownloadProgress
  {
    private int dots = 0;
    PrintStream out;
    
    public VerboseProgress(PrintStream out)
    {
      this.out = out;
    }
    
    public void beginDownload()
    {
      dots = 0;
    }
    
    public void onTick()
    {
      out.print(".");
      if (dots++ > 50)
      {
        out.flush();
        dots = 0;
      }
    }
    
    public void endDownload()
    {
      out.println();
      out.flush();
    }
  }
  
  public static abstract interface DownloadProgress
  {
    public abstract void beginDownload();
    
    public abstract void onTick();
    
    public abstract void endDownload();
  }
  
  public static class NullProgress
    implements Get.DownloadProgress
  {
    public void beginDownload() {}
    
    public void onTick() {}
    
    public void endDownload() {}
  }
  
  private class GetThread
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
    
    GetThread(URL source, File dest, boolean h, long t, Get.DownloadProgress p, int l, String userAgent)
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
      if ((downloadSucceeded) && (useTimestamp)) {
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
        if (ignoreErrors)
        {
          log(message, logLevel);
          return false;
        }
        throw new BuildException(message);
      }
      redirections += 1;
      if (redirections > 25)
      {
        String message = "More than 25 times redirected, giving up";
        if (ignoreErrors)
        {
          log("More than 25 times redirected, giving up", logLevel);
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
      if ((uname != null) || (pword != null))
      {
        up = uname + ":" + pword;
        
        Get.Base64Converter encoder = new Get.Base64Converter();
        String encoding = encoder.encode(up.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encoding);
      }
      if (tryGzipEncoding) {
        connection.setRequestProperty("Accept-Encoding", "gzip");
      }
      for (Map.Entry<String, String> header : headers.entrySet())
      {
        log(String.format("Adding header '%s' ", new Object[] { header.getKey() }));
        connection.setRequestProperty((String)header.getKey(), (String)header.getValue());
      }
      if ((connection instanceof HttpURLConnection))
      {
        ((HttpURLConnection)connection).setInstanceFollowRedirects(false);
        connection.setUseCaches(httpUseCaches);
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
          log(message, logLevel);
          URL newURL = new URL(aSource, newLocation);
          if (!redirectionAllowed(aSource, newURL)) {
            return null;
          }
          return openConnection(newURL);
        }
        long lastModified = httpConnection.getLastModified();
        if ((responseCode == 304) || ((lastModified != 0L) && (hasTimestamp) && (timestamp >= lastModified)))
        {
          log("Not modified - so not downloaded", logLevel);
          return null;
        }
        if (responseCode == 401)
        {
          String message = "HTTP Authorization failure";
          if (ignoreErrors)
          {
            log("HTTP Authorization failure", logLevel);
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
      for (int i = 0; i < numberRetries; i++) {
        try
        {
          is = connection.getInputStream();
        }
        catch (IOException ex)
        {
          log("Error opening connection " + ex, logLevel);
        }
      }
      if (is == null)
      {
        log("Can't get " + source + " to " + dest, logLevel);
        if (ignoreErrors) {
          return false;
        }
        throw new BuildException("Can't get " + source + " to " + dest, getLocation());
      }
      if ((tryGzipEncoding) && 
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
      if (verbose)
      {
        Date t = new Date(remoteTimestamp);
        log("last modified = " + t.toString() + (
          remoteTimestamp == 0L ? " - using current time instead" : ""), logLevel);
      }
      if (remoteTimestamp != 0L) {
        Get.FILE_UTILS.setFileLastModified(dest, remoteTimestamp);
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
  
  protected static class Base64Converter
    extends Base64Converter
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Get
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */