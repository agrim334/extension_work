package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;

public final class FixCrLfFilter
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final int DEFAULT_TAB_LENGTH = 8;
  private static final int MIN_TAB_LENGTH = 2;
  private static final int MAX_TAB_LENGTH = 80;
  private static final char CTRLZ = '\032';
  private int tabLength = 8;
  private CrLf eol;
  private AddAsisRemove ctrlz;
  private AddAsisRemove tabs;
  private boolean javafiles = false;
  private boolean fixlast = true;
  private boolean initialized = false;
  
  public FixCrLfFilter()
  {
    tabs = AddAsisRemove.ASIS;
    if ((Os.isFamily("mac")) && (!Os.isFamily("unix")))
    {
      ctrlz = AddAsisRemove.REMOVE;
      setEol(CrLf.MAC);
    }
    else if (Os.isFamily("dos"))
    {
      ctrlz = AddAsisRemove.ASIS;
      setEol(CrLf.DOS);
    }
    else
    {
      ctrlz = AddAsisRemove.REMOVE;
      setEol(CrLf.UNIX);
    }
  }
  
  public FixCrLfFilter(Reader in)
    throws IOException
  {
    super(in);
    
    tabs = AddAsisRemove.ASIS;
    if ((Os.isFamily("mac")) && (!Os.isFamily("unix")))
    {
      ctrlz = AddAsisRemove.REMOVE;
      setEol(CrLf.MAC);
    }
    else if (Os.isFamily("dos"))
    {
      ctrlz = AddAsisRemove.ASIS;
      setEol(CrLf.DOS);
    }
    else
    {
      ctrlz = AddAsisRemove.REMOVE;
      setEol(CrLf.UNIX);
    }
  }
  
  public Reader chain(Reader rdr)
  {
    try
    {
      FixCrLfFilter newFilter = new FixCrLfFilter(rdr);
      
      newFilter.setJavafiles(getJavafiles());
      newFilter.setEol(getEol());
      newFilter.setTab(getTab());
      newFilter.setTablength(getTablength());
      newFilter.setEof(getEof());
      newFilter.setFixlast(getFixlast());
      newFilter.initInternalFilters();
      
      return newFilter;
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
  }
  
  public AddAsisRemove getEof()
  {
    return ctrlz.newInstance();
  }
  
  public CrLf getEol()
  {
    return eol.newInstance();
  }
  
  public boolean getFixlast()
  {
    return fixlast;
  }
  
  public boolean getJavafiles()
  {
    return javafiles;
  }
  
  public AddAsisRemove getTab()
  {
    return tabs.newInstance();
  }
  
  public int getTablength()
  {
    return tabLength;
  }
  
  private static String calculateEolString(CrLf eol)
  {
    if ((eol == CrLf.CR) || (eol == CrLf.MAC)) {
      return "\r";
    }
    if ((eol == CrLf.CRLF) || (eol == CrLf.DOS)) {
      return "\r\n";
    }
    return "\n";
  }
  
  private void initInternalFilters()
  {
    in = (ctrlz == AddAsisRemove.REMOVE ? new RemoveEofFilter(in) : in);
    if (eol != CrLf.ASIS) {
      in = new NormalizeEolFilter(in, calculateEolString(eol), getFixlast());
    }
    if (tabs != AddAsisRemove.ASIS)
    {
      if (getJavafiles()) {
        in = new MaskJavaTabLiteralsFilter(in);
      }
      in = (tabs == AddAsisRemove.ADD ? new AddTabFilter(in, getTablength()) : new RemoveTabFilter(in, getTablength()));
    }
    in = (ctrlz == AddAsisRemove.ADD ? new AddEofFilter(in) : in);
    initialized = true;
  }
  
  public synchronized int read()
    throws IOException
  {
    if (!initialized) {
      initInternalFilters();
    }
    return in.read();
  }
  
  public void setEof(AddAsisRemove attr)
  {
    ctrlz = attr.resolve();
  }
  
  public void setEol(CrLf attr)
  {
    eol = attr.resolve();
  }
  
  public void setFixlast(boolean fixlast)
  {
    this.fixlast = fixlast;
  }
  
  public void setJavafiles(boolean javafiles)
  {
    this.javafiles = javafiles;
  }
  
  public void setTab(AddAsisRemove attr)
  {
    tabs = attr.resolve();
  }
  
  public void setTablength(int tabLength)
    throws IOException
  {
    if ((tabLength < 2) || (tabLength > 80)) {
      throw new IOException("tablength must be between 2 and 80");
    }
    this.tabLength = tabLength;
  }
  
  private static class SimpleFilterReader
    extends Reader
  {
    private static final int PREEMPT_BUFFER_LENGTH = 16;
    private Reader in;
    private int[] preempt = new int[16];
    private int preemptIndex = 0;
    
    public SimpleFilterReader(Reader in)
    {
      this.in = in;
    }
    
    public void push(char c)
    {
      push(c);
    }
    
    public void push(int c)
    {
      try
      {
        preempt[(preemptIndex++)] = c;
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        int[] p2 = new int[preempt.length * 2];
        System.arraycopy(preempt, 0, p2, 0, preempt.length);
        preempt = p2;
        push(c);
      }
    }
    
    public void push(char[] cs, int start, int length)
    {
      for (int i = start + length - 1; i >= start;) {
        push(cs[(i--)]);
      }
    }
    
    public void push(char[] cs)
    {
      push(cs, 0, cs.length);
    }
    
    public boolean editsBlocked()
    {
      return ((in instanceof SimpleFilterReader)) && (((SimpleFilterReader)in).editsBlocked());
    }
    
    public int read()
      throws IOException
    {
      return preemptIndex > 0 ? preempt[(--preemptIndex)] : in.read();
    }
    
    public void close()
      throws IOException
    {
      in.close();
    }
    
    public void reset()
      throws IOException
    {
      in.reset();
    }
    
    public boolean markSupported()
    {
      return in.markSupported();
    }
    
    public boolean ready()
      throws IOException
    {
      return in.ready();
    }
    
    public void mark(int i)
      throws IOException
    {
      in.mark(i);
    }
    
    public long skip(long i)
      throws IOException
    {
      return in.skip(i);
    }
    
    public int read(char[] buf)
      throws IOException
    {
      return read(buf, 0, buf.length);
    }
    
    public int read(char[] buf, int start, int length)
      throws IOException
    {
      int count = 0;
      int c = 0;
      while ((length-- > 0) && ((c = read()) != -1))
      {
        buf[(start++)] = ((char)c);
        count++;
      }
      return (count == 0) && (c == -1) ? -1 : count;
    }
  }
  
  private static class MaskJavaTabLiteralsFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private boolean editsBlocked = false;
    private static final int JAVA = 1;
    private static final int IN_CHAR_CONST = 2;
    private static final int IN_STR_CONST = 3;
    private static final int IN_SINGLE_COMMENT = 4;
    private static final int IN_MULTI_COMMENT = 5;
    private static final int TRANS_TO_COMMENT = 6;
    private static final int TRANS_FROM_MULTI = 8;
    private int state;
    
    public MaskJavaTabLiteralsFilter(Reader in)
    {
      super();
      state = 1;
    }
    
    public boolean editsBlocked()
    {
      return (editsBlocked) || (super.editsBlocked());
    }
    
    public int read()
      throws IOException
    {
      int thisChar = super.read();
      
      editsBlocked = ((state == 2) || (state == 3));
      switch (state)
      {
      case 1: 
        switch (thisChar)
        {
        case 39: 
          state = 2;
          break;
        case 34: 
          state = 3;
          break;
        case 47: 
          state = 6;
        }
        break;
      case 2: 
        switch (thisChar)
        {
        case 39: 
          state = 1;
        }
        break;
      case 3: 
        switch (thisChar)
        {
        case 34: 
          state = 1;
        }
        break;
      case 4: 
        switch (thisChar)
        {
        case 10: 
        case 13: 
          state = 1;
        }
        break;
      case 5: 
        switch (thisChar)
        {
        case 42: 
          state = 8;
        }
        break;
      case 6: 
        switch (thisChar)
        {
        case 42: 
          state = 5;
          break;
        case 47: 
          state = 4;
          break;
        case 39: 
          state = 2;
          break;
        case 34: 
          state = 3;
          break;
        default: 
          state = 1;
        }
        break;
      case 8: 
        switch (thisChar)
        {
        case 47: 
          state = 1;
        }
        break;
      }
      return thisChar;
    }
  }
  
  private static class NormalizeEolFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private boolean previousWasEOL;
    private boolean fixLast;
    private int normalizedEOL = 0;
    private char[] eol = null;
    
    public NormalizeEolFilter(Reader in, String eolString, boolean fixLast)
    {
      super();
      eol = eolString.toCharArray();
      this.fixLast = fixLast;
    }
    
    public int read()
      throws IOException
    {
      int thisChar = super.read();
      if (normalizedEOL == 0)
      {
        int numEOL = 0;
        boolean atEnd = false;
        switch (thisChar)
        {
        case 26: 
          int c = super.read();
          if (c == -1)
          {
            atEnd = true;
            if ((fixLast) && (!previousWasEOL))
            {
              numEOL = 1;
              push(thisChar);
            }
          }
          else
          {
            push(c);
          }
          break;
        case -1: 
          atEnd = true;
          if ((fixLast) && (!previousWasEOL)) {
            numEOL = 1;
          }
          break;
        case 10: 
          numEOL = 1;
          break;
        case 13: 
          numEOL = 1;
          int c1 = super.read();
          int c2 = super.read();
          if ((c1 != 13) || (c2 != 10)) {
            if (c1 == 13)
            {
              numEOL = 2;
              push(c2);
            }
            else if (c1 == 10)
            {
              push(c2);
            }
            else
            {
              push(c2);
              push(c1);
            }
          }
          break;
        }
        if (numEOL > 0)
        {
          while (numEOL-- > 0)
          {
            push(eol);
            normalizedEOL += eol.length;
          }
          previousWasEOL = true;
          thisChar = read();
        }
        else if (!atEnd)
        {
          previousWasEOL = false;
        }
      }
      else
      {
        normalizedEOL -= 1;
      }
      return thisChar;
    }
  }
  
  private static class AddEofFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private int lastChar = -1;
    
    public AddEofFilter(Reader in)
    {
      super();
    }
    
    public int read()
      throws IOException
    {
      int thisChar = super.read();
      if (thisChar == -1)
      {
        if (lastChar != 26)
        {
          lastChar = 26;
          return lastChar;
        }
      }
      else {
        lastChar = thisChar;
      }
      return thisChar;
    }
  }
  
  private static class RemoveEofFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private int lookAhead = -1;
    
    public RemoveEofFilter(Reader in)
    {
      super();
      try
      {
        lookAhead = in.read();
      }
      catch (IOException e)
      {
        lookAhead = -1;
      }
    }
    
    public int read()
      throws IOException
    {
      int lookAhead2 = super.read();
      if ((lookAhead2 == -1) && (lookAhead == 26)) {
        return -1;
      }
      int i = lookAhead;
      lookAhead = lookAhead2;
      return i;
    }
  }
  
  private static class AddTabFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private int columnNumber = 0;
    private int tabLength = 0;
    
    public AddTabFilter(Reader in, int tabLength)
    {
      super();
      this.tabLength = tabLength;
    }
    
    public int read()
      throws IOException
    {
      int c = super.read();
      switch (c)
      {
      case 10: 
      case 13: 
        columnNumber = 0;
        break;
      case 32: 
        columnNumber += 1;
        if (!editsBlocked())
        {
          int colNextTab = (columnNumber + tabLength - 1) / tabLength * tabLength;
          int countSpaces = 1;
          int numTabs = 0;
          while ((c = super.read()) != -1) {
            switch (c)
            {
            case 32: 
              if (++columnNumber == colNextTab)
              {
                numTabs++;
                countSpaces = 0;
                colNextTab += tabLength;
              }
              else
              {
                countSpaces++;
              }
              break;
            case 9: 
              columnNumber = colNextTab;
              numTabs++;
              countSpaces = 0;
              colNextTab += tabLength;
              break;
            default: 
              push(c);
            }
          }
          while (countSpaces-- > 0)
          {
            push(' ');
            columnNumber -= 1;
          }
          while (numTabs-- > 0)
          {
            push('\t');
            columnNumber -= tabLength;
          }
          c = super.read();
          switch (c)
          {
          case 32: 
            columnNumber += 1;
            break;
          case 9: 
            columnNumber += tabLength;
          }
        }
        break;
      case 9: 
        columnNumber = ((columnNumber + tabLength - 1) / tabLength * tabLength);
        break;
      default: 
        columnNumber += 1;
      }
      return c;
    }
  }
  
  private static class RemoveTabFilter
    extends FixCrLfFilter.SimpleFilterReader
  {
    private int columnNumber = 0;
    private int tabLength = 0;
    
    public RemoveTabFilter(Reader in, int tabLength)
    {
      super();
      
      this.tabLength = tabLength;
    }
    
    public int read()
      throws IOException
    {
      int c = super.read();
      switch (c)
      {
      case 10: 
      case 13: 
        columnNumber = 0;
        break;
      case 9: 
        int width = tabLength - columnNumber % tabLength;
        if (!editsBlocked())
        {
          for (; width > 1; width--) {
            push(' ');
          }
          c = 32;
        }
        columnNumber += width;
        break;
      case 11: 
      case 12: 
      default: 
        columnNumber += 1;
      }
      return c;
    }
  }
  
  public static class AddAsisRemove
    extends EnumeratedAttribute
  {
    private static final AddAsisRemove ASIS = newInstance("asis");
    private static final AddAsisRemove ADD = newInstance("add");
    private static final AddAsisRemove REMOVE = newInstance("remove");
    
    public String[] getValues()
    {
      return new String[] { "add", "asis", "remove" };
    }
    
    public boolean equals(Object other)
    {
      return ((other instanceof AddAsisRemove)) && 
        (getIndex() == ((AddAsisRemove)other).getIndex());
    }
    
    public int hashCode()
    {
      return getIndex();
    }
    
    AddAsisRemove resolve()
      throws IllegalStateException
    {
      if (equals(ASIS)) {
        return ASIS;
      }
      if (equals(ADD)) {
        return ADD;
      }
      if (equals(REMOVE)) {
        return REMOVE;
      }
      throw new IllegalStateException("No replacement for " + this);
    }
    
    private AddAsisRemove newInstance()
    {
      return newInstance(getValue());
    }
    
    public static AddAsisRemove newInstance(String value)
    {
      AddAsisRemove a = new AddAsisRemove();
      a.setValue(value);
      return a;
    }
  }
  
  public static class CrLf
    extends EnumeratedAttribute
  {
    private static final CrLf ASIS = newInstance("asis");
    private static final CrLf CR = newInstance("cr");
    private static final CrLf CRLF = newInstance("crlf");
    private static final CrLf DOS = newInstance("dos");
    private static final CrLf LF = newInstance("lf");
    private static final CrLf MAC = newInstance("mac");
    private static final CrLf UNIX = newInstance("unix");
    
    public String[] getValues()
    {
      return new String[] { "asis", "cr", "lf", "crlf", "mac", "unix", "dos" };
    }
    
    public boolean equals(Object other)
    {
      return ((other instanceof CrLf)) && (getIndex() == ((CrLf)other).getIndex());
    }
    
    public int hashCode()
    {
      return getIndex();
    }
    
    CrLf resolve()
    {
      if (equals(ASIS)) {
        return ASIS;
      }
      if ((equals(CR)) || (equals(MAC))) {
        return CR;
      }
      if ((equals(CRLF)) || (equals(DOS))) {
        return CRLF;
      }
      if ((equals(LF)) || (equals(UNIX))) {
        return LF;
      }
      throw new IllegalStateException("No replacement for " + this);
    }
    
    private CrLf newInstance()
    {
      return newInstance(getValue());
    }
    
    public static CrLf newInstance(String value)
    {
      CrLf c = new CrLf();
      c.setValue(value);
      return c;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */