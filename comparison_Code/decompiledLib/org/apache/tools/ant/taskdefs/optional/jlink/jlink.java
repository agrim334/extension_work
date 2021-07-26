package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class jlink
{
  private static final int BUFFER_SIZE = 8192;
  private static final int VECTOR_INIT_SIZE = 10;
  private String outfile = null;
  private List<String> mergefiles = new Vector(10);
  private List<String> addfiles = new Vector(10);
  private boolean compression = false;
  byte[] buffer = new byte[' '];
  
  public void setOutfile(String outfile)
  {
    if (outfile == null) {
      return;
    }
    this.outfile = outfile;
  }
  
  public void addMergeFile(String fileToMerge)
  {
    if (fileToMerge == null) {
      return;
    }
    mergefiles.add(fileToMerge);
  }
  
  public void addAddFile(String fileToAdd)
  {
    if (fileToAdd == null) {
      return;
    }
    addfiles.add(fileToAdd);
  }
  
  public void addMergeFiles(String... filesToMerge)
  {
    if (filesToMerge == null) {
      return;
    }
    for (String element : filesToMerge) {
      addMergeFile(element);
    }
  }
  
  public void addAddFiles(String... filesToAdd)
  {
    if (filesToAdd == null) {
      return;
    }
    for (String element : filesToAdd) {
      addAddFile(element);
    }
  }
  
  public void setCompression(boolean compress)
  {
    compression = compress;
  }
  
  public void link()
    throws Exception
  {
    ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(Paths.get(outfile, new String[0]), new OpenOption[0]));
    try
    {
      if (compression)
      {
        output.setMethod(8);
        output.setLevel(-1);
      }
      else
      {
        output.setMethod(0);
      }
      for (String path : mergefiles)
      {
        File f = new File(path);
        if ((f.getName().endsWith(".jar")) || 
          (f.getName().endsWith(".zip"))) {
          mergeZipJarContents(output, f);
        } else {
          addAddFile(path);
        }
      }
      for (String name : addfiles)
      {
        File f = new File(name);
        if (f.isDirectory()) {
          addDirContents(output, f, f.getName() + '/', compression);
        } else {
          addFile(output, f, "", compression);
        }
      }
      output.close();
    }
    catch (Throwable localThrowable2) {}
    try
    {
      output.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2.addSuppressed(localThrowable1);
    }
    throw localThrowable2;
  }
  
  public static void main(String[] args)
  {
    if (args.length < 2)
    {
      System.out.println("usage: jlink output input1 ... inputN");
      System.exit(1);
    }
    jlink linker = new jlink();
    
    linker.setOutfile(args[0]);
    for (int i = 1; i < args.length; i++) {
      linker.addMergeFile(args[i]);
    }
    try
    {
      linker.link();
    }
    catch (Exception ex)
    {
      System.err.print(ex.getMessage());
    }
  }
  
  private void mergeZipJarContents(ZipOutputStream output, File f)
    throws IOException
  {
    if (!f.exists()) {
      return;
    }
    ZipFile zipf = new ZipFile(f);
    try
    {
      Enumeration<? extends ZipEntry> entries = zipf.entries();
      label192:
      label195:
      while (entries.hasMoreElements())
      {
        ZipEntry inputEntry = (ZipEntry)entries.nextElement();
        
        String inputEntryName = inputEntry.getName();
        int index = inputEntryName.indexOf("META-INF");
        if (index < 0)
        {
          try
          {
            output.putNextEntry(processEntry(zipf, inputEntry));
          }
          catch (ZipException ex) {}
          if (!ex.getMessage().contains("duplicate"))
          {
            throw ex;
            
            InputStream in = zipf.getInputStream(inputEntry);
            try
            {
              int len = buffer.length;
              int count = -1;
              while ((count = in.read(buffer, 0, len)) > 0) {
                output.write(buffer, 0, count);
              }
              output.closeEntry();
              if (in == null) {
                break label195;
              }
              in.close();
            }
            catch (Throwable localThrowable)
            {
              if (in == null) {
                break label192;
              }
            }
            try
            {
              in.close();
            }
            catch (Throwable localThrowable1)
            {
              localThrowable.addSuppressed(localThrowable1);
            }
            throw localThrowable;
          }
        }
      }
      zipf.close();
    }
    catch (Throwable localThrowable2) {}
    try
    {
      zipf.close();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2.addSuppressed(localThrowable3);
    }
    throw localThrowable2;
  }
  
  private void addDirContents(ZipOutputStream output, File dir, String prefix, boolean compress)
    throws IOException
  {
    for (String name : dir.list())
    {
      File file = new File(dir, name);
      if (file.isDirectory()) {
        addDirContents(output, file, prefix + name + '/', compress);
      } else {
        addFile(output, file, prefix, compress);
      }
    }
  }
  
  private String getEntryName(File file, String prefix)
  {
    String name = file.getName();
    if (!name.endsWith(".class")) {
      label118:
      label121:
      try
      {
        InputStream input = Files.newInputStream(file.toPath(), new OpenOption[0]);
        try
        {
          String className = ClassNameReader.getClassName(input);
          if (className != null)
          {
            String str1 = className.replace('.', '/') + ".class";
            if (input != null) {
              input.close();
            }
            return str1;
          }
          if (input == null) {
            break label121;
          }
          input.close();
        }
        catch (Throwable localThrowable)
        {
          if (input == null) {
            break label118;
          }
        }
        try
        {
          input.close();
        }
        catch (Throwable localThrowable2)
        {
          localThrowable.addSuppressed(localThrowable2);
        }
        throw localThrowable;
      }
      catch (IOException localIOException) {}
    }
    System.out.printf("From %1$s and prefix %2$s, creating entry %2$s%3$s%n", new Object[] {file
    
      .getPath(), prefix, name });
    return prefix + name;
  }
  
  private void addFile(ZipOutputStream output, File file, String prefix, boolean compress)
    throws IOException
  {
    if (!file.exists()) {
      return;
    }
    ZipEntry entry = new ZipEntry(getEntryName(file, prefix));
    
    entry.setTime(file.lastModified());
    entry.setSize(file.length());
    if (!compress) {
      entry.setCrc(calcChecksum(file));
    }
    addToOutputStream(output, Files.newInputStream(file.toPath(), new OpenOption[0]), entry);
  }
  
  private void addToOutputStream(ZipOutputStream output, InputStream input, ZipEntry ze)
    throws IOException
  {
    try
    {
      output.putNextEntry(ze);
    }
    catch (ZipException zipEx)
    {
      input.close(); return;
    }
    int numBytes;
    while ((numBytes = input.read(buffer)) > 0) {
      output.write(buffer, 0, numBytes);
    }
    output.closeEntry();
    input.close();
  }
  
  private ZipEntry processEntry(ZipFile zip, ZipEntry inputEntry)
  {
    String name = inputEntry.getName();
    if ((!inputEntry.isDirectory()) && (!name.endsWith(".class"))) {
      label109:
      label112:
      try
      {
        InputStream input = zip.getInputStream(zip.getEntry(name));
        try
        {
          String className = ClassNameReader.getClassName(input);
          if (className != null) {
            name = className.replace('.', '/') + ".class";
          }
          if (input == null) {
            break label112;
          }
          input.close();
        }
        catch (Throwable localThrowable)
        {
          if (input == null) {
            break label109;
          }
        }
        try
        {
          input.close();
        }
        catch (Throwable localThrowable1)
        {
          localThrowable.addSuppressed(localThrowable1);
        }
        throw localThrowable;
      }
      catch (IOException localIOException) {}
    }
    ZipEntry outputEntry = new ZipEntry(name);
    
    outputEntry.setTime(inputEntry.getTime());
    outputEntry.setExtra(inputEntry.getExtra());
    outputEntry.setComment(inputEntry.getComment());
    outputEntry.setTime(inputEntry.getTime());
    if (compression)
    {
      outputEntry.setMethod(8);
    }
    else
    {
      outputEntry.setMethod(0);
      outputEntry.setCrc(inputEntry.getCrc());
      outputEntry.setSize(inputEntry.getSize());
    }
    return outputEntry;
  }
  
  private long calcChecksum(File f)
    throws IOException
  {
    return calcChecksum(new BufferedInputStream(
      Files.newInputStream(f.toPath(), new OpenOption[0])));
  }
  
  private long calcChecksum(InputStream in)
    throws IOException
  {
    CRC32 crc = new CRC32();
    int len = buffer.length;
    int count;
    while ((count = in.read(buffer, 0, len)) > 0) {
      crc.update(buffer, 0, count);
    }
    in.close();
    return crc.getValue();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jlink.jlink
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */