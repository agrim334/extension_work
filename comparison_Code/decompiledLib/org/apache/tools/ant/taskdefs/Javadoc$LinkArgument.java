package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.net.URL;

public class Javadoc$LinkArgument
{
  private String href;
  private boolean offline = false;
  private File packagelistLoc;
  private URL packagelistURL;
  private boolean resolveLink = false;
  
  public Javadoc$LinkArgument(Javadoc this$0) {}
  
  public void setHref(String hr)
  {
    href = hr;
  }
  
  public String getHref()
  {
    return href;
  }
  
  public void setPackagelistLoc(File src)
  {
    packagelistLoc = src;
  }
  
  public File getPackagelistLoc()
  {
    return packagelistLoc;
  }
  
  public void setPackagelistURL(URL src)
  {
    packagelistURL = src;
  }
  
  public URL getPackagelistURL()
  {
    return packagelistURL;
  }
  
  public void setOffline(boolean offline)
  {
    this.offline = offline;
  }
  
  public boolean isLinkOffline()
  {
    return offline;
  }
  
  public void setResolveLink(boolean resolve)
  {
    resolveLink = resolve;
  }
  
  public boolean shouldResolveLink()
  {
    return resolveLink;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.LinkArgument
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */