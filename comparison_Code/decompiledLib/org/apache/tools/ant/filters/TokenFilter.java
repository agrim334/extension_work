package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.util.FileTokenizer;
import org.apache.tools.ant.util.LineTokenizer;
import org.apache.tools.ant.util.StringTokenizer;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.Tokenizer;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class TokenFilter
  extends BaseFilterReader
  implements ChainableReader
{
  private Vector<Filter> filters = new Vector();
  private Tokenizer tokenizer = null;
  private String delimOutput = null;
  private String line = null;
  private int linePos = 0;
  
  public TokenFilter() {}
  
  public TokenFilter(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    if (tokenizer == null) {
      tokenizer = new LineTokenizer();
    }
    while ((line == null) || (line.isEmpty()))
    {
      line = tokenizer.getToken(in);
      if (line == null) {
        return -1;
      }
      for (Filter filter : filters)
      {
        line = filter.filter(line);
        if (line == null) {
          break;
        }
      }
      linePos = 0;
      if ((line != null) && (!tokenizer.getPostToken().isEmpty())) {
        if (delimOutput != null) {
          line += delimOutput;
        } else {
          line += tokenizer.getPostToken();
        }
      }
    }
    int ch = line.charAt(linePos);
    linePos += 1;
    if (linePos == line.length()) {
      line = null;
    }
    return ch;
  }
  
  public final Reader chain(Reader reader)
  {
    TokenFilter newFilter = new TokenFilter(reader);
    filters = filters;
    tokenizer = tokenizer;
    delimOutput = delimOutput;
    newFilter.setProject(getProject());
    return newFilter;
  }
  
  public void setDelimOutput(String delimOutput)
  {
    this.delimOutput = resolveBackSlash(delimOutput);
  }
  
  public void addLineTokenizer(LineTokenizer tokenizer)
  {
    add(tokenizer);
  }
  
  public void addStringTokenizer(StringTokenizer tokenizer)
  {
    add(tokenizer);
  }
  
  public void addFileTokenizer(FileTokenizer tokenizer)
  {
    add(tokenizer);
  }
  
  public void add(Tokenizer tokenizer)
  {
    if (this.tokenizer != null) {
      throw new BuildException("Only one tokenizer allowed");
    }
    this.tokenizer = tokenizer;
  }
  
  public void addReplaceString(ReplaceString filter)
  {
    filters.addElement(filter);
  }
  
  public void addContainsString(ContainsString filter)
  {
    filters.addElement(filter);
  }
  
  public void addReplaceRegex(ReplaceRegex filter)
  {
    filters.addElement(filter);
  }
  
  public void addContainsRegex(ContainsRegex filter)
  {
    filters.addElement(filter);
  }
  
  public void addTrim(Trim filter)
  {
    filters.addElement(filter);
  }
  
  public void addIgnoreBlank(IgnoreBlank filter)
  {
    filters.addElement(filter);
  }
  
  public void addDeleteCharacters(DeleteCharacters filter)
  {
    filters.addElement(filter);
  }
  
  public void add(Filter filter)
  {
    filters.addElement(filter);
  }
  
  public static class FileTokenizer
    extends FileTokenizer
  {}
  
  public static class StringTokenizer
    extends StringTokenizer
  {}
  
  public static abstract class ChainableReaderFilter
    extends ProjectComponent
    implements ChainableReader, TokenFilter.Filter
  {
    private boolean byLine = true;
    
    public void setByLine(boolean byLine)
    {
      this.byLine = byLine;
    }
    
    public Reader chain(Reader reader)
    {
      TokenFilter tokenFilter = new TokenFilter(reader);
      if (!byLine) {
        tokenFilter.add(new TokenFilter.FileTokenizer());
      }
      tokenFilter.add(this);
      return tokenFilter;
    }
  }
  
  public static class ReplaceString
    extends TokenFilter.ChainableReaderFilter
  {
    private String from;
    private String to;
    
    public void setFrom(String from)
    {
      this.from = from;
    }
    
    public void setTo(String to)
    {
      this.to = to;
    }
    
    public String filter(String line)
    {
      if (from == null) {
        throw new BuildException("Missing from in stringreplace");
      }
      StringBuffer ret = new StringBuffer();
      int start = 0;
      int found = line.indexOf(from);
      while (found >= 0)
      {
        if (found > start) {
          ret.append(line, start, found);
        }
        if (to != null) {
          ret.append(to);
        }
        start = found + from.length();
        found = line.indexOf(from, start);
      }
      if (line.length() > start) {
        ret.append(line, start, line.length());
      }
      return ret.toString();
    }
  }
  
  public static class ContainsString
    extends ProjectComponent
    implements TokenFilter.Filter
  {
    private String contains;
    
    public void setContains(String contains)
    {
      this.contains = contains;
    }
    
    public String filter(String string)
    {
      if (contains == null) {
        throw new BuildException("Missing contains in containsstring");
      }
      if (string.contains(contains)) {
        return string;
      }
      return null;
    }
  }
  
  public static class ReplaceRegex
    extends TokenFilter.ChainableReaderFilter
  {
    private String from;
    private String to;
    private RegularExpression regularExpression;
    private Substitution substitution;
    private boolean initialized = false;
    private String flags = "";
    private int options;
    private Regexp regexp;
    
    public void setPattern(String from)
    {
      this.from = from;
    }
    
    public void setReplace(String to)
    {
      this.to = to;
    }
    
    public void setFlags(String flags)
    {
      this.flags = flags;
    }
    
    private void initialize()
    {
      if (initialized) {
        return;
      }
      options = TokenFilter.convertRegexOptions(flags);
      if (from == null) {
        throw new BuildException("Missing pattern in replaceregex");
      }
      regularExpression = new RegularExpression();
      regularExpression.setPattern(from);
      regexp = regularExpression.getRegexp(getProject());
      if (to == null) {
        to = "";
      }
      substitution = new Substitution();
      substitution.setExpression(to);
    }
    
    public String filter(String line)
    {
      initialize();
      if (!regexp.matches(line, options)) {
        return line;
      }
      return regexp.substitute(line, substitution
        .getExpression(getProject()), options);
    }
  }
  
  public static class ContainsRegex
    extends TokenFilter.ChainableReaderFilter
  {
    private String from;
    private String to;
    private RegularExpression regularExpression;
    private Substitution substitution;
    private boolean initialized = false;
    private String flags = "";
    private int options;
    private Regexp regexp;
    
    public void setPattern(String from)
    {
      this.from = from;
    }
    
    public void setReplace(String to)
    {
      this.to = to;
    }
    
    public void setFlags(String flags)
    {
      this.flags = flags;
    }
    
    private void initialize()
    {
      if (initialized) {
        return;
      }
      options = TokenFilter.convertRegexOptions(flags);
      if (from == null) {
        throw new BuildException("Missing from in containsregex");
      }
      regularExpression = new RegularExpression();
      regularExpression.setPattern(from);
      regexp = regularExpression.getRegexp(getProject());
      if (to == null) {
        return;
      }
      substitution = new Substitution();
      substitution.setExpression(to);
    }
    
    public String filter(String string)
    {
      initialize();
      if (!regexp.matches(string, options)) {
        return null;
      }
      if (substitution == null) {
        return string;
      }
      return regexp.substitute(string, substitution
        .getExpression(getProject()), options);
    }
  }
  
  public static class Trim
    extends TokenFilter.ChainableReaderFilter
  {
    public String filter(String line)
    {
      return line.trim();
    }
  }
  
  public static class IgnoreBlank
    extends TokenFilter.ChainableReaderFilter
  {
    public String filter(String line)
    {
      if (line.trim().isEmpty()) {
        return null;
      }
      return line;
    }
  }
  
  public static class DeleteCharacters
    extends ProjectComponent
    implements TokenFilter.Filter, ChainableReader
  {
    private String deleteChars = "";
    
    public void setChars(String deleteChars)
    {
      this.deleteChars = TokenFilter.resolveBackSlash(deleteChars);
    }
    
    public String filter(String string)
    {
      StringBuffer output = new StringBuffer(string.length());
      for (int i = 0; i < string.length(); i++)
      {
        char ch = string.charAt(i);
        if (!isDeleteCharacter(ch)) {
          output.append(ch);
        }
      }
      return output.toString();
    }
    
    public Reader chain(Reader reader)
    {
      new BaseFilterReader(reader)
      {
        public int read()
          throws IOException
        {
          for (;;)
          {
            int c = in.read();
            if (c == -1) {
              return c;
            }
            if (!TokenFilter.DeleteCharacters.this.isDeleteCharacter((char)c)) {
              return c;
            }
          }
        }
      };
    }
    
    private boolean isDeleteCharacter(char c)
    {
      for (int d = 0; d < deleteChars.length(); d++) {
        if (deleteChars.charAt(d) == c) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static String resolveBackSlash(String input)
  {
    return StringUtils.resolveBackSlash(input);
  }
  
  public static int convertRegexOptions(String flags)
  {
    return RegexpUtil.asOptions(flags);
  }
  
  public static abstract interface Filter
  {
    public abstract String filter(String paramString);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */