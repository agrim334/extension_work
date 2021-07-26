package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.UserPrincipal;
import org.apache.tools.ant.BuildException;

public class OwnedBySelector
  implements FileSelector
{
  private String owner;
  private boolean followSymlinks = true;
  
  public void setOwner(String owner)
  {
    this.owner = owner;
  }
  
  public void setFollowSymlinks(boolean followSymlinks)
  {
    this.followSymlinks = followSymlinks;
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    if (owner == null) {
      throw new BuildException("the owner attribute is required");
    }
    if (file != null) {
      try
      {
        UserPrincipal user = followSymlinks ? Files.getOwner(file.toPath(), new LinkOption[0]) : Files.getOwner(file.toPath(), new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
        return (user != null) && (owner.equals(user.getName()));
      }
      catch (UnsupportedOperationException|IOException localUnsupportedOperationException) {}
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.OwnedBySelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */