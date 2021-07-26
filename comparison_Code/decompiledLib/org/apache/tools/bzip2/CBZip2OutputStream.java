package org.apache.tools.bzip2;

import java.io.IOException;
import java.io.OutputStream;

public class CBZip2OutputStream
  extends OutputStream
  implements BZip2Constants
{
  public static final int MIN_BLOCKSIZE = 1;
  public static final int MAX_BLOCKSIZE = 9;
  protected static final int SETMASK = 2097152;
  protected static final int CLEARMASK = -2097153;
  protected static final int GREATER_ICOST = 15;
  protected static final int LESSER_ICOST = 0;
  protected static final int SMALL_THRESH = 20;
  protected static final int DEPTH_THRESH = 10;
  protected static final int WORK_FACTOR = 30;
  protected static final int QSORT_STACK_SIZE = 1000;
  private static final int[] INCS = { 1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484 };
  private int last;
  private final int blockSize100k;
  private int bsBuff;
  private int bsLive;
  
  protected static void hbMakeCodeLengths(char[] len, int[] freq, int alphaSize, int maxLen)
  {
    int[] heap = new int['Ȅ'];
    int[] weight = new int['Ȅ'];
    int[] parent = new int['Ȅ'];
    
    int i = alphaSize;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      weight[(i + 1)] = ((freq[i] == 0 ? 1 : freq[i]) << 8);
    }
    for (boolean tooLong = true; tooLong;)
    {
      tooLong = false;
      
      int nNodes = alphaSize;
      int nHeap = 0;
      heap[0] = 0;
      weight[0] = 0;
      parent[0] = -2;
      for (int i = 1; i <= alphaSize; i++)
      {
        parent[i] = -1;
        nHeap++;
        heap[nHeap] = i;
        
        int zz = nHeap;
        int tmp = heap[zz];
        while (weight[tmp] < weight[heap[(zz >> 1)]])
        {
          heap[zz] = heap[(zz >> 1)];
          zz >>= 1;
        }
        heap[zz] = tmp;
      }
      while (nHeap > 1)
      {
        int n1 = heap[1];
        heap[1] = heap[nHeap];
        nHeap--;
        
        int yy = 0;
        int zz = 1;
        int tmp = heap[1];
        for (;;)
        {
          yy = zz << 1;
          if (yy > nHeap) {
            break;
          }
          if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
            yy++;
          }
          if (weight[tmp] < weight[heap[yy]]) {
            break;
          }
          heap[zz] = heap[yy];
          zz = yy;
        }
        heap[zz] = tmp;
        
        int n2 = heap[1];
        heap[1] = heap[nHeap];
        nHeap--;
        
        yy = 0;
        zz = 1;
        tmp = heap[1];
        for (;;)
        {
          yy = zz << 1;
          if (yy > nHeap) {
            break;
          }
          if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
            yy++;
          }
          if (weight[tmp] < weight[heap[yy]]) {
            break;
          }
          heap[zz] = heap[yy];
          zz = yy;
        }
        heap[zz] = tmp;
        nNodes++;
        parent[n1] = (parent[n2] = nNodes);
        
        int weight_n1 = weight[n1];
        int weight_n2 = weight[n2];
        weight[nNodes] = 
        
          ((weight_n1 & 0xFF00) + (weight_n2 & 0xFF00) | 1 + ((weight_n1 & 0xFF) > (weight_n2 & 0xFF) ? weight_n1 & 0xFF : weight_n2 & 0xFF));
        
        parent[nNodes] = -1;
        nHeap++;
        heap[nHeap] = nNodes;
        
        tmp = 0;
        zz = nHeap;
        tmp = heap[zz];
        int weight_tmp = weight[tmp];
        while (weight_tmp < weight[heap[(zz >> 1)]])
        {
          heap[zz] = heap[(zz >> 1)];
          zz >>= 1;
        }
        heap[zz] = tmp;
      }
      for (int i = 1; i <= alphaSize; i++)
      {
        int j = 0;
        int k = i;
        int parent_k;
        while ((parent_k = parent[k]) >= 0)
        {
          k = parent_k;
          j++;
        }
        len[(i - 1)] = ((char)j);
        if (j > maxLen) {
          tooLong = true;
        }
      }
      if (tooLong) {
        for (int i = 1; i < alphaSize; i++)
        {
          int j = weight[i] >> 8;
          j = 1 + (j >> 1);
          weight[i] = (j << 8);
        }
      }
    }
  }
  
  private static void hbMakeCodeLengths(byte[] len, int[] freq, Data dat, int alphaSize, int maxLen)
  {
    int[] heap = heap;
    int[] weight = weight;
    int[] parent = parent;
    
    int i = alphaSize;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      weight[(i + 1)] = ((freq[i] == 0 ? 1 : freq[i]) << 8);
    }
    for (boolean tooLong = true; tooLong;)
    {
      tooLong = false;
      
      int nNodes = alphaSize;
      int nHeap = 0;
      heap[0] = 0;
      weight[0] = 0;
      parent[0] = -2;
      for (int i = 1; i <= alphaSize; i++)
      {
        parent[i] = -1;
        nHeap++;
        heap[nHeap] = i;
        
        int zz = nHeap;
        int tmp = heap[zz];
        while (weight[tmp] < weight[heap[(zz >> 1)]])
        {
          heap[zz] = heap[(zz >> 1)];
          zz >>= 1;
        }
        heap[zz] = tmp;
      }
      while (nHeap > 1)
      {
        int n1 = heap[1];
        heap[1] = heap[nHeap];
        nHeap--;
        
        int yy = 0;
        int zz = 1;
        int tmp = heap[1];
        for (;;)
        {
          yy = zz << 1;
          if (yy > nHeap) {
            break;
          }
          if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
            yy++;
          }
          if (weight[tmp] < weight[heap[yy]]) {
            break;
          }
          heap[zz] = heap[yy];
          zz = yy;
        }
        heap[zz] = tmp;
        
        int n2 = heap[1];
        heap[1] = heap[nHeap];
        nHeap--;
        
        yy = 0;
        zz = 1;
        tmp = heap[1];
        for (;;)
        {
          yy = zz << 1;
          if (yy > nHeap) {
            break;
          }
          if ((yy < nHeap) && (weight[heap[(yy + 1)]] < weight[heap[yy]])) {
            yy++;
          }
          if (weight[tmp] < weight[heap[yy]]) {
            break;
          }
          heap[zz] = heap[yy];
          zz = yy;
        }
        heap[zz] = tmp;
        nNodes++;
        parent[n1] = (parent[n2] = nNodes);
        
        int weight_n1 = weight[n1];
        int weight_n2 = weight[n2];
        weight[nNodes] = 
        
          ((weight_n1 & 0xFF00) + (weight_n2 & 0xFF00) | 1 + ((weight_n1 & 0xFF) > (weight_n2 & 0xFF) ? weight_n1 & 0xFF : weight_n2 & 0xFF));
        
        parent[nNodes] = -1;
        nHeap++;
        heap[nHeap] = nNodes;
        
        tmp = 0;
        zz = nHeap;
        tmp = heap[zz];
        int weight_tmp = weight[tmp];
        while (weight_tmp < weight[heap[(zz >> 1)]])
        {
          heap[zz] = heap[(zz >> 1)];
          zz >>= 1;
        }
        heap[zz] = tmp;
      }
      for (int i = 1; i <= alphaSize; i++)
      {
        int j = 0;
        int k = i;
        int parent_k;
        while ((parent_k = parent[k]) >= 0)
        {
          k = parent_k;
          j++;
        }
        len[(i - 1)] = ((byte)j);
        if (j > maxLen) {
          tooLong = true;
        }
      }
      if (tooLong) {
        for (int i = 1; i < alphaSize; i++)
        {
          int j = weight[i] >> 8;
          j = 1 + (j >> 1);
          weight[i] = (j << 8);
        }
      }
    }
  }
  
  private final CRC crc = new CRC();
  private int nInUse;
  private int nMTF;
  private int currentChar = -1;
  private int runLength = 0;
  private int blockCRC;
  private int combinedCRC;
  private final int allowableBlockSize;
  private Data data;
  private BlockSort blockSorter;
  private OutputStream out;
  
  public static int chooseBlockSize(long inputLength)
  {
    return inputLength > 0L ? 
      (int)Math.min(inputLength / 132000L + 1L, 9L) : 9;
  }
  
  public CBZip2OutputStream(OutputStream out)
    throws IOException
  {
    this(out, 9);
  }
  
  public CBZip2OutputStream(OutputStream out, int blockSize)
    throws IOException
  {
    if (blockSize < 1) {
      throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
    }
    if (blockSize > 9) {
      throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
    }
    blockSize100k = blockSize;
    this.out = out;
    
    allowableBlockSize = (blockSize100k * 100000 - 20);
    init();
  }
  
  public void write(int b)
    throws IOException
  {
    if (out != null) {
      write0(b);
    } else {
      throw new IOException("closed");
    }
  }
  
  private void writeRun()
    throws IOException
  {
    int lastShadow = last;
    if (lastShadow < allowableBlockSize)
    {
      int currentCharShadow = currentChar;
      Data dataShadow = data;
      inUse[currentCharShadow] = true;
      byte ch = (byte)currentCharShadow;
      
      int runLengthShadow = runLength;
      crc.updateCRC(currentCharShadow, runLengthShadow);
      byte[] block = block;
      switch (runLengthShadow)
      {
      case 1: 
        block[(lastShadow + 2)] = ch;
        last = (lastShadow + 1);
        break;
      case 2: 
        block[(lastShadow + 2)] = ch;
        block[(lastShadow + 3)] = ch;
        last = (lastShadow + 2);
        break;
      case 3: 
        block[(lastShadow + 2)] = ch;
        block[(lastShadow + 3)] = ch;
        block[(lastShadow + 4)] = ch;
        last = (lastShadow + 3);
        break;
      default: 
        runLengthShadow -= 4;
        inUse[runLengthShadow] = true;
        block[(lastShadow + 2)] = ch;
        block[(lastShadow + 3)] = ch;
        block[(lastShadow + 4)] = ch;
        block[(lastShadow + 5)] = ch;
        block[(lastShadow + 6)] = ((byte)runLengthShadow);
        last = (lastShadow + 5);
      }
    }
    else
    {
      endBlock();
      initBlock();
      writeRun();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    finish();
    super.finalize();
  }
  
  /* Error */
  public void finish()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 78	org/apache/tools/bzip2/CBZip2OutputStream:out	Ljava/io/OutputStream;
    //   4: ifnull +63 -> 67
    //   7: aload_0
    //   8: getfield 47	org/apache/tools/bzip2/CBZip2OutputStream:runLength	I
    //   11: ifle +7 -> 18
    //   14: aload_0
    //   15: invokespecial 123	org/apache/tools/bzip2/CBZip2OutputStream:writeRun	()V
    //   18: aload_0
    //   19: iconst_m1
    //   20: putfield 43	org/apache/tools/bzip2/CBZip2OutputStream:currentChar	I
    //   23: aload_0
    //   24: invokespecial 117	org/apache/tools/bzip2/CBZip2OutputStream:endBlock	()V
    //   27: aload_0
    //   28: invokespecial 134	org/apache/tools/bzip2/CBZip2OutputStream:endCompression	()V
    //   31: aload_0
    //   32: aconst_null
    //   33: putfield 78	org/apache/tools/bzip2/CBZip2OutputStream:out	Ljava/io/OutputStream;
    //   36: aload_0
    //   37: aconst_null
    //   38: putfield 101	org/apache/tools/bzip2/CBZip2OutputStream:data	Lorg/apache/tools/bzip2/CBZip2OutputStream$Data;
    //   41: aload_0
    //   42: aconst_null
    //   43: putfield 137	org/apache/tools/bzip2/CBZip2OutputStream:blockSorter	Lorg/apache/tools/bzip2/BlockSort;
    //   46: goto +21 -> 67
    //   49: astore_1
    //   50: aload_0
    //   51: aconst_null
    //   52: putfield 78	org/apache/tools/bzip2/CBZip2OutputStream:out	Ljava/io/OutputStream;
    //   55: aload_0
    //   56: aconst_null
    //   57: putfield 101	org/apache/tools/bzip2/CBZip2OutputStream:data	Lorg/apache/tools/bzip2/CBZip2OutputStream$Data;
    //   60: aload_0
    //   61: aconst_null
    //   62: putfield 137	org/apache/tools/bzip2/CBZip2OutputStream:blockSorter	Lorg/apache/tools/bzip2/BlockSort;
    //   65: aload_1
    //   66: athrow
    //   67: return
    // Line number table:
    //   Java source line #726	-> byte code offset #0
    //   Java source line #728	-> byte code offset #7
    //   Java source line #729	-> byte code offset #14
    //   Java source line #731	-> byte code offset #18
    //   Java source line #732	-> byte code offset #23
    //   Java source line #733	-> byte code offset #27
    //   Java source line #735	-> byte code offset #31
    //   Java source line #736	-> byte code offset #36
    //   Java source line #737	-> byte code offset #41
    //   Java source line #738	-> byte code offset #46
    //   Java source line #735	-> byte code offset #49
    //   Java source line #736	-> byte code offset #55
    //   Java source line #737	-> byte code offset #60
    //   Java source line #738	-> byte code offset #65
    //   Java source line #740	-> byte code offset #67
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	this	CBZip2OutputStream
    //   49	17	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	31	49	finally
  }
  
  public void close()
    throws IOException
  {
    if (out != null)
    {
      OutputStream outShadow = out;
      finish();
      outShadow.close();
    }
  }
  
  public void flush()
    throws IOException
  {
    OutputStream outShadow = out;
    if (outShadow != null) {
      outShadow.flush();
    }
  }
  
  private void init()
    throws IOException
  {
    data = new Data(blockSize100k);
    blockSorter = new BlockSort(data);
    
    bsPutUByte(104);
    bsPutUByte(48 + blockSize100k);
    
    combinedCRC = 0;
    initBlock();
  }
  
  private void initBlock()
  {
    crc.initialiseCRC();
    last = -1;
    
    boolean[] inUse = data.inUse;
    int i = 256;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      inUse[i] = false;
    }
  }
  
  private void endBlock()
    throws IOException
  {
    blockCRC = crc.getFinalCRC();
    combinedCRC = (combinedCRC << 1 | combinedCRC >>> 31);
    combinedCRC ^= blockCRC;
    if (last == -1) {
      return;
    }
    blockSort();
    
    bsPutUByte(49);
    bsPutUByte(65);
    bsPutUByte(89);
    bsPutUByte(38);
    bsPutUByte(83);
    bsPutUByte(89);
    
    bsPutInt(blockCRC);
    
    bsW(1, 0);
    
    moveToFrontCodeAndSend();
  }
  
  private void endCompression()
    throws IOException
  {
    bsPutUByte(23);
    bsPutUByte(114);
    bsPutUByte(69);
    bsPutUByte(56);
    bsPutUByte(80);
    bsPutUByte(144);
    
    bsPutInt(combinedCRC);
    bsFinishedWithStream();
  }
  
  public final int getBlockSize()
  {
    return blockSize100k;
  }
  
  public void write(byte[] buf, int offs, int len)
    throws IOException
  {
    if (offs < 0) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
    }
    if (len < 0) {
      throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
    }
    if (offs + len > buf.length) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
    }
    if (out == null) {
      throw new IOException("stream closed");
    }
    for (int hi = offs + len; offs < hi;) {
      write0(buf[(offs++)]);
    }
  }
  
  private void write0(int b)
    throws IOException
  {
    if (currentChar != -1)
    {
      b &= 0xFF;
      if (currentChar == b)
      {
        if (++runLength > 254)
        {
          writeRun();
          currentChar = -1;
          runLength = 0;
        }
      }
      else
      {
        writeRun();
        runLength = 1;
        currentChar = b;
      }
    }
    else
    {
      currentChar = (b & 0xFF);
      runLength += 1;
    }
  }
  
  private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize)
  {
    int vec = 0;
    for (int n = minLen; n <= maxLen; n++)
    {
      for (int i = 0; i < alphaSize; i++) {
        if ((length[i] & 0xFF) == n)
        {
          code[i] = vec;
          vec++;
        }
      }
      vec <<= 1;
    }
  }
  
  private void bsFinishedWithStream()
    throws IOException
  {
    while (bsLive > 0)
    {
      int ch = bsBuff >> 24;
      out.write(ch);
      bsBuff <<= 8;
      bsLive -= 8;
    }
  }
  
  private void bsW(int n, int v)
    throws IOException
  {
    OutputStream outShadow = out;
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    while (bsLiveShadow >= 8)
    {
      outShadow.write(bsBuffShadow >> 24);
      bsBuffShadow <<= 8;
      bsLiveShadow -= 8;
    }
    bsBuff = (bsBuffShadow | v << 32 - bsLiveShadow - n);
    bsLive = (bsLiveShadow + n);
  }
  
  private void bsPutUByte(int c)
    throws IOException
  {
    bsW(8, c);
  }
  
  private void bsPutInt(int u)
    throws IOException
  {
    bsW(8, u >> 24 & 0xFF);
    bsW(8, u >> 16 & 0xFF);
    bsW(8, u >> 8 & 0xFF);
    bsW(8, u & 0xFF);
  }
  
  private void sendMTFValues()
    throws IOException
  {
    byte[][] len = data.sendMTFValues_len;
    int alphaSize = nInUse + 2;
    
    int t = 6;
    for (;;)
    {
      t--;
      if (t < 0) {
        break;
      }
      byte[] len_t = len[t];
      int v = alphaSize;
      for (;;)
      {
        v--;
        if (v < 0) {
          break;
        }
        len_t[v] = 15;
      }
    }
    int nGroups = nMTF < 2400 ? 5 : nMTF < 1200 ? 4 : nMTF < 600 ? 3 : nMTF < 200 ? 2 : 6;
    
    sendMTFValues0(nGroups, alphaSize);
    
    int nSelectors = sendMTFValues1(nGroups, alphaSize);
    
    sendMTFValues2(nGroups, nSelectors);
    
    sendMTFValues3(nGroups, alphaSize);
    
    sendMTFValues4();
    
    sendMTFValues5(nGroups, nSelectors);
    
    sendMTFValues6(nGroups, alphaSize);
    
    sendMTFValues7();
  }
  
  private void sendMTFValues0(int nGroups, int alphaSize)
  {
    byte[][] len = data.sendMTFValues_len;
    int[] mtfFreq = data.mtfFreq;
    
    int remF = nMTF;
    int gs = 0;
    for (int nPart = nGroups; nPart > 0; nPart--)
    {
      int tFreq = remF / nPart;
      int ge = gs - 1;
      int aFreq = 0;
      while ((aFreq < tFreq) && (ge < alphaSize - 1)) {
        aFreq += mtfFreq[(++ge)];
      }
      if ((ge > gs) && (nPart != nGroups) && (nPart != 1) && ((nGroups - nPart & 0x1) != 0)) {
        aFreq -= mtfFreq[(ge--)];
      }
      byte[] len_np = len[(nPart - 1)];
      int v = alphaSize;
      for (;;)
      {
        v--;
        if (v < 0) {
          break;
        }
        if ((v >= gs) && (v <= ge)) {
          len_np[v] = 0;
        } else {
          len_np[v] = 15;
        }
      }
      gs = ge + 1;
      remF -= aFreq;
    }
  }
  
  private int sendMTFValues1(int nGroups, int alphaSize)
  {
    Data dataShadow = data;
    int[][] rfreq = sendMTFValues_rfreq;
    int[] fave = sendMTFValues_fave;
    short[] cost = sendMTFValues_cost;
    char[] sfmap = sfmap;
    byte[] selector = selector;
    byte[][] len = sendMTFValues_len;
    byte[] len_0 = len[0];
    byte[] len_1 = len[1];
    byte[] len_2 = len[2];
    byte[] len_3 = len[3];
    byte[] len_4 = len[4];
    byte[] len_5 = len[5];
    int nMTFShadow = nMTF;
    
    int nSelectors = 0;
    for (int iter = 0; iter < 4; iter++)
    {
      int t = nGroups;
      for (;;)
      {
        t--;
        if (t < 0) {
          break;
        }
        fave[t] = 0;
        int[] rfreqt = rfreq[t];
        int i = alphaSize;
        for (;;)
        {
          i--;
          if (i < 0) {
            break;
          }
          rfreqt[i] = 0;
        }
      }
      nSelectors = 0;
      for (int gs = 0; gs < nMTF;)
      {
        int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
        if (nGroups == 6)
        {
          short cost0 = 0;
          short cost1 = 0;
          short cost2 = 0;
          short cost3 = 0;
          short cost4 = 0;
          short cost5 = 0;
          for (int i = gs; i <= ge; i++)
          {
            int icv = sfmap[i];
            cost0 = (short)(cost0 + (len_0[icv] & 0xFF));
            cost1 = (short)(cost1 + (len_1[icv] & 0xFF));
            cost2 = (short)(cost2 + (len_2[icv] & 0xFF));
            cost3 = (short)(cost3 + (len_3[icv] & 0xFF));
            cost4 = (short)(cost4 + (len_4[icv] & 0xFF));
            cost5 = (short)(cost5 + (len_5[icv] & 0xFF));
          }
          cost[0] = cost0;
          cost[1] = cost1;
          cost[2] = cost2;
          cost[3] = cost3;
          cost[4] = cost4;
          cost[5] = cost5;
        }
        else
        {
          int t = nGroups;
          for (;;)
          {
            t--;
            if (t < 0) {
              break;
            }
            cost[t] = 0;
          }
          for (int i = gs; i <= ge; i++)
          {
            int icv = sfmap[i];
            int t = nGroups;
            for (;;)
            {
              t--;
              if (t < 0) {
                break;
              }
              int tmp403_401 = t; short[] tmp403_399 = cost;tmp403_399[tmp403_401] = ((short)(tmp403_399[tmp403_401] + (len[t][icv] & 0xFF)));
            }
          }
        }
        int bt = -1;
        int t = nGroups;int bc = 999999999;
        for (;;)
        {
          t--;
          if (t < 0) {
            break;
          }
          int cost_t = cost[t];
          if (cost_t < bc)
          {
            bc = cost_t;
            bt = t;
          }
        }
        fave[bt] += 1;
        selector[nSelectors] = ((byte)bt);
        nSelectors++;
        
        int[] rfreq_bt = rfreq[bt];
        for (int i = gs; i <= ge; i++) {
          rfreq_bt[sfmap[i]] += 1;
        }
        gs = ge + 1;
      }
      for (int t = 0; t < nGroups; t++) {
        hbMakeCodeLengths(len[t], rfreq[t], data, alphaSize, 20);
      }
    }
    return nSelectors;
  }
  
  private void sendMTFValues2(int nGroups, int nSelectors)
  {
    Data dataShadow = data;
    byte[] pos = sendMTFValues2_pos;
    
    int i = nGroups;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      pos[i] = ((byte)i);
    }
    for (int i = 0; i < nSelectors; i++)
    {
      byte ll_i = selector[i];
      byte tmp = pos[0];
      int j = 0;
      while (ll_i != tmp)
      {
        j++;
        byte tmp2 = tmp;
        tmp = pos[j];
        pos[j] = tmp2;
      }
      pos[0] = tmp;
      selectorMtf[i] = ((byte)j);
    }
  }
  
  private void sendMTFValues3(int nGroups, int alphaSize)
  {
    int[][] code = data.sendMTFValues_code;
    byte[][] len = data.sendMTFValues_len;
    for (int t = 0; t < nGroups; t++)
    {
      int minLen = 32;
      int maxLen = 0;
      byte[] len_t = len[t];
      int i = alphaSize;
      for (;;)
      {
        i--;
        if (i < 0) {
          break;
        }
        int l = len_t[i] & 0xFF;
        if (l > maxLen) {
          maxLen = l;
        }
        if (l < minLen) {
          minLen = l;
        }
      }
      hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
    }
  }
  
  private void sendMTFValues4()
    throws IOException
  {
    boolean[] inUse = data.inUse;
    boolean[] inUse16 = data.sentMTFValues4_inUse16;
    
    int i = 16;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      inUse16[i] = false;
      int i16 = i * 16;
      int j = 16;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        if (inUse[(i16 + j)] != 0) {
          inUse16[i] = true;
        }
      }
    }
    for (int i = 0; i < 16; i++) {
      bsW(1, inUse16[i] != 0 ? 1 : 0);
    }
    OutputStream outShadow = out;
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    for (int i = 0; i < 16; i++) {
      if (inUse16[i] != 0)
      {
        int i16 = i * 16;
        for (int j = 0; j < 16; j++)
        {
          while (bsLiveShadow >= 8)
          {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
          }
          if (inUse[(i16 + j)] != 0) {
            bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
          }
          bsLiveShadow++;
        }
      }
    }
    bsBuff = bsBuffShadow;
    bsLive = bsLiveShadow;
  }
  
  private void sendMTFValues5(int nGroups, int nSelectors)
    throws IOException
  {
    bsW(3, nGroups);
    bsW(15, nSelectors);
    
    OutputStream outShadow = out;
    byte[] selectorMtf = data.selectorMtf;
    
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    for (int i = 0; i < nSelectors; i++)
    {
      int j = 0;
      for (int hj = selectorMtf[i] & 0xFF; j < hj; j++)
      {
        while (bsLiveShadow >= 8)
        {
          outShadow.write(bsBuffShadow >> 24);
          bsBuffShadow <<= 8;
          bsLiveShadow -= 8;
        }
        bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
        bsLiveShadow++;
      }
      while (bsLiveShadow >= 8)
      {
        outShadow.write(bsBuffShadow >> 24);
        bsBuffShadow <<= 8;
        bsLiveShadow -= 8;
      }
      bsLiveShadow++;
    }
    bsBuff = bsBuffShadow;
    bsLive = bsLiveShadow;
  }
  
  private void sendMTFValues6(int nGroups, int alphaSize)
    throws IOException
  {
    byte[][] len = data.sendMTFValues_len;
    OutputStream outShadow = out;
    
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    for (int t = 0; t < nGroups; t++)
    {
      byte[] len_t = len[t];
      int curr = len_t[0] & 0xFF;
      while (bsLiveShadow >= 8)
      {
        outShadow.write(bsBuffShadow >> 24);
        bsBuffShadow <<= 8;
        bsLiveShadow -= 8;
      }
      bsBuffShadow |= curr << 32 - bsLiveShadow - 5;
      bsLiveShadow += 5;
      for (int i = 0; i < alphaSize; i++)
      {
        int lti = len_t[i] & 0xFF;
        while (curr < lti)
        {
          while (bsLiveShadow >= 8)
          {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
          }
          bsBuffShadow |= 2 << 32 - bsLiveShadow - 2;
          bsLiveShadow += 2;
          
          curr++;
        }
        while (curr > lti)
        {
          while (bsLiveShadow >= 8)
          {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
          }
          bsBuffShadow |= 3 << 32 - bsLiveShadow - 2;
          bsLiveShadow += 2;
          
          curr--;
        }
        while (bsLiveShadow >= 8)
        {
          outShadow.write(bsBuffShadow >> 24);
          bsBuffShadow <<= 8;
          bsLiveShadow -= 8;
        }
        bsLiveShadow++;
      }
    }
    bsBuff = bsBuffShadow;
    bsLive = bsLiveShadow;
  }
  
  private void sendMTFValues7()
    throws IOException
  {
    Data dataShadow = data;
    byte[][] len = sendMTFValues_len;
    int[][] code = sendMTFValues_code;
    OutputStream outShadow = out;
    byte[] selector = selector;
    char[] sfmap = sfmap;
    int nMTFShadow = nMTF;
    
    int selCtr = 0;
    
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    for (int gs = 0; gs < nMTFShadow;)
    {
      int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
      int selector_selCtr = selector[selCtr] & 0xFF;
      int[] code_selCtr = code[selector_selCtr];
      byte[] len_selCtr = len[selector_selCtr];
      while (gs <= ge)
      {
        int sfmap_i = sfmap[gs];
        while (bsLiveShadow >= 8)
        {
          outShadow.write(bsBuffShadow >> 24);
          bsBuffShadow <<= 8;
          bsLiveShadow -= 8;
        }
        int n = len_selCtr[sfmap_i] & 0xFF;
        bsBuffShadow |= code_selCtr[sfmap_i] << 32 - bsLiveShadow - n;
        bsLiveShadow += n;
        
        gs++;
      }
      gs = ge + 1;
      selCtr++;
    }
    bsBuff = bsBuffShadow;
    bsLive = bsLiveShadow;
  }
  
  private void moveToFrontCodeAndSend()
    throws IOException
  {
    bsW(24, data.origPtr);
    generateMTFValues();
    sendMTFValues();
  }
  
  private void blockSort()
  {
    blockSorter.blockSort(data, last);
  }
  
  private void generateMTFValues()
  {
    int lastShadow = last;
    Data dataShadow = data;
    boolean[] inUse = inUse;
    byte[] block = block;
    int[] fmap = fmap;
    char[] sfmap = sfmap;
    int[] mtfFreq = mtfFreq;
    byte[] unseqToSeq = unseqToSeq;
    byte[] yy = generateMTFValues_yy;
    
    int nInUseShadow = 0;
    for (int i = 0; i < 256; i++) {
      if (inUse[i] != 0)
      {
        unseqToSeq[i] = ((byte)nInUseShadow);
        nInUseShadow++;
      }
    }
    nInUse = nInUseShadow;
    
    int eob = nInUseShadow + 1;
    for (int i = eob; i >= 0; i--) {
      mtfFreq[i] = 0;
    }
    int i = nInUseShadow;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      yy[i] = ((byte)i);
    }
    int wr = 0;
    int zPend = 0;
    for (int i = 0; i <= lastShadow; i++)
    {
      byte ll_i = unseqToSeq[(block[fmap[i]] & 0xFF)];
      byte tmp = yy[0];
      int j = 0;
      while (ll_i != tmp)
      {
        j++;
        byte tmp2 = tmp;
        tmp = yy[j];
        yy[j] = tmp2;
      }
      yy[0] = tmp;
      if (j == 0)
      {
        zPend++;
      }
      else
      {
        if (zPend > 0)
        {
          zPend--;
          for (;;)
          {
            if ((zPend & 0x1) == 0)
            {
              sfmap[wr] = '\000';
              wr++;
              mtfFreq[0] += 1;
            }
            else
            {
              sfmap[wr] = '\001';
              wr++;
              mtfFreq[1] += 1;
            }
            if (zPend < 2) {
              break;
            }
            zPend = zPend - 2 >> 1;
          }
          zPend = 0;
        }
        sfmap[wr] = ((char)(j + 1));
        wr++;
        mtfFreq[(j + 1)] += 1;
      }
    }
    if (zPend > 0)
    {
      zPend--;
      for (;;)
      {
        if ((zPend & 0x1) == 0)
        {
          sfmap[wr] = '\000';
          wr++;
          mtfFreq[0] += 1;
        }
        else
        {
          sfmap[wr] = '\001';
          wr++;
          mtfFreq[1] += 1;
        }
        if (zPend < 2) {
          break;
        }
        zPend = zPend - 2 >> 1;
      }
    }
    sfmap[wr] = ((char)eob);
    mtfFreq[eob] += 1;
    nMTF = (wr + 1);
  }
  
  static final class Data
  {
    final boolean[] inUse = new boolean['Ā'];
    final byte[] unseqToSeq = new byte['Ā'];
    final int[] mtfFreq = new int['Ă'];
    final byte[] selector = new byte['䙒'];
    final byte[] selectorMtf = new byte['䙒'];
    final byte[] generateMTFValues_yy = new byte['Ā'];
    final byte[][] sendMTFValues_len = new byte[6]['Ă'];
    final int[][] sendMTFValues_rfreq = new int[6]['Ă'];
    final int[] sendMTFValues_fave = new int[6];
    final short[] sendMTFValues_cost = new short[6];
    final int[][] sendMTFValues_code = new int[6]['Ă'];
    final byte[] sendMTFValues2_pos = new byte[6];
    final boolean[] sentMTFValues4_inUse16 = new boolean[16];
    final int[] heap = new int['Ą'];
    final int[] weight = new int['Ȅ'];
    final int[] parent = new int['Ȅ'];
    final byte[] block;
    final int[] fmap;
    final char[] sfmap;
    int origPtr;
    
    Data(int blockSize100k)
    {
      int n = blockSize100k * 100000;
      block = new byte[n + 1 + 20];
      fmap = new int[n];
      sfmap = new char[2 * n];
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.bzip2.CBZip2OutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */