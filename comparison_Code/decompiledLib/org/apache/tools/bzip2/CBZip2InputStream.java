package org.apache.tools.bzip2;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class CBZip2InputStream
  extends InputStream
  implements BZip2Constants
{
  private int last;
  private int origPtr;
  private int blockSize100k;
  private boolean blockRandomised;
  private int bsBuff;
  private int bsLive;
  private final CRC crc = new CRC();
  private int nInUse;
  private InputStream in;
  private final boolean decompressConcatenated;
  private int currentChar = -1;
  private static final int EOF = 0;
  private static final int START_BLOCK_STATE = 1;
  private static final int RAND_PART_A_STATE = 2;
  private static final int RAND_PART_B_STATE = 3;
  private static final int RAND_PART_C_STATE = 4;
  private static final int NO_RAND_PART_A_STATE = 5;
  private static final int NO_RAND_PART_B_STATE = 6;
  private static final int NO_RAND_PART_C_STATE = 7;
  private int currentState = 1;
  private int storedBlockCRC;
  private int storedCombinedCRC;
  private int computedBlockCRC;
  private int computedCombinedCRC;
  private int su_count;
  private int su_ch2;
  private int su_chPrev;
  private int su_i2;
  private int su_j2;
  private int su_rNToGo;
  private int su_rTPos;
  private int su_tPos;
  private char su_z;
  private Data data;
  
  public CBZip2InputStream(InputStream in)
    throws IOException
  {
    this(in, false);
  }
  
  public CBZip2InputStream(InputStream in, boolean decompressConcatenated)
    throws IOException
  {
    this.in = in;
    this.decompressConcatenated = decompressConcatenated;
    
    init(true);
    initBlock();
    setupBlock();
  }
  
  public int read()
    throws IOException
  {
    if (in != null) {
      return read0();
    }
    throw new IOException("stream closed");
  }
  
  public int read(byte[] dest, int offs, int len)
    throws IOException
  {
    if (offs < 0) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
    }
    if (len < 0) {
      throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
    }
    if (offs + len > dest.length) {
      throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
    }
    if (in == null) {
      throw new IOException("stream closed");
    }
    int hi = offs + len;
    int destOffs = offs;
    int b;
    while ((destOffs < hi) && ((b = read0()) >= 0)) {
      dest[(destOffs++)] = ((byte)b);
    }
    return destOffs == offs ? -1 : destOffs - offs;
  }
  
  private void makeMaps()
  {
    boolean[] inUse = data.inUse;
    byte[] seqToUnseq = data.seqToUnseq;
    
    int nInUseShadow = 0;
    for (int i = 0; i < 256; i++) {
      if (inUse[i] != 0) {
        seqToUnseq[(nInUseShadow++)] = ((byte)i);
      }
    }
    nInUse = nInUseShadow;
  }
  
  private int read0()
    throws IOException
  {
    int retChar = currentChar;
    switch (currentState)
    {
    case 0: 
      return -1;
    case 1: 
      throw new IllegalStateException();
    case 2: 
      throw new IllegalStateException();
    case 3: 
      setupRandPartB();
      break;
    case 4: 
      setupRandPartC();
      break;
    case 5: 
      throw new IllegalStateException();
    case 6: 
      setupNoRandPartB();
      break;
    case 7: 
      setupNoRandPartC();
      break;
    default: 
      throw new IllegalStateException();
    }
    return retChar;
  }
  
  private boolean init(boolean isFirstStream)
    throws IOException
  {
    if (null == in) {
      throw new IOException("No InputStream");
    }
    if (isFirstStream)
    {
      if (in.available() == 0) {
        throw new IOException("Empty InputStream");
      }
    }
    else
    {
      int magic0 = in.read();
      if (magic0 == -1) {
        return false;
      }
      int magic1 = in.read();
      if ((magic0 != 66) || (magic1 != 90)) {
        throw new IOException("Garbage after a valid BZip2 stream");
      }
    }
    int magic2 = in.read();
    if (magic2 != 104) {
      throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
    }
    int blockSize = in.read();
    if ((blockSize < 49) || (blockSize > 57)) {
      throw new IOException("Stream is not BZip2 formatted: illegal blocksize " + (char)blockSize);
    }
    blockSize100k = (blockSize - 48);
    
    bsLive = 0;
    computedCombinedCRC = 0;
    
    return true;
  }
  
  private void initBlock()
    throws IOException
  {
    char magic0;
    char magic1;
    char magic2;
    char magic3;
    char magic4;
    char magic5;
    do
    {
      magic0 = bsGetUByte();
      magic1 = bsGetUByte();
      magic2 = bsGetUByte();
      magic3 = bsGetUByte();
      magic4 = bsGetUByte();
      magic5 = bsGetUByte();
      if ((magic0 != '\027') || (magic1 != 'r') || (magic2 != 'E') || (magic3 != '8') || (magic4 != 'P') || (magic5 != '')) {
        break;
      }
    } while (!complete());
    return;
    if ((magic0 != '1') || (magic1 != 'A') || (magic2 != 'Y') || (magic3 != '&') || (magic4 != 'S') || (magic5 != 'Y'))
    {
      currentState = 0;
      throw new IOException("bad block header");
    }
    storedBlockCRC = bsGetInt();
    blockRandomised = (bsR(1) == 1);
    if (data == null) {
      data = new Data(blockSize100k);
    }
    getAndMoveToFrontDecode();
    
    crc.initialiseCRC();
    currentState = 1;
  }
  
  private void endBlock()
  {
    computedBlockCRC = crc.getFinalCRC();
    if (storedBlockCRC != computedBlockCRC)
    {
      computedCombinedCRC = (storedCombinedCRC << 1 | storedCombinedCRC >>> 31);
      
      computedCombinedCRC ^= storedBlockCRC;
      
      reportCRCError();
    }
    computedCombinedCRC = (computedCombinedCRC << 1 | computedCombinedCRC >>> 31);
    
    computedCombinedCRC ^= computedBlockCRC;
  }
  
  private boolean complete()
    throws IOException
  {
    storedCombinedCRC = bsGetInt();
    currentState = 0;
    data = null;
    if (storedCombinedCRC != computedCombinedCRC) {
      reportCRCError();
    }
    return (!decompressConcatenated) || (!init(false));
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 26	org/apache/tools/bzip2/CBZip2InputStream:in	Ljava/io/InputStream;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnull +40 -> 46
    //   9: aload_1
    //   10: getstatic 188	java/lang/System:in	Ljava/io/InputStream;
    //   13: if_acmpeq +7 -> 20
    //   16: aload_1
    //   17: invokevirtual 191	java/io/InputStream:close	()V
    //   20: aload_0
    //   21: aconst_null
    //   22: putfield 84	org/apache/tools/bzip2/CBZip2InputStream:data	Lorg/apache/tools/bzip2/CBZip2InputStream$Data;
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 26	org/apache/tools/bzip2/CBZip2InputStream:in	Ljava/io/InputStream;
    //   30: goto +16 -> 46
    //   33: astore_2
    //   34: aload_0
    //   35: aconst_null
    //   36: putfield 84	org/apache/tools/bzip2/CBZip2InputStream:data	Lorg/apache/tools/bzip2/CBZip2InputStream$Data;
    //   39: aload_0
    //   40: aconst_null
    //   41: putfield 26	org/apache/tools/bzip2/CBZip2InputStream:in	Ljava/io/InputStream;
    //   44: aload_2
    //   45: athrow
    //   46: return
    // Line number table:
    //   Java source line #401	-> byte code offset #0
    //   Java source line #402	-> byte code offset #5
    //   Java source line #404	-> byte code offset #9
    //   Java source line #405	-> byte code offset #16
    //   Java source line #408	-> byte code offset #20
    //   Java source line #409	-> byte code offset #25
    //   Java source line #410	-> byte code offset #30
    //   Java source line #408	-> byte code offset #33
    //   Java source line #409	-> byte code offset #39
    //   Java source line #410	-> byte code offset #44
    //   Java source line #412	-> byte code offset #46
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	CBZip2InputStream
    //   4	13	1	inShadow	InputStream
    //   33	12	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	20	33	finally
  }
  
  private int bsR(int n)
    throws IOException
  {
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    if (bsLiveShadow < n)
    {
      InputStream inShadow = in;
      do
      {
        int thech = inShadow.read();
        if (thech < 0) {
          throw new IOException("unexpected end of stream");
        }
        bsBuffShadow = bsBuffShadow << 8 | thech;
        bsLiveShadow += 8;
      } while (bsLiveShadow < n);
      bsBuff = bsBuffShadow;
    }
    bsLive = (bsLiveShadow - n);
    return bsBuffShadow >> bsLiveShadow - n & (1 << n) - 1;
  }
  
  private boolean bsGetBit()
    throws IOException
  {
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    if (bsLiveShadow < 1)
    {
      int thech = in.read();
      if (thech < 0) {
        throw new IOException("unexpected end of stream");
      }
      bsBuffShadow = bsBuffShadow << 8 | thech;
      bsLiveShadow += 8;
      bsBuff = bsBuffShadow;
    }
    bsLive = (bsLiveShadow - 1);
    return (bsBuffShadow >> bsLiveShadow - 1 & 0x1) != 0;
  }
  
  private char bsGetUByte()
    throws IOException
  {
    return (char)bsR(8);
  }
  
  private int bsGetInt()
    throws IOException
  {
    return ((bsR(8) << 8 | bsR(8)) << 8 | bsR(8)) << 8 | bsR(8);
  }
  
  private static void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize)
  {
    int i = minLen;
    for (int pp = 0; i <= maxLen; i++) {
      for (int j = 0; j < alphaSize; j++) {
        if (length[j] == i) {
          perm[(pp++)] = j;
        }
      }
    }
    int i = 23;
    for (;;)
    {
      i--;
      if (i <= 0) {
        break;
      }
      base[i] = 0;
      limit[i] = 0;
    }
    for (int i = 0; i < alphaSize; i++) {
      base[(length[i] + '\001')] += 1;
    }
    int i = 1;
    for (int b = base[0]; i < 23; i++)
    {
      b += base[i];
      base[i] = b;
    }
    int i = minLen;int vec = 0;
    for (int b = base[i]; i <= maxLen; i++)
    {
      int nb = base[(i + 1)];
      vec += nb - b;
      b = nb;
      limit[i] = (vec - 1);
      vec <<= 1;
    }
    for (int i = minLen + 1; i <= maxLen; i++) {
      base[i] = ((limit[(i - 1)] + 1 << 1) - base[i]);
    }
  }
  
  private void recvDecodingTables()
    throws IOException
  {
    Data dataShadow = data;
    boolean[] inUse = inUse;
    byte[] pos = recvDecodingTables_pos;
    byte[] selector = selector;
    byte[] selectorMtf = selectorMtf;
    
    int inUse16 = 0;
    for (int i = 0; i < 16; i++) {
      if (bsGetBit()) {
        inUse16 |= 1 << i;
      }
    }
    int i = 256;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      inUse[i] = false;
    }
    for (int i = 0; i < 16; i++) {
      if ((inUse16 & 1 << i) != 0)
      {
        int i16 = i << 4;
        for (int j = 0; j < 16; j++) {
          if (bsGetBit()) {
            inUse[(i16 + j)] = true;
          }
        }
      }
    }
    makeMaps();
    int alphaSize = nInUse + 2;
    
    int nGroups = bsR(3);
    int nSelectors = bsR(15);
    for (int i = 0; i < nSelectors; i++)
    {
      int j = 0;
      while (bsGetBit()) {
        j++;
      }
      selectorMtf[i] = ((byte)j);
    }
    int v = nGroups;
    for (;;)
    {
      v--;
      if (v < 0) {
        break;
      }
      pos[v] = ((byte)v);
    }
    for (int i = 0; i < nSelectors; i++)
    {
      int v = selectorMtf[i] & 0xFF;
      byte tmp = pos[v];
      while (v > 0)
      {
        pos[v] = pos[(v - 1)];
        v--;
      }
      pos[0] = tmp;
      selector[i] = tmp;
    }
    char[][] len = temp_charArray2d;
    for (int t = 0; t < nGroups; t++)
    {
      int curr = bsR(5);
      char[] len_t = len[t];
      for (int i = 0; i < alphaSize; i++)
      {
        while (bsGetBit()) {
          curr += (bsGetBit() ? -1 : 1);
        }
        len_t[i] = ((char)curr);
      }
    }
    createHuffmanDecodingTables(alphaSize, nGroups);
  }
  
  private void createHuffmanDecodingTables(int alphaSize, int nGroups)
  {
    Data dataShadow = data;
    char[][] len = temp_charArray2d;
    int[] minLens = minLens;
    int[][] limit = limit;
    int[][] base = base;
    int[][] perm = perm;
    for (int t = 0; t < nGroups; t++)
    {
      int minLen = 32;
      int maxLen = 0;
      char[] len_t = len[t];
      int i = alphaSize;
      for (;;)
      {
        i--;
        if (i < 0) {
          break;
        }
        char lent = len_t[i];
        if (lent > maxLen) {
          maxLen = lent;
        }
        if (lent < minLen) {
          minLen = lent;
        }
      }
      hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
      
      minLens[t] = minLen;
    }
  }
  
  private void getAndMoveToFrontDecode()
    throws IOException
  {
    origPtr = bsR(24);
    recvDecodingTables();
    
    InputStream inShadow = in;
    Data dataShadow = data;
    byte[] ll8 = ll8;
    int[] unzftab = unzftab;
    byte[] selector = selector;
    byte[] seqToUnseq = seqToUnseq;
    char[] yy = getAndMoveToFrontDecode_yy;
    int[] minLens = minLens;
    int[][] limit = limit;
    int[][] base = base;
    int[][] perm = perm;
    int limitLast = blockSize100k * 100000;
    
    int i = 256;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      yy[i] = ((char)i);
      unzftab[i] = 0;
    }
    int groupNo = 0;
    int groupPos = 49;
    int eob = nInUse + 1;
    int nextSym = getAndMoveToFrontDecode0(0);
    int bsBuffShadow = bsBuff;
    int bsLiveShadow = bsLive;
    int lastShadow = -1;
    int zt = selector[groupNo] & 0xFF;
    int[] base_zt = base[zt];
    int[] limit_zt = limit[zt];
    int[] perm_zt = perm[zt];
    int minLens_zt = minLens[zt];
    while (nextSym != eob) {
      if ((nextSym == 0) || (nextSym == 1))
      {
        int s = -1;
        for (int n = 1;; n <<= 1)
        {
          if (nextSym == 0)
          {
            s += n;
          }
          else
          {
            if (nextSym != 1) {
              break;
            }
            s += (n << 1);
          }
          if (groupPos == 0)
          {
            groupPos = 49;
            zt = selector[(++groupNo)] & 0xFF;
            base_zt = base[zt];
            limit_zt = limit[zt];
            perm_zt = perm[zt];
            minLens_zt = minLens[zt];
          }
          else
          {
            groupPos--;
          }
          int zn = minLens_zt;
          while (bsLiveShadow < zn)
          {
            int thech = inShadow.read();
            if (thech >= 0)
            {
              bsBuffShadow = bsBuffShadow << 8 | thech;
              bsLiveShadow += 8;
            }
            else
            {
              throw new IOException("unexpected end of stream");
            }
          }
          int zvec = bsBuffShadow >> bsLiveShadow - zn & (1 << zn) - 1;
          bsLiveShadow -= zn;
          while (zvec > limit_zt[zn])
          {
            zn++;
            while (bsLiveShadow < 1)
            {
              int thech = inShadow.read();
              if (thech >= 0)
              {
                bsBuffShadow = bsBuffShadow << 8 | thech;
                bsLiveShadow += 8;
              }
              else
              {
                throw new IOException("unexpected end of stream");
              }
            }
            bsLiveShadow--;
            zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
          }
          nextSym = perm_zt[(zvec - base_zt[zn])];
        }
        byte ch = seqToUnseq[yy[0]];
        unzftab[(ch & 0xFF)] += s + 1;
        while (s-- >= 0) {
          ll8[(++lastShadow)] = ch;
        }
        if (lastShadow >= limitLast) {
          throw new IOException("block overrun");
        }
      }
      else
      {
        lastShadow++;
        if (lastShadow >= limitLast) {
          throw new IOException("block overrun");
        }
        char tmp = yy[(nextSym - 1)];
        unzftab[(seqToUnseq[tmp] & 0xFF)] += 1;
        ll8[lastShadow] = seqToUnseq[tmp];
        int j;
        if (nextSym <= 16) {
          for (j = nextSym - 1; j > 0;) {
            yy[j] = yy[(--j)];
          }
        } else {
          System.arraycopy(yy, 0, yy, 1, nextSym - 1);
        }
        yy[0] = tmp;
        if (groupPos == 0)
        {
          groupPos = 49;
          zt = selector[(++groupNo)] & 0xFF;
          base_zt = base[zt];
          limit_zt = limit[zt];
          perm_zt = perm[zt];
          minLens_zt = minLens[zt];
        }
        else
        {
          groupPos--;
        }
        int zn = minLens_zt;
        while (bsLiveShadow < zn)
        {
          int thech = inShadow.read();
          if (thech >= 0)
          {
            bsBuffShadow = bsBuffShadow << 8 | thech;
            bsLiveShadow += 8;
          }
          else
          {
            throw new IOException("unexpected end of stream");
          }
        }
        int zvec = bsBuffShadow >> bsLiveShadow - zn & (1 << zn) - 1;
        bsLiveShadow -= zn;
        while (zvec > limit_zt[zn])
        {
          zn++;
          while (bsLiveShadow < 1)
          {
            int thech = inShadow.read();
            if (thech >= 0)
            {
              bsBuffShadow = bsBuffShadow << 8 | thech;
              bsLiveShadow += 8;
            }
            else
            {
              throw new IOException("unexpected end of stream");
            }
          }
          bsLiveShadow--;
          zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
        }
        nextSym = perm_zt[(zvec - base_zt[zn])];
      }
    }
    last = lastShadow;
    bsLive = bsLiveShadow;
    bsBuff = bsBuffShadow;
  }
  
  private int getAndMoveToFrontDecode0(int groupNo)
    throws IOException
  {
    InputStream inShadow = in;
    Data dataShadow = data;
    int zt = selector[groupNo] & 0xFF;
    int[] limit_zt = limit[zt];
    int zn = minLens[zt];
    int zvec = bsR(zn);
    int bsLiveShadow = bsLive;
    int bsBuffShadow = bsBuff;
    while (zvec > limit_zt[zn])
    {
      zn++;
      while (bsLiveShadow < 1)
      {
        int thech = inShadow.read();
        if (thech >= 0)
        {
          bsBuffShadow = bsBuffShadow << 8 | thech;
          bsLiveShadow += 8;
        }
        else
        {
          throw new IOException("unexpected end of stream");
        }
      }
      bsLiveShadow--;
      zvec = zvec << 1 | bsBuffShadow >> bsLiveShadow & 0x1;
    }
    bsLive = bsLiveShadow;
    bsBuff = bsBuffShadow;
    
    return perm[zt][(zvec - base[zt][zn])];
  }
  
  private void setupBlock()
    throws IOException
  {
    if (data == null) {
      return;
    }
    int[] cftab = data.cftab;
    int[] tt = data.initTT(last + 1);
    byte[] ll8 = data.ll8;
    cftab[0] = 0;
    System.arraycopy(data.unzftab, 0, cftab, 1, 256);
    
    int i = 1;
    for (int c = cftab[0]; i <= 256; i++)
    {
      c += cftab[i];
      cftab[i] = c;
    }
    int i = 0;
    for (int lastShadow = last; i <= lastShadow; i++)
    {
      byte tmp121_120 = (ll8[i] & 0xFF); int[] tmp121_112 = cftab; int tmp123_122 = tmp121_112[tmp121_120];tmp121_112[tmp121_120] = (tmp123_122 + 1);tt[tmp123_122] = i;
    }
    if ((origPtr < 0) || (origPtr >= tt.length)) {
      throw new IOException("stream corrupted");
    }
    su_tPos = tt[origPtr];
    su_count = 0;
    su_i2 = 0;
    su_ch2 = 256;
    if (blockRandomised)
    {
      su_rNToGo = 0;
      su_rTPos = 0;
      setupRandPartA();
    }
    else
    {
      setupNoRandPartA();
    }
  }
  
  private void setupRandPartA()
    throws IOException
  {
    if (su_i2 <= last)
    {
      su_chPrev = su_ch2;
      int su_ch2Shadow = data.ll8[su_tPos] & 0xFF;
      su_tPos = data.tt[su_tPos];
      if (su_rNToGo == 0)
      {
        su_rNToGo = (BZip2Constants.rNums[su_rTPos] - 1);
        if (++su_rTPos == 512) {
          su_rTPos = 0;
        }
      }
      else
      {
        su_rNToGo -= 1;
      }
      su_ch2 = (su_ch2Shadow ^= (su_rNToGo == 1 ? 1 : 0));
      su_i2 += 1;
      currentChar = su_ch2Shadow;
      currentState = 3;
      crc.updateCRC(su_ch2Shadow);
    }
    else
    {
      endBlock();
      initBlock();
      setupBlock();
    }
  }
  
  private void setupNoRandPartA()
    throws IOException
  {
    if (su_i2 <= last)
    {
      su_chPrev = su_ch2;
      int su_ch2Shadow = data.ll8[su_tPos] & 0xFF;
      su_ch2 = su_ch2Shadow;
      su_tPos = data.tt[su_tPos];
      su_i2 += 1;
      currentChar = su_ch2Shadow;
      currentState = 6;
      crc.updateCRC(su_ch2Shadow);
    }
    else
    {
      currentState = 5;
      endBlock();
      initBlock();
      setupBlock();
    }
  }
  
  private void setupRandPartB()
    throws IOException
  {
    if (su_ch2 != su_chPrev)
    {
      currentState = 2;
      su_count = 1;
      setupRandPartA();
    }
    else if (++su_count >= 4)
    {
      su_z = ((char)(data.ll8[su_tPos] & 0xFF));
      su_tPos = data.tt[su_tPos];
      if (su_rNToGo == 0)
      {
        su_rNToGo = (BZip2Constants.rNums[su_rTPos] - 1);
        if (++su_rTPos == 512) {
          su_rTPos = 0;
        }
      }
      else
      {
        su_rNToGo -= 1;
      }
      su_j2 = 0;
      currentState = 4;
      if (su_rNToGo == 1) {
        su_z = ((char)(su_z ^ 0x1));
      }
      setupRandPartC();
    }
    else
    {
      currentState = 2;
      setupRandPartA();
    }
  }
  
  private void setupRandPartC()
    throws IOException
  {
    if (su_j2 < su_z)
    {
      currentChar = su_ch2;
      crc.updateCRC(su_ch2);
      su_j2 += 1;
    }
    else
    {
      currentState = 2;
      su_i2 += 1;
      su_count = 0;
      setupRandPartA();
    }
  }
  
  private void setupNoRandPartB()
    throws IOException
  {
    if (su_ch2 != su_chPrev)
    {
      su_count = 1;
      setupNoRandPartA();
    }
    else if (++su_count >= 4)
    {
      su_z = ((char)(data.ll8[su_tPos] & 0xFF));
      su_tPos = data.tt[su_tPos];
      su_j2 = 0;
      setupNoRandPartC();
    }
    else
    {
      setupNoRandPartA();
    }
  }
  
  private void setupNoRandPartC()
    throws IOException
  {
    if (su_j2 < su_z)
    {
      int su_ch2Shadow = su_ch2;
      currentChar = su_ch2Shadow;
      crc.updateCRC(su_ch2Shadow);
      su_j2 += 1;
      currentState = 7;
    }
    else
    {
      su_i2 += 1;
      su_count = 0;
      setupNoRandPartA();
    }
  }
  
  private static final class Data
  {
    final boolean[] inUse = new boolean['Ā'];
    final byte[] seqToUnseq = new byte['Ā'];
    final byte[] selector = new byte['䙒'];
    final byte[] selectorMtf = new byte['䙒'];
    final int[] unzftab = new int['Ā'];
    final int[][] limit = new int[6]['Ă'];
    final int[][] base = new int[6]['Ă'];
    final int[][] perm = new int[6]['Ă'];
    final int[] minLens = new int[6];
    final int[] cftab = new int['ā'];
    final char[] getAndMoveToFrontDecode_yy = new char['Ā'];
    final char[][] temp_charArray2d = new char[6]['Ă'];
    final byte[] recvDecodingTables_pos = new byte[6];
    int[] tt;
    byte[] ll8;
    
    Data(int blockSize100k)
    {
      ll8 = new byte[blockSize100k * 100000];
    }
    
    final int[] initTT(int length)
    {
      int[] ttShadow = tt;
      if ((ttShadow == null) || (ttShadow.length < length)) {
        tt = (ttShadow = new int[length]);
      }
      return ttShadow;
    }
  }
  
  private static void reportCRCError()
  {
    System.err.println("BZip2 CRC error");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.bzip2.CBZip2InputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */