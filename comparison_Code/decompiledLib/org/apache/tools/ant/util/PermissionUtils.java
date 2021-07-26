package org.apache.tools.ant.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.FileProvider;

public class PermissionUtils
{
  public static int modeFromPermissions(Set<PosixFilePermission> permissions, FileType type)
  {
    int mode;
    int mode;
    int mode;
    switch (type)
    {
    case SYMLINK: 
      mode = 10;
      break;
    case REGULAR_FILE: 
      mode = 8;
      break;
    case DIR: 
      mode = 4;
      break;
    default: 
      mode = 0;
    }
    mode <<= 3;
    mode <<= 3;
    int mode = (int)(mode | modeFromPermissions(permissions, "OWNER"));
    mode <<= 3;
    mode = (int)(mode | modeFromPermissions(permissions, "GROUP"));
    mode <<= 3;
    mode = (int)(mode | modeFromPermissions(permissions, "OTHERS"));
    return mode;
  }
  
  public static Set<PosixFilePermission> permissionsFromMode(int mode)
  {
    Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
    addPermissions(permissions, "OTHERS", mode);
    addPermissions(permissions, "GROUP", mode >> 3);
    addPermissions(permissions, "OWNER", mode >> 6);
    return permissions;
  }
  
  public static void setPermissions(Resource r, Set<PosixFilePermission> permissions, Consumer<Path> posixNotSupportedCallback)
    throws IOException
  {
    FileProvider f = (FileProvider)r.as(FileProvider.class);
    if (f != null)
    {
      Path p = f.getFile().toPath();
      
      PosixFileAttributeView view = (PosixFileAttributeView)Files.getFileAttributeView(p, PosixFileAttributeView.class, new LinkOption[0]);
      if (view != null) {
        view.setPermissions(permissions);
      } else if (posixNotSupportedCallback != null) {
        posixNotSupportedCallback.accept(p);
      }
    }
    else if ((r instanceof ArchiveResource))
    {
      ((ArchiveResource)r).setMode(modeFromPermissions(permissions, 
        FileType.of(r)));
    }
  }
  
  public static Set<PosixFilePermission> getPermissions(Resource r, Function<Path, Set<PosixFilePermission>> posixNotSupportedFallback)
    throws IOException
  {
    FileProvider f = (FileProvider)r.as(FileProvider.class);
    if (f != null)
    {
      Path p = f.getFile().toPath();
      
      PosixFileAttributeView view = (PosixFileAttributeView)Files.getFileAttributeView(p, PosixFileAttributeView.class, new LinkOption[0]);
      if (view != null) {
        return view.readAttributes().permissions();
      }
      if (posixNotSupportedFallback != null) {
        return (Set)posixNotSupportedFallback.apply(p);
      }
    }
    else if ((r instanceof ArchiveResource))
    {
      return permissionsFromMode(((ArchiveResource)r).getMode());
    }
    return EnumSet.noneOf(PosixFilePermission.class);
  }
  
  private static long modeFromPermissions(Set<PosixFilePermission> permissions, String prefix)
  {
    long mode = 0L;
    if (permissions.contains(PosixFilePermission.valueOf(prefix + "_READ"))) {
      mode |= 0x4;
    }
    if (permissions.contains(PosixFilePermission.valueOf(prefix + "_WRITE"))) {
      mode |= 0x2;
    }
    if (permissions.contains(PosixFilePermission.valueOf(prefix + "_EXECUTE"))) {
      mode |= 1L;
    }
    return mode;
  }
  
  private static void addPermissions(Set<PosixFilePermission> permissions, String prefix, long mode)
  {
    if ((mode & 1L) == 1L) {
      permissions.add(PosixFilePermission.valueOf(prefix + "_EXECUTE"));
    }
    if ((mode & 0x2) == 2L) {
      permissions.add(PosixFilePermission.valueOf(prefix + "_WRITE"));
    }
    if ((mode & 0x4) == 4L) {
      permissions.add(PosixFilePermission.valueOf(prefix + "_READ"));
    }
  }
  
  public static enum FileType
  {
    REGULAR_FILE,  DIR,  SYMLINK,  OTHER;
    
    private FileType() {}
    
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
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.PermissionUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */