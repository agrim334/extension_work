package org.apache.tools.bzip2;

import java.util.BitSet;

class BlockSort
{
  private static final int QSORT_STACK_SIZE = 1000;
  private static final int FALLBACK_QSORT_STACK_SIZE = 100;
  private static final int STACK_SIZE = 1000;
  private int workDone;
  private int workLimit;
  private boolean firstAttempt;
  private final int[] stack_ll = new int['Ϩ'];
  private final int[] stack_hh = new int['Ϩ'];
  private final int[] stack_dd = new int['Ϩ'];
  private final int[] mainSort_runningOrder = new int['Ā'];
  private final int[] mainSort_copy = new int['Ā'];
  private final boolean[] mainSort_bigDone = new boolean['Ā'];
  private final int[] ftab = new int[65537];
  private final char[] quadrant;
  private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
  private int[] eclass;
  
  BlockSort(CBZip2OutputStream.Data data)
  {
    quadrant = sfmap;
  }
  
  void blockSort(CBZip2OutputStream.Data data, int last)
  {
    workLimit = (30 * last);
    workDone = 0;
    firstAttempt = true;
    if (last + 1 < 10000)
    {
      fallbackSort(data, last);
    }
    else
    {
      mainSort(data, last);
      if ((firstAttempt) && (workDone > workLimit)) {
        fallbackSort(data, last);
      }
    }
    int[] fmap = fmap;
    origPtr = -1;
    for (int i = 0; i <= last; i++) {
      if (fmap[i] == 0)
      {
        origPtr = i;
        break;
      }
    }
  }
  
  final void fallbackSort(CBZip2OutputStream.Data data, int last)
  {
    block[0] = block[(last + 1)];
    fallbackSort(fmap, block, last + 1);
    for (int i = 0; i < last + 1; i++) {
      fmap[i] -= 1;
    }
    for (int i = 0; i < last + 1; i++) {
      if (fmap[i] == -1)
      {
        fmap[i] = last;
        break;
      }
    }
  }
  
  private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo, int hi)
  {
    if (lo == hi) {
      return;
    }
    if (hi - lo > 3) {
      for (int i = hi - 4; i >= lo; i--)
      {
        int tmp = fmap[i];
        int ec_tmp = eclass[tmp];
        for (int j = i + 4; (j <= hi) && (ec_tmp > eclass[fmap[j]]); j += 4) {
          fmap[(j - 4)] = fmap[j];
        }
        fmap[(j - 4)] = tmp;
      }
    }
    for (int i = hi - 1; i >= lo; i--)
    {
      int tmp = fmap[i];
      int ec_tmp = eclass[tmp];
      for (int j = i + 1; (j <= hi) && (ec_tmp > eclass[fmap[j]]); j++) {
        fmap[(j - 1)] = fmap[j];
      }
      fmap[(j - 1)] = tmp;
    }
  }
  
  private void fswap(int[] fmap, int zz1, int zz2)
  {
    int zztmp = fmap[zz1];
    fmap[zz1] = fmap[zz2];
    fmap[zz2] = zztmp;
  }
  
  private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn)
  {
    while (yyn > 0)
    {
      fswap(fmap, yyp1, yyp2);
      yyp1++;
      yyp2++;
      yyn--;
    }
  }
  
  private int fmin(int a, int b)
  {
    return a < b ? a : b;
  }
  
  private void fpush(int sp, int lz, int hz)
  {
    stack_ll[sp] = lz;
    stack_hh[sp] = hz;
  }
  
  private int[] fpop(int sp)
  {
    return new int[] { stack_ll[sp], stack_hh[sp] };
  }
  
  private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt)
  {
    long r = 0L;
    int sp = 0;
    fpush(sp++, loSt, hiSt);
    while (sp > 0)
    {
      int[] s = fpop(--sp);
      int lo = s[0];
      int hi = s[1];
      if (hi - lo < 10)
      {
        fallbackSimpleSort(fmap, eclass, lo, hi);
      }
      else
      {
        r = (r * 7621L + 1L) % 32768L;
        long r3 = r % 3L;
        long med;
        long med;
        if (r3 == 0L)
        {
          med = eclass[fmap[lo]];
        }
        else
        {
          long med;
          if (r3 == 1L) {
            med = eclass[fmap[(lo + hi >>> 1)]];
          } else {
            med = eclass[fmap[hi]];
          }
        }
        int ltLo;
        int unLo = ltLo = lo;
        int gtHi;
        int unHi = gtHi = hi;
        for (;;)
        {
          if (unLo <= unHi)
          {
            int n = eclass[fmap[unLo]] - (int)med;
            if (n == 0)
            {
              fswap(fmap, unLo, ltLo);
              ltLo++;
              unLo++;
              continue;
            }
            if (n <= 0)
            {
              unLo++; continue;
            }
          }
          while (unLo <= unHi)
          {
            int n = eclass[fmap[unHi]] - (int)med;
            if (n == 0)
            {
              fswap(fmap, unHi, gtHi);
              gtHi--;
              unHi--;
            }
            else
            {
              if (n < 0) {
                break;
              }
              unHi--;
            }
          }
          if (unLo > unHi) {
            break;
          }
          fswap(fmap, unLo, unHi);
          unLo++;
          unHi--;
        }
        if (gtHi >= ltLo)
        {
          int n = fmin(ltLo - lo, unLo - ltLo);
          fvswap(fmap, lo, unLo - n, n);
          int m = fmin(hi - gtHi, gtHi - unHi);
          fvswap(fmap, unHi + 1, hi - m + 1, m);
          
          n = lo + unLo - ltLo - 1;
          m = hi - (gtHi - unHi) + 1;
          if (n - lo > hi - m)
          {
            fpush(sp++, lo, n);
            fpush(sp++, m, hi);
          }
          else
          {
            fpush(sp++, m, hi);
            fpush(sp++, lo, n);
          }
        }
      }
    }
  }
  
  private int[] getEclass()
  {
    if (eclass == null) {
      eclass = new int[quadrant.length / 2];
    }
    return eclass;
  }
  
  final void fallbackSort(int[] fmap, byte[] block, int nblock)
  {
    int[] ftab = new int['ā'];
    
    int[] eclass = getEclass();
    for (int i = 0; i < nblock; i++) {
      eclass[i] = 0;
    }
    for (i = 0; i < nblock; i++) {
      ftab[(block[i] & 0xFF)] += 1;
    }
    for (i = 1; i < 257; i++) {
      ftab[i] += ftab[(i - 1)];
    }
    for (i = 0; i < nblock; i++)
    {
      int j = block[i] & 0xFF;
      int k = ftab[j] - 1;
      ftab[j] = k;
      fmap[k] = i;
    }
    int nBhtab = 64 + nblock;
    BitSet bhtab = new BitSet(nBhtab);
    for (i = 0; i < 256; i++) {
      bhtab.set(ftab[i]);
    }
    for (i = 0; i < 32; i++)
    {
      bhtab.set(nblock + 2 * i);
      bhtab.clear(nblock + 2 * i + 1);
    }
    int H = 1;
    int nNotDone;
    do
    {
      int j = 0;
      for (i = 0; i < nblock; i++)
      {
        if (bhtab.get(i)) {
          j = i;
        }
        int k = fmap[i] - H;
        if (k < 0) {
          k += nblock;
        }
        eclass[k] = j;
      }
      nNotDone = 0;
      int r = -1;
      for (;;)
      {
        int k = r + 1;
        k = bhtab.nextClearBit(k);
        int l = k - 1;
        if (l >= nblock) {
          break;
        }
        k = bhtab.nextSetBit(k + 1);
        r = k - 1;
        if (r >= nblock) {
          break;
        }
        if (r > l)
        {
          nNotDone += r - l + 1;
          fallbackQSort3(fmap, eclass, l, r);
          
          int cc = -1;
          for (i = l; i <= r; i++)
          {
            int cc1 = eclass[fmap[i]];
            if (cc != cc1)
            {
              bhtab.set(i);
              cc = cc1;
            }
          }
        }
      }
      H *= 2;
    } while ((H <= nblock) && (nNotDone != 0));
  }
  
  private static final int[] INCS = { 1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484 };
  private static final int SMALL_THRESH = 20;
  private static final int DEPTH_THRESH = 10;
  private static final int WORK_FACTOR = 30;
  private static final int SETMASK = 2097152;
  private static final int CLEARMASK = -2097153;
  
  private boolean mainSimpleSort(CBZip2OutputStream.Data dataShadow, int lo, int hi, int d, int lastShadow)
  {
    int bigN = hi - lo + 1;
    if (bigN < 2) {
      return (firstAttempt) && (workDone > workLimit);
    }
    int hp = 0;
    while (INCS[hp] < bigN) {
      hp++;
    }
    int[] fmap = fmap;
    char[] quadrant = this.quadrant;
    byte[] block = block;
    int lastPlus1 = lastShadow + 1;
    boolean firstAttemptShadow = firstAttempt;
    int workLimitShadow = workLimit;
    int workDoneShadow = workDone;
    int h;
    int mj;
    int i;
    label536:
    label564:
    label584:
    label612:
    label632:
    label660:
    label676:
    label871:
    for (;;)
    {
      hp--;
      if (hp < 0) {
        break;
      }
      h = INCS[hp];
      mj = lo + h - 1;
      for (i = lo + h; i <= hi;)
      {
        for (int k = 3; i <= hi; i++)
        {
          k--;
          if (k < 0) {
            break;
          }
          int v = fmap[i];
          int vd = v + d;
          int j = i;
          
          boolean onceRun = false;
          int a = 0;
          for (;;)
          {
            if (onceRun)
            {
              fmap[j] = a;
              if (j -= h <= mj) {
                break;
              }
            }
            else
            {
              onceRun = true;
            }
            a = fmap[(j - h)];
            int i1 = a + d;
            int i2 = vd;
            if (block[(i1 + 1)] == block[(i2 + 1)])
            {
              if (block[(i1 + 2)] == block[(i2 + 2)])
              {
                if (block[(i1 + 3)] == block[(i2 + 3)])
                {
                  if (block[(i1 + 4)] == block[(i2 + 4)])
                  {
                    if (block[(i1 + 5)] == block[(i2 + 5)])
                    {
                      i1 += 6;i2 += 6;
                      if (block[i1] == block[i2])
                      {
                        int x = lastShadow;
                        for (;;)
                        {
                          if (x <= 0) {
                            break label871;
                          }
                          x -= 4;
                          if (block[(i1 + 1)] != block[(i2 + 1)]) {
                            break label676;
                          }
                          if (quadrant[i1] != quadrant[i2]) {
                            break label660;
                          }
                          if (block[(i1 + 2)] != block[(i2 + 2)]) {
                            break label632;
                          }
                          if (quadrant[(i1 + 1)] != quadrant[(i2 + 1)]) {
                            break label612;
                          }
                          if (block[(i1 + 3)] != block[(i2 + 3)]) {
                            break label584;
                          }
                          if (quadrant[(i1 + 2)] != quadrant[(i2 + 2)]) {
                            break label564;
                          }
                          if (block[(i1 + 4)] != block[(i2 + 4)]) {
                            break label536;
                          }
                          if (quadrant[(i1 + 3)] != quadrant[(i2 + 3)]) {
                            break;
                          }
                          i1 += 4;
                          if (i1 >= lastPlus1) {
                            i1 -= lastPlus1;
                          }
                          i2 += 4;
                          if (i2 >= lastPlus1) {
                            i2 -= lastPlus1;
                          }
                          workDoneShadow++;
                        }
                        if (quadrant[(i1 + 3)] <= quadrant[(i2 + 3)]) {
                          break;
                        }
                        continue;
                        if ((block[(i1 + 4)] & 0xFF) <= (block[(i2 + 4)] & 0xFF)) {
                          break;
                        }
                        continue;
                        if (quadrant[(i1 + 2)] <= quadrant[(i2 + 2)]) {
                          break;
                        }
                        continue;
                        if ((block[(i1 + 3)] & 0xFF) <= (block[(i2 + 3)] & 0xFF)) {
                          break;
                        }
                        continue;
                        if (quadrant[(i1 + 1)] <= quadrant[(i2 + 1)]) {
                          break;
                        }
                        continue;
                        if ((block[(i1 + 2)] & 0xFF) <= (block[(i2 + 2)] & 0xFF)) {
                          break;
                        }
                        continue;
                        if (quadrant[i1] <= quadrant[i2]) {
                          break;
                        }
                        continue;
                        if ((block[(i1 + 1)] & 0xFF) <= (block[(i2 + 1)] & 0xFF)) {
                          break;
                        }
                      }
                      else if ((block[i1] & 0xFF) <= (block[i2] & 0xFF))
                      {
                        break;
                      }
                    }
                    else if ((block[(i1 + 5)] & 0xFF) <= (block[(i2 + 5)] & 0xFF))
                    {
                      break;
                    }
                  }
                  else if ((block[(i1 + 4)] & 0xFF) <= (block[(i2 + 4)] & 0xFF)) {
                    break;
                  }
                }
                else if ((block[(i1 + 3)] & 0xFF) <= (block[(i2 + 3)] & 0xFF)) {
                  break;
                }
              }
              else if ((block[(i1 + 2)] & 0xFF) <= (block[(i2 + 2)] & 0xFF)) {
                break;
              }
            }
            else {
              if ((block[(i1 + 1)] & 0xFF) <= (block[(i2 + 1)] & 0xFF)) {
                break;
              }
            }
          }
          fmap[j] = v;
        }
        if ((firstAttemptShadow) && (i <= hi) && (workDoneShadow > workLimitShadow)) {
          break label908;
        }
      }
    }
    label908:
    workDone = workDoneShadow;
    return (firstAttemptShadow) && (workDoneShadow > workLimitShadow);
  }
  
  private static void vswap(int[] fmap, int p1, int p2, int n)
  {
    n += p1;
    while (p1 < n)
    {
      int t = fmap[p1];
      fmap[(p1++)] = fmap[p2];
      fmap[(p2++)] = t;
    }
  }
  
  private static byte med3(byte a, byte b, byte c)
  {
    return a > c ? c : b > c ? b : a < b ? a : a < c ? c : b < c ? b : 
      a;
  }
  
  private void mainQSort3(CBZip2OutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last)
  {
    int[] stack_ll = this.stack_ll;
    int[] stack_hh = this.stack_hh;
    int[] stack_dd = this.stack_dd;
    int[] fmap = fmap;
    byte[] block = block;
    
    stack_ll[0] = loSt;
    stack_hh[0] = hiSt;
    stack_dd[0] = dSt;
    
    int sp = 1;
    for (;;)
    {
      sp--;
      if (sp < 0) {
        break;
      }
      int lo = stack_ll[sp];
      int hi = stack_hh[sp];
      int d = stack_dd[sp];
      if ((hi - lo < 20) || (d > 10))
      {
        if (!mainSimpleSort(dataShadow, lo, hi, d, last)) {}
      }
      else
      {
        int d1 = d + 1;
        int med = med3(block[(fmap[lo] + d1)], block[(fmap[hi] + d1)], block[(fmap[(lo + hi >>> 1)] + d1)]) & 0xFF;
        
        int unLo = lo;
        int unHi = hi;
        int ltLo = lo;
        int gtHi = hi;
        for (;;)
        {
          if (unLo <= unHi)
          {
            int n = (block[(fmap[unLo] + d1)] & 0xFF) - med;
            if (n == 0)
            {
              int temp = fmap[unLo];
              fmap[(unLo++)] = fmap[ltLo];
              fmap[(ltLo++)] = temp;
            }
            else
            {
              if (n >= 0) {
                break label257;
              }
              unLo++;
            }
            continue;
          }
          label257:
          while (unLo <= unHi)
          {
            int n = (block[(fmap[unHi] + d1)] & 0xFF) - med;
            if (n == 0)
            {
              int temp = fmap[unHi];
              fmap[(unHi--)] = fmap[gtHi];
              fmap[(gtHi--)] = temp;
            }
            else
            {
              if (n <= 0) {
                break;
              }
              unHi--;
            }
          }
          if (unLo > unHi) {
            break;
          }
          int temp = fmap[unLo];
          fmap[(unLo++)] = fmap[unHi];
          fmap[(unHi--)] = temp;
        }
        if (gtHi < ltLo)
        {
          stack_ll[sp] = lo;
          stack_hh[sp] = hi;
          stack_dd[sp] = d1;
          sp++;
        }
        else
        {
          int n = ltLo - lo < unLo - ltLo ? ltLo - lo : unLo - ltLo;
          vswap(fmap, lo, unLo - n, n);
          
          int m = hi - gtHi < gtHi - unHi ? hi - gtHi : gtHi - unHi;
          vswap(fmap, unLo, hi - m + 1, m);
          
          n = lo + unLo - ltLo - 1;
          m = hi - (gtHi - unHi) + 1;
          
          stack_ll[sp] = lo;
          stack_hh[sp] = n;
          stack_dd[sp] = d;
          sp++;
          
          stack_ll[sp] = (n + 1);
          stack_hh[sp] = (m - 1);
          stack_dd[sp] = d1;
          sp++;
          
          stack_ll[sp] = m;
          stack_hh[sp] = hi;
          stack_dd[sp] = d;
          sp++;
        }
      }
    }
  }
  
  final void mainSort(CBZip2OutputStream.Data dataShadow, int lastShadow)
  {
    int[] runningOrder = mainSort_runningOrder;
    int[] copy = mainSort_copy;
    boolean[] bigDone = mainSort_bigDone;
    int[] ftab = this.ftab;
    byte[] block = block;
    int[] fmap = fmap;
    char[] quadrant = this.quadrant;
    int workLimitShadow = workLimit;
    boolean firstAttemptShadow = firstAttempt;
    
    int i = 65537;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      ftab[i] = 0;
    }
    for (int i = 0; i < 20; i++) {
      block[(lastShadow + i + 2)] = block[(i % (lastShadow + 1) + 1)];
    }
    int i = lastShadow + 20 + 1;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      quadrant[i] = '\000';
    }
    block[0] = block[(lastShadow + 1)];
    
    int c1 = block[0] & 0xFF;
    for (int i = 0; i <= lastShadow; i++)
    {
      int c2 = block[(i + 1)] & 0xFF;
      ftab[((c1 << 8) + c2)] += 1;
      c1 = c2;
    }
    for (int i = 1; i <= 65536; i++) {
      ftab[i] += ftab[(i - 1)];
    }
    c1 = block[1] & 0xFF;
    for (int i = 0; i < lastShadow; i++)
    {
      int c2 = block[(i + 2)] & 0xFF;
      fmap[(ftab[((c1 << 8) + c2)] -= 1)] = i;
      c1 = c2;
    }
    fmap[(ftab[(((block[(lastShadow + 1)] & 0xFF) << 8) + (block[1] & 0xFF))] -= 1)] = lastShadow;
    
    int i = 256;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      bigDone[i] = false;
      runningOrder[i] = i;
    }
    for (int h = 364; h != 1;)
    {
      h /= 3;
      for (int i = h; i <= 255; i++)
      {
        int vv = runningOrder[i];
        int a = ftab[(vv + 1 << 8)] - ftab[(vv << 8)];
        int b = h - 1;
        int j = i;
        for (int ro = runningOrder[(j - h)]; ftab[(ro + 1 << 8)] - ftab[(ro << 8)] > a; ro = runningOrder[(j - h)])
        {
          runningOrder[j] = ro;
          j -= h;
          if (j <= b) {
            break;
          }
        }
        runningOrder[j] = vv;
      }
    }
    for (int i = 0; i <= 255; i++)
    {
      int ss = runningOrder[i];
      for (int j = 0; j <= 255; j++)
      {
        int sb = (ss << 8) + j;
        int ftab_sb = ftab[sb];
        if ((ftab_sb & 0x200000) != 2097152)
        {
          int lo = ftab_sb & 0xFFDFFFFF;
          int hi = (ftab[(sb + 1)] & 0xFFDFFFFF) - 1;
          if (hi > lo)
          {
            mainQSort3(dataShadow, lo, hi, 2, lastShadow);
            if ((firstAttemptShadow) && (workDone > workLimitShadow)) {
              return;
            }
          }
          ftab[sb] = (ftab_sb | 0x200000);
        }
      }
      for (int j = 0; j <= 255; j++) {
        copy[j] = (ftab[((j << 8) + ss)] & 0xFFDFFFFF);
      }
      int j = ftab[(ss << 8)] & 0xFFDFFFFF;
      for (int hj = ftab[(ss + 1 << 8)] & 0xFFDFFFFF; j < hj; j++)
      {
        int fmap_j = fmap[j];
        c1 = block[fmap_j] & 0xFF;
        if (bigDone[c1] == 0)
        {
          fmap[copy[c1]] = (fmap_j == 0 ? lastShadow : fmap_j - 1);
          copy[c1] += 1;
        }
      }
      int j = 256;
      for (;;)
      {
        j--;
        if (j < 0) {
          break;
        }
        ftab[((j << 8) + ss)] |= 0x200000;
      }
      bigDone[ss] = true;
      if (i < 255)
      {
        int bbStart = ftab[(ss << 8)] & 0xFFDFFFFF;
        int bbSize = (ftab[(ss + 1 << 8)] & 0xFFDFFFFF) - bbStart;
        int shifts = 0;
        while (bbSize >> shifts > 65534) {
          shifts++;
        }
        for (int j = 0; j < bbSize; j++)
        {
          int a2update = fmap[(bbStart + j)];
          char qVal = (char)(j >> shifts);
          quadrant[a2update] = qVal;
          if (a2update < 20) {
            quadrant[(a2update + lastShadow + 1)] = qVal;
          }
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.bzip2.BlockSort
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */