import java.io.*;
import java.math.BigInteger;

public class SaM {

  public static BigInteger SquareAndMultiply(BigInteger base, int exponent) {
    if (exponent < 0) {
      return base;
    }

    BigInteger ret;

    // Convert exponent into a binary stream
    String bits = Integer.toBinaryString(exponent);
    
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

  public static BigInteger SquareAndMultiply(BigInteger base, int exponent, BigInteger mod) {
    if (exponent < 0) {
      return base;
    }

    BigInteger ret;

    // Convert exponent into a binary stream
    String bits = Integer.toBinaryString(exponent);
    
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

  public static void main(String[] args) {
    BigInteger base;
    int        exponent;
    BigInteger mod;
    BigInteger correctA;
    BigInteger correctB;
    BigInteger testValue;

    // Standard tests
    base     = new BigInteger("4");
    exponent = 2;
    mod      = new BigInteger("100");
    correctA = base.pow(exponent);
    correctB = base.pow(exponent).mod(mod);

    testValue = SaM.SquareAndMultiply(base, exponent);
    if (testValue.compareTo(correctA) != 0) {
      System.out.println("SquareAndMultiply test Standard_a failed.");
      System.out.println("  Correct: " + correctA);
      System.out.println("  Output:  " + testValue);
    }

    testValue = SaM.SquareAndMultiply(base, exponent, mod);
    if (testValue.compareTo(correctB) != 0) {
      System.out.println("SquareAndMultiply test Standard_b failed.");
      System.out.println("  Correct: " + correctB);
      System.out.println("  Output:  " + testValue);
    }

    // Negative exponent
    base     = new BigInteger("4");
    exponent = -1;
    mod      = new BigInteger("100");
    correctA = base;
    correctB = base;

    testValue = SaM.SquareAndMultiply(base, exponent);
    if (testValue.compareTo(correctA) != 0) {
      System.out.println("SquareAndMultiply test Negative_a failed.");
      System.out.println("  Correct: " + correctA);
      System.out.println("  Output:  " + testValue);
    }

    testValue = SaM.SquareAndMultiply(base, exponent, mod);
    if (testValue.compareTo(correctB) != 0) {
      System.out.println("SquareAndMultiply test Negative_b failed.");
      System.out.println("  Correct: " + correctB);
      System.out.println("  Output:  " + testValue);
    }

    // Book example
    base     = new BigInteger("4");
    exponent = 26;
    mod      = new BigInteger("100");
    correctA = base.pow(exponent);
    correctB = base.pow(exponent).mod(mod);

    testValue = SaM.SquareAndMultiply(base, exponent);
    if (testValue.compareTo(correctA) != 0) {
      System.out.println("SquareAndMultiply test 1a failed.");
      System.out.println("  Correct: " + correctA);
      System.out.println("  Output:  " + testValue);
    }

    testValue = SaM.SquareAndMultiply(base, exponent, mod);
    if (testValue.compareTo(correctB) != 0) {
      System.out.println("SquareAndMultiply test 1b failed.");
      System.out.println("  Correct: " + correctB);
      System.out.println("  Output:  " + testValue);
    }

    // Big exponenet
    base     = new BigInteger("4");
    exponent = 214748364; // 2147483647 will cause an overflow. :(
    mod      = new BigInteger("100");
    correctA = base.pow(exponent);
    correctB = base.pow(exponent).mod(mod);

    testValue = SaM.SquareAndMultiply(base, exponent);
    if (testValue.compareTo(correctA) != 0) {
      System.out.println("SquareAndMultiply test BigExp_a failed.");
      System.out.println("  Correct: " + correctA);
      System.out.println("  Output:  " + testValue);
    }

    testValue = SaM.SquareAndMultiply(base, exponent, mod);
    if (testValue.compareTo(correctB) != 0) {
      System.out.println("SquareAndMultiply test BigExp_b failed.");
      System.out.println("  Correct: " + correctB);
      System.out.println("  Output:  " + testValue);
    }
  }

}