package org.apache.tools.ant;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.condition.Os;

public class PathTokenizer
{
  private StringTokenizer tokenizer;
  private String lookahead = null;
  private boolean onNetWare = Os.isFamily("netware");
  private boolean dosStyleFilesystem;
  
  public PathTokenizer(String path)
  {
    if (onNetWare) {
      tokenizer = new StringTokenizer(path, ":;", true);
    } else {
      tokenizer = new StringTokenizer(path, ":;", false);
    }
    dosStyleFilesystem = (File.pathSeparatorChar == ';');
  }
  
  public boolean hasMoreTokens()
  {
    return (lookahead != null) || (tokenizer.hasMoreTokens());
  }
  
  public String nextToken()
    throws NoSuchElementException
  {
    String token = null;
    if (lookahead != null)
    {
      token = lookahead;
      lookahead = null;
    }
    else
    {
      token = tokenizer.nextToken().trim();
    }
    if (!onNetWare)
    {
      if ((token.length() == 1) && (Character.isLetter(token.charAt(0))) && (dosStyleFilesystem)) {
        if (tokenizer.hasMoreTokens())
        {
          String nextToken = tokenizer.nextToken().trim();
          if ((nextToken.startsWith("\\")) || (nextToken.startsWith("/"))) {
            token = token + ":" + nextToken;
          } else {
            lookahead = nextToken;
          }
        }
      }
    }
    else
    {
      if ((token.equals(File.pathSeparator)) || (":".equals(token))) {
        token = tokenizer.nextToken().trim();
      }
      if (tokenizer.hasMoreTokens())
      {
        String nextToken = tokenizer.nextToken().trim();
        if (!nextToken.equals(File.pathSeparator)) {
          if (":".equals(nextToken))
          {
            if ((!token.startsWith("/")) && (!token.startsWith("\\")) && 
              (!token.startsWith(".")) && 
              (!token.startsWith("..")))
            {
              String oneMore = tokenizer.nextToken().trim();
              if (!oneMore.equals(File.pathSeparator))
              {
                token = token + ":" + oneMore;
              }
              else
              {
                token = token + ":";
                lookahead = oneMore;
              }
            }
          }
          else {
            lookahead = nextToken;
          }
        }
      }
    }
    return token;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.PathTokenizer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */