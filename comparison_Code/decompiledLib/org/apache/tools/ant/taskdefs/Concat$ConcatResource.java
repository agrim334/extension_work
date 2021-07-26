package org.apache.tools.ant.taskdefs;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.ReaderInputStream;

final class Concat$ConcatResource
  extends Resource
{
  private ResourceCollection c;
  
  private Concat$ConcatResource(Concat paramConcat, ResourceCollection c)
  {
    this.c = c;
  }
  
  public InputStream getInputStream()
  {
    if (Concat.access$300(this$0))
    {
      ConcatResourceInputStream result = new ConcatResourceInputStream(c);
      result.setManagingComponent(this);
      return result;
    }
    Reader resourceReader = Concat.access$600(this$0, new Concat.MultiReader(this$0, c
      .iterator(), Concat.access$400(this$0), null));
    Reader rdr;
    Reader rdr;
    if ((Concat.access$700(this$0) == null) && (Concat.access$800(this$0) == null))
    {
      rdr = resourceReader;
    }
    else
    {
      int readerCount = 1;
      if (Concat.access$700(this$0) != null) {
        readerCount++;
      }
      if (Concat.access$800(this$0) != null) {
        readerCount++;
      }
      Reader[] readers = new Reader[readerCount];
      int pos = 0;
      if (Concat.access$700(this$0) != null)
      {
        readers[pos] = new StringReader(Concat.access$700(this$0).getValue());
        if (Concat.TextElement.access$900(Concat.access$700(this$0))) {
          readers[pos] = Concat.access$600(this$0, readers[pos]);
        }
        pos++;
      }
      readers[(pos++)] = resourceReader;
      if (Concat.access$800(this$0) != null)
      {
        readers[pos] = new StringReader(Concat.access$800(this$0).getValue());
        if (Concat.TextElement.access$900(Concat.access$800(this$0))) {
          readers[pos] = Concat.access$600(this$0, readers[pos]);
        }
      }
      rdr = new Concat.MultiReader(this$0, Arrays.asList(readers).iterator(), Concat.access$1000(this$0), null);
    }
    return Concat.access$1100(this$0) == null ? new ReaderInputStream(rdr) : 
      new ReaderInputStream(rdr, Concat.access$1100(this$0));
  }
  
  public String getName()
  {
    return Concat.access$1200(this$0) == null ? 
      "concat (" + String.valueOf(c) + ")" : Concat.access$1200(this$0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Concat.ConcatResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */