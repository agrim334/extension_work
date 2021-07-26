package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import org.apache.tools.ant.BuildException;

public class PosixGroupSelector
  implements FileSelector
{
  private String group;
  private boolean followSymlinks = true;
  
  public void setGroup(String group)
  {
    this.group = group;
  }
  
  public void setFollowSymlinks(boolean followSymlinks)
  {
    this.followSymlinks = followSymlinks;
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    if (group == null) {
      throw new BuildException("the group attribute is required");
    }
    try
    {
      GroupPrincipal actualGroup = followSymlinks ? ((PosixFileAttributes)Files.readAttributes(file.toPath(), PosixFileAttributes.class, new LinkOption[0])).group() : ((PosixFileAttributes)Files.readAttributes(file.toPath(), PosixFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })).group();
      return (actualGroup != null) && (actualGroup.getName().equals(group));
    }
    catch (IOException localIOException) {}
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.PosixGroupSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */