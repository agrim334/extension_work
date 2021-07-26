package org.apache.tools.ant.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.tools.ant.types.Resource;

public enum PermissionUtils$FileType
{
  REGULAR_FILE,  DIR,  SYMLINK,  OTHER;
  
  private PermissionUtils$FileType() {}
  
  public static FileType of(Path p)
    throws IOException
  {
    BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class, new LinkOption[0]);
    if (attrs.isRegularFile()) {
      return REGULAR_FILE;
    }
    if (attrs.isDirectory()) {
      return DIR;
    }
    if (attrs.isSymbolicLink()) {
      return SYMLINK;
    }
    return OTHER;
  }
  
  public static FileType of(Resource r)
  {
    if (r.isDirectory()) {
      return DIR;
    }
    return REGULAR_FILE;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.PermissionUtils.FileType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */