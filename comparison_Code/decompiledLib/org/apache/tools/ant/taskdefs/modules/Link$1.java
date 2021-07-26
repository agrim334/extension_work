package org.apache.tools.ant.taskdefs.modules;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class Link$1
  extends SimpleFileVisitor<Path>
{
  Link$1(Link this$0) {}
  
  public FileVisitResult visitFile(Path file, BasicFileAttributes attr)
    throws IOException
  {
    Files.delete(file);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult postVisitDirectory(Path dir, IOException e)
    throws IOException
  {
    if (e == null) {
      Files.delete(dir);
    }
    return super.postVisitDirectory(dir, e);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */