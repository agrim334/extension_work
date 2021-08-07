package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class LineOrientedInterpolatingReader
  extends FilterReader
{
  public static final String DEFAULT_START_DELIM = "${";
  public static final String DEFAULT_END_DELIM = "}";
  public static final String DEFAULT_ESCAPE_SEQ = "\\";
  private static final char CARRIAGE_RETURN_CHAR = '\r';
  private static final char NEWLINE_CHAR = '\n';
  private final PushbackReader pushbackReader;
  private final Map<String, Object> context;
  private final String startDelim;
  private final String endDelim;
  private final String escapeSeq;
  private final int minExpressionSize;
  private final Reflector reflector;
  private int lineIdx = -1;
  private String line;
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim, String escapeSeq)
  {
    super(reader);
    
    this.startDelim = startDelim;
    
    this.endDelim = endDelim;
    
    this.escapeSeq = escapeSeq;
    
    minExpressionSize = (startDelim.length() + endDelim.length() + 1);
    
    this.context = Collections.unmodifiableMap(context);
    
    reflector = new Reflector();
    if ((reader instanceof PushbackReader)) {
      pushbackReader = ((PushbackReader)reader);
    } else {
      pushbackReader = new PushbackReader(reader, 1);
    }
  }
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context, String startDelim, String endDelim)
  {
    this(reader, context, startDelim, endDelim, "\\");
  }
  
  public LineOrientedInterpolatingReader(Reader reader, Map<String, ?> context)
  {
    this(reader, context, "${", "}", "\\");
  }
  
  public int read()
    throws IOException
  {
    if ((line == null) || (lineIdx >= line.length())) {
      readAndInterpolateLine();
    }
    int next = -1;
    if ((line != null) && (lineIdx < line.length())) {
      next = line.charAt(lineIdx++);
    }
    return next;
  }
  
  public int read(char[] cbuf, int off, int len)
    throws IOException
  {
    int fillCount = 0;
    for (int i = off; i < off + len; i++)
    {
      int next = read();
      if (next <= -1) {
        break;
      }
      cbuf[i] = ((char)next);
      
      fillCount++;
    }
    if (fillCount == 0) {
      fillCount = -1;
    }
    return fillCount;
  }
  
  public long skip(long n)
    throws IOException
  {
    long skipCount = 0L;
    for (long i = 0L; i < n; i += 1L)
    {
      int next = read();
      if (next < 0) {
        break;
      }
      skipCount += 1L;
    }
    return skipCount;
  }
  
  private void readAndInterpolateLine()
    throws IOException
  {
    String rawLine = readLine();
    if (rawLine != null)
    {
      Set<String> expressions = parseForExpressions(rawLine);
      
      Map<String, Object> evaluatedExpressions = evaluateExpressions(expressions);
      
      String interpolated = replaceWithInterpolatedValues(rawLine, evaluatedExpressions);
      if ((interpolated != null) && (interpolated.length() > 0))
      {
        line = interpolated;
        lineIdx = 0;
      }
    }
    else
    {
      line = null;
      lineIdx = -1;
    }
  }
  
  private String readLine()
    throws IOException
  {
    StringBuilder lineBuffer = new StringBuilder(40);
    
    boolean lastWasCR = false;
    int next;
    while ((next = pushbackReader.read()) > -1)
    {
      char c = (char)next;
      if (c == '\r')
      {
        lastWasCR = true;
        lineBuffer.append(c);
      }
      else
      {
        if (c == '\n')
        {
          lineBuffer.append(c);
          break;
        }
        if (lastWasCR)
        {
          pushbackReader.unread(c);
          break;
        }
        lineBuffer.append(c);
      }
    }
    if (lineBuffer.length() < 1) {
      return null;
    }
    return lineBuffer.toString();
  }
  
  private String replaceWithInterpolatedValues(String rawLine, Map<String, Object> evaluatedExpressions)
  {
    String result = rawLine;
    for (Object o : evaluatedExpressions.entrySet())
    {
      Map.Entry entry = (Map.Entry)o;
      
      String expression = (String)entry.getKey();
      
      String value = String.valueOf(entry.getValue());
      
      result = findAndReplaceUnlessEscaped(result, expression, value);
    }
    return result;
  }
  
  private Map<String, Object> evaluateExpressions(Set<String> expressions)
  {
    Map<String, Object> evaluated = new TreeMap();
    for (Object expression : expressions)
    {
      String rawExpression = (String)expression;
      
      String realExpression = rawExpression.substring(startDelim.length(), rawExpression.length() - endDelim.length());
      
      String[] parts = realExpression.split("\\.");
      if (parts.length > 0)
      {
        Object value = context.get(parts[0]);
        if (value != null)
        {
          for (int i = 1; i < parts.length; i++) {
            try
            {
              value = reflector.getObjectProperty(value, parts[i]);
              if (value == null) {
                break;
              }
            }
            catch (ReflectorException e)
            {
              e.printStackTrace();
              
              break;
            }
          }
          evaluated.put(rawExpression, value);
        }
      }
    }
    return evaluated;
  }
  
  private Set<String> parseForExpressions(String rawLine)
  {
    Set<String> expressions = new HashSet();
    if (rawLine != null)
    {
      int placeholder = -1;
      do
      {
        int start = findDelimiter(rawLine, startDelim, placeholder);
        if (start < 0) {
          break;
        }
        int end = findDelimiter(rawLine, endDelim, start + 1);
        if (end < 0) {
          break;
        }
        expressions.add(rawLine.substring(start, end + endDelim.length()));
        
        placeholder = end + 1;
      } while (placeholder < rawLine.length() - minExpressionSize);
    }
    return expressions;
  }
  
  private int findDelimiter(String rawLine, String delimiter, int lastPos)
  {
    int placeholder = lastPos;
    int position;
    do
    {
      position = rawLine.indexOf(delimiter, placeholder);
      if (position < 0) {
        break;
      }
      int escEndIdx = rawLine.indexOf(escapeSeq, placeholder) + escapeSeq.length();
      if ((escEndIdx > escapeSeq.length() - 1) && (escEndIdx == position))
      {
        placeholder = position + 1;
        position = -1;
      }
    } while ((position < 0) && (placeholder < rawLine.length() - endDelim.length()));
    return position;
  }
  
  private String findAndReplaceUnlessEscaped(String rawLine, String search, String replace)
  {
    StringBuilder lineBuffer = new StringBuilder((int)(rawLine.length() * 1.5D));
    
    int lastReplacement = -1;
    do
    {
      int nextReplacement = rawLine.indexOf(search, lastReplacement + 1);
      if (nextReplacement <= -1) {
        break;
      }
      if (lastReplacement < 0) {
        lastReplacement = 0;
      }
      lineBuffer.append(rawLine, lastReplacement, nextReplacement);
      
      int escIdx = rawLine.indexOf(escapeSeq, lastReplacement + 1);
      if ((escIdx > -1) && (escIdx + escapeSeq.length() == nextReplacement))
      {
        lineBuffer.setLength(lineBuffer.length() - escapeSeq.length());
        lineBuffer.append(search);
      }
      else
      {
        lineBuffer.append(replace);
      }
      lastReplacement = nextReplacement + search.length();
    } while (lastReplacement > -1);
    if (lastReplacement < rawLine.length()) {
      lineBuffer.append(rawLine, lastReplacement, rawLine.length());
    }
    return lineBuffer.toString();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.LineOrientedInterpolatingReader
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */