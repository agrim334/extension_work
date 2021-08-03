package org.apache.maven.it.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.shared.utils.io.IOUtil;

public class ResourceExtractor
{
  public static File simpleExtractResources(Class<?> cl, String resourcePath)
    throws IOException
  {
    String tempDirPath = System.getProperty("maven.test.tmpdir", System.getProperty("java.io.tmpdir"));
    File tempDir = new File(tempDirPath);
    
    File testDir = new File(tempDir, resourcePath);
    
    FileUtils.deleteDirectory(testDir);
    
    testDir = extractResourcePath(cl, resourcePath, tempDir, false);
    return testDir;
  }
  
  public static File extractResourcePath(String resourcePath, File dest)
    throws IOException
  {
    return extractResourcePath(ResourceExtractor.class, resourcePath, dest);
  }
  
  public static File extractResourcePath(Class<?> cl, String resourcePath, File dest)
    throws IOException
  {
    return extractResourcePath(cl, resourcePath, dest, false);
  }
  
  public static File extractResourcePath(Class<?> cl, String resourcePath, File tempDir, boolean alwaysExtract)
    throws IOException
  {
    File dest = new File(tempDir, resourcePath);
    return extractResourceToDestination(cl, resourcePath, dest, alwaysExtract);
  }
  
  public static File extractResourceToDestination(Class<?> cl, String resourcePath, File destination, boolean alwaysExtract)
    throws IOException
  {
    URL url = cl.getResource(resourcePath);
    if (url == null) {
      throw new IllegalArgumentException("Resource not found: " + resourcePath);
    }
    if ("jar".equalsIgnoreCase(url.getProtocol()))
    {
      File jarFile = getJarFileFromUrl(url);
      extractResourcePathFromJar(cl, jarFile, resourcePath, destination);
    }
    else
    {
      try
      {
        File resourceFile = new File(new URI(url.toExternalForm()));
        if (!alwaysExtract) {
          return resourceFile;
        }
        if (resourceFile.isDirectory()) {
          FileUtils.copyDirectoryStructure(resourceFile, destination);
        } else {
          FileUtils.copyFile(resourceFile, destination);
        }
      }
      catch (URISyntaxException e)
      {
        throw new RuntimeException("Couldn't convert URL to File:" + url, e);
      }
    }
    return destination;
  }
  
  private static void extractResourcePathFromJar(Class<?> cl, File jarFile, String resourcePath, File dest)
    throws IOException
  {
    ZipFile z = new ZipFile(jarFile, 1);
    String zipStyleResourcePath = resourcePath.substring(1) + "/";
    ZipEntry ze = z.getEntry(zipStyleResourcePath);
    Enumeration<? extends ZipEntry> entries;
    if (ze != null)
    {
      for (entries = z.entries(); entries.hasMoreElements();)
      {
        ze = (ZipEntry)entries.nextElement();
        if (ze.getName().startsWith(zipStyleResourcePath))
        {
          String relativePath = ze.getName().substring(zipStyleResourcePath.length());
          File destFile = new File(dest, relativePath);
          if (ze.isDirectory())
          {
            destFile.mkdirs();
          }
          else
          {
            FileOutputStream fos = new FileOutputStream(destFile);
            try
            {
              IOUtil.copy(z.getInputStream(ze), fos);
            }
            finally
            {
              IOUtil.close(fos);
              z.close();
            }
          }
        }
      }
    }
    else
    {
      FileOutputStream fos = new FileOutputStream(dest);
      try
      {
        IOUtil.copy(cl.getResourceAsStream(resourcePath), fos);
      }
      finally
      {
        IOUtil.close(fos);
        z.close();
      }
    }
  }
  
  private static File getJarFileFromUrl(URL url)
  {
    if (!"jar".equalsIgnoreCase(url.getProtocol())) {
      throw new IllegalArgumentException("This is not a Jar URL:" + url.toString());
    }
    String resourceFilePath = url.getFile();
    int index = resourceFilePath.indexOf("!");
    if (index == -1) {
      throw new RuntimeException("Bug! " + url.toExternalForm() + " does not have a '!'");
    }
    String jarFileURI = resourceFilePath.substring(0, index);
    try
    {
      return new File(new URI(jarFileURI));
    }
    catch (URISyntaxException e)
    {
      throw new RuntimeException("Bug! URI failed to parse: " + jarFileURI, e);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.util.ResourceExtractor
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */