package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class PropertiesfileCache
  implements Cache
{
  private File cachefile = null;
  private Properties cache = new Properties();
  private boolean cacheLoaded = false;
  private boolean cacheDirty = true;
  
  public PropertiesfileCache() {}
  
  public PropertiesfileCache(File cachefile)
  {
    this.cachefile = cachefile;
  }
  
  public void setCachefile(File file)
  {
    cachefile = file;
  }
  
  public File getCachefile()
  {
    return cachefile;
  }
  
  public boolean isValid()
  {
    return cachefile != null;
  }
  
  public void load()
  {
    if ((cachefile != null) && (cachefile.isFile()) && (cachefile.canRead())) {
      try
      {
        BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(cachefile.toPath(), new OpenOption[0]));
        try
        {
          cache.load(bis);
          bis.close();
        }
        catch (Throwable localThrowable) {}
        try
        {
          bis.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        throw localThrowable;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    cacheLoaded = true;
    cacheDirty = false;
  }
  
  public void save()
  {
    if (!cacheDirty) {
      return;
    }
    if ((cachefile != null) && (cache.propertyNames().hasMoreElements())) {
      try
      {
        BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(cachefile.toPath(), new OpenOption[0]));
        try
        {
          cache.store(bos, null);
          bos.flush();
          bos.close();
        }
        catch (Throwable localThrowable) {}
        try
        {
          bos.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        throw localThrowable;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    cacheDirty = false;
  }
  
  public void delete()
  {
    cache = new Properties();
    cachefile.delete();
    cacheLoaded = true;
    cacheDirty = false;
  }
  
  public Object get(Object key)
  {
    if (!cacheLoaded) {
      load();
    }
    try
    {
      return cache.getProperty(String.valueOf(key));
    }
    catch (ClassCastException e) {}
    return null;
  }
  
  public void put(Object key, Object value)
  {
    cache.put(String.valueOf(key), String.valueOf(value));
    cacheDirty = true;
  }
  
  public Iterator<String> iterator()
  {
    return cache.stringPropertyNames().iterator();
  }
  
  public String toString()
  {
    return String.format("<PropertiesfileCache:cachefile=%s;noOfEntries=%d>", new Object[] { cachefile, 
      Integer.valueOf(cache.size()) });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.PropertiesfileCache
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */