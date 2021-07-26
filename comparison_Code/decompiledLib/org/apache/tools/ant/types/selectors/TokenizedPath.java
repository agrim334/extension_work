package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

public class TokenizedPath
{
  public static final TokenizedPath EMPTY_PATH = new TokenizedPath("", new String[0]);
  private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
  private static final boolean[] CS_SCAN_ONLY = { true };
  private static final boolean[] CS_THEN_NON_CS = { true, false };
  private final String path;
  private final String[] tokenizedPath;
  
  public TokenizedPath(String path)
  {
    this(path, SelectorUtils.tokenizePathAsArray(path));
  }
  
  public TokenizedPath(TokenizedPath parent, String child)
  {
    if ((!path.isEmpty()) && 
      (path.charAt(path.length() - 1) != File.separatorChar)) {
      path = (path + File.separatorChar + child);
    } else {
      path += child;
    }
    tokenizedPath = new String[tokenizedPath.length + 1];
    System.arraycopy(tokenizedPath, 0, tokenizedPath, 0, tokenizedPath.length);
    
    tokenizedPath[tokenizedPath.length] = child;
  }
  
  TokenizedPath(String path, String[] tokens)
  {
    this.path = path;
    tokenizedPath = tokens;
  }
  
  public String toString()
  {
    return path;
  }
  
  public int depth()
  {
    return tokenizedPath.length;
  }
  
  String[] getTokens()
  {
    return tokenizedPath;
  }
  
  public File findFile(File base, boolean cs)
  {
    String[] tokens = tokenizedPath;
    if (FileUtils.isAbsolutePath(path)) {
      if (base == null)
      {
        String[] s = FILE_UTILS.dissect(path);
        base = new File(s[0]);
        tokens = SelectorUtils.tokenizePathAsArray(s[1]);
      }
      else
      {
        File f = FILE_UTILS.normalize(path);
        String s = FILE_UTILS.removeLeadingPath(base, f);
        if (s.equals(f.getAbsolutePath())) {
          return null;
        }
        tokens = SelectorUtils.tokenizePathAsArray(s);
      }
    }
    return findFile(base, tokens, cs);
  }
  
  public boolean isSymlink(File base)
  {
    for (String token : tokenizedPath)
    {
      Path pathToTraverse;
      Path pathToTraverse;
      if (base == null) {
        pathToTraverse = Paths.get(token, new String[0]);
      } else {
        pathToTraverse = Paths.get(base.toPath().toString(), new String[] { token });
      }
      if (Files.isSymbolicLink(pathToTraverse)) {
        return true;
      }
      base = new File(base, token);
    }
    return false;
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof TokenizedPath)) && 
      (path.equals(path));
  }
  
  public int hashCode()
  {
    return path.hashCode();
  }
  
  private static File findFile(File base, String[] pathElements, boolean cs)
  {
    for (String pathElement : pathElements)
    {
      if (!base.isDirectory()) {
        return null;
      }
      String[] files = base.list();
      if (files == null) {
        throw new BuildException("IO error scanning directory %s", new Object[] {base.getAbsolutePath() });
      }
      boolean found = false;
      boolean[] matchCase = cs ? CS_SCAN_ONLY : CS_THEN_NON_CS;
      for (int i = 0; (!found) && (i < matchCase.length); i++) {
        for (int j = 0; (!found) && (j < files.length); j++) {
          if (matchCase[i] != 0 ? files[j]
            .equals(pathElement) : files[j]
            .equalsIgnoreCase(pathElement))
          {
            base = new File(base, files[j]);
            found = true;
          }
        }
      }
      if (!found) {
        return null;
      }
    }
    return (pathElements.length == 0) && (!base.isDirectory()) ? null : base;
  }
  
  public TokenizedPattern toPattern()
  {
    return new TokenizedPattern(path, tokenizedPath);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.TokenizedPath
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */