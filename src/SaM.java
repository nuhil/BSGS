import java.io.*;
import java.math.BigInteger;

public class SaM {

  public static BigInteger SquareAndMultiply(BigInteger base, BigInteger exponent) {
    if (exponent.compareTo(BigInteger.ZERO) < 0 || exponent.compareTo(BigInteger.ONE) == 0) {
      return base;
    }
    if (exponent.compareTo(BigInteger.ZERO) == 0) {
      return BigInteger.ONE;
    }

    BigInteger ret;

    // Convert exponent into a binary stream
    String bits = exponent.toString(2);
    
    // Step 1: Initial setting
    ret = base;

    // Step 2: Loop through the bits squaring for each one, and multiplying for each 1.
    for (int i = 1; i < bits.length(); ++i) {
      ret = ret.multiply(ret);
      if (bits.charAt(i) == '1') {
        ret = ret.multiply(base);
      }
    }

    return ret;
  }

  public static BigInteger SquareAndMultiply(BigInteger base, BigInteger exponent, BigInteger mod) {
    if (exponent.compareTo(BigInteger.ZERO) < 0 || exponent.compareTo(BigInteger.ONE) == 0) {
      return base;
    }
    if (exponent.compareTo(BigInteger.ZERO) == 0) {
      return BigInteger.ONE;
    }

    BigInteger ret;

    // Convert exponent into a binary stream
    String bits = exponent.toString(2);
    
    // Step 1: Initial setting
    ret = base.mod(mod);

    // Step 2: Loop through the bits squaring for each one, and multiplying for each 1.
    for (int i = 1; i < bits.length(); ++i) {
      ret = ret.multiply(ret).mod(mod);
      if (bits.charAt(i) == '1') {
        ret = ret.multiply(base).mod(mod);
      }
    }

    return ret;
  }

  private static void testResult(BigInteger testValue, BigInteger correct, String name) {
    if (testValue.compareTo(correct) != 0) {
      System.out.println("SquareAndMultiply test " + name + " failed.");
      System.out.println("  Correct: " + correct);
      System.out.println("  Output:  " + testValue);
    }
  }

  private static void test(BigInteger base, BigInteger exponent, BigInteger mod,
                           BigInteger correctA, BigInteger correctB, String name) {
    BigInteger testValue = SaM.SquareAndMultiply(base, exponent);
    testResult(testValue, correctA, name + "_a");
    testValue = SaM.SquareAndMultiply(base, exponent, mod);
    testResult(testValue, correctB, name + "_b");
  }

  public static void main(String[] args) {
    BigInteger base;
    BigInteger exponent;
    BigInteger mod;
    BigInteger correctA;
    BigInteger correctB;
    BigInteger testValue;

    // Standard tests
    base     = new BigInteger("4");
    exponent = new BigInteger("2");
    mod      = new BigInteger("100");
    correctA = base.pow(exponent.intValue());
    correctB = base.pow(exponent.intValue()).mod(mod);
    test(base, exponent, mod, correctA, correctB, "Standard");

    // Negative exponent
    base     = new BigInteger("4");
    exponent = new BigInteger("-1");
    mod      = new BigInteger("100");
    correctA = base;
    correctB = base;
    test(base, exponent, mod, correctA, correctB, "Negative");

    // Zero exponent
    base     = new BigInteger("4");
    exponent = BigInteger.ZERO;
    mod      = new BigInteger("100");
    correctA = BigInteger.ONE;
    correctB = BigInteger.ONE;
    test(base, exponent, mod, correctA, correctB, "Zero");

    // Zero exponent
    base     = new BigInteger("4");
    exponent = new BigInteger("1");
    mod      = new BigInteger("100");
    correctA = base;
    correctB = base;
    test(base, exponent, mod, correctA, correctB, "One");

    // Book example
    base     = new BigInteger("4");
    exponent = new BigInteger("26");
    mod      = new BigInteger("100");
    correctA = base.pow(exponent.intValue());
    correctB = base.pow(exponent.intValue()).mod(mod);
    test(base, exponent, mod, correctA, correctB, "Book");

    // Big exponenet
    base     = new BigInteger("4");
    exponent = new BigInteger("214748364"); // 2147483647 will cause an overflow. :(
    mod      = new BigInteger("100");
    correctA = base.pow(exponent.intValue());
    correctB = base.pow(exponent.intValue()).mod(mod);
    test(base, exponent, mod, correctA, correctB, "Big");
  }

}