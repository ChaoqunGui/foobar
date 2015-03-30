package com.dr.license.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vnimani
 * @since 3.1
 */
public final class LCRandom {

  private static final Logger logger = LoggerFactory.getLogger(LCRandom.class);
  private static final Random s_random = new Random();
  // private static final SecureRandom rng = new SecureRandom();

  // IMPORTANT: do NOT change the FINAL values below in ANY way
  private static final int[] CHSM_MASK = { 3, 5, 1, 7, 11 };
  private static final String EASY_CHARS = "YE9CVDFJAXHK4LMW8BNR3TU7";
  // private static final String MEDIUM_CHARS = EASY_CHARS + "Z6G2PQ5";
  // private static final String HARD_CHARS = MEDIUM_CHARS + "_#!@&^$";
  private static final String NUMBER_CHARS = "0123456789";

  // ==========================================================================
  public static String genDmCode(Long guid, int length) {
    String e = encode(guid, EASY_CHARS);
    String chkSum = getChecksum(e, EASY_CHARS);
    String filler = genRandomString(EASY_CHARS,
        length - e.length() - chkSum.length(), 0);

    // logger.debug("[guid : {}] [chkSum : {}] [e : {}] [filler : {}] ",guid,chkSum,e,filler);

    return chkSum + e + filler;
  }

  // ==========================================================================
  public static String getChecksum(String s, String map) {
    long chksm = 0;
    for (int i = 0; i < s.length(); i++)
      chksm += (s.charAt(i) - '0') * CHSM_MASK[i % CHSM_MASK.length];

    int mapLength = map.length();

    return encode((mapLength - (chksm % mapLength)) % mapLength, map);
  }

  // ==========================================================================
  public static String genRandomString(int lengthMin, int lengthMax) {
    return genRandomString(EASY_CHARS, lengthMin, lengthMax);
  }

  public static String genRandomNumberString(int lengthMin, int lengthMax) {
    return genRandomString(NUMBER_CHARS, lengthMin, lengthMax);
  }

  // ==========================================================================
  private static String genRandomString(String cSet, int lengthMin,
      int lengthMax) {
    int cSetLenth = cSet.length();
    int resLength = (lengthMax <= lengthMin) ? lengthMin : lengthMin
        + s_random.nextInt(lengthMax - lengthMin);

    if (resLength < 1)
      throw new IllegalArgumentException(
          "Invalid lengthMin,lengthMax combination!");

    char[] strBuffer = new char[resLength];

    for (int i = 0; i < resLength; i++)
      strBuffer[i] = cSet.charAt(s_random.nextInt(cSetLenth));

    return new String(strBuffer);
  }

  // ==========================================================================
  public static String genRandomString2(int lengthMin, int lengthMax) {
    int length = (lengthMax <= lengthMin) ? lengthMin : lengthMin
        + s_random.nextInt(lengthMax - lengthMin);

    char[] strBuffer = new char[length];

    int i, r;
    char ch;
    double p;

    for (i = 0; i < length; ++i) {
      p = s_random.nextDouble();
      // if (p < 0.03) -- there are cases where we can only use alphanumeric, so
      // disable _
      // ch = '_';
      // else
      if (p < 0.333333) {
        r = s_random.nextInt(26);
        ch = (char) ('A' + r);
      } else if (p < 0.666666) {
        r = s_random.nextInt(26);
        ch = (char) ('a' + r);
      } else {
        r = s_random.nextInt(10);
        ch = (char) ('0' + r);
      }
      strBuffer[i] = ch;
    }
    return new String(strBuffer);
  }

  // ==========================================================================
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    // for(int i = 0; i < 1000000; i++)
    // logger.info(":[genRandomString : {}] ",genRandomString(5, 40));
    //
    long time = System.currentTimeMillis();

    /*
     * for(int i = 0; i < 100; i++){ String s = genRandomString(NUMBER_CHARS, 6,
     * 6); logger.info(" :   :[s : {}] [s.length : {}] ",s,s.length()); }
     */
    /*
     * genDmCode(85524403L, 12);
     * 
     * for(int i = 0; i < 100; i++) { long l =
     * (long)(s_random.nextDouble()*100000000); String s = genDmCode(l, 12);
     * if(s.length() != 12) throw new Exception("..."+s);
     * logger.info(" :   :[s : {}] [s.length : {}] ",s,s.length()); }
     */
    logger.info(" Elapsed: :[ (System.currentTimeMillis : {}] ",
        (System.currentTimeMillis() - time));

  }

  // ==========================================================================
  public static String encode(long lng, String map) {
    int radix;
    if (map == null || (radix = map.length()) < 2)
      throw new IllegalArgumentException(
          "Map has to be contain at least two characters");

    char[] buf = new char[65];
    int charPos = 64;
    long val = lng < 0 ? -lng : lng;

    while (val >= radix) {
      buf[charPos--] = map.charAt((int) (val % radix));
      val = val / radix;
    }
    buf[charPos] = map.charAt((int) val);

    if (val < 0)
      buf[--charPos] = '-';

    return new String(buf, charPos, (65 - charPos));
  }
}
