import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RSA {

  private BigInteger p, q;
  private BigInteger n, phi_n;

  // The most recently decrypted message
  private BigInteger message;

  private Key publicKey, privateKey;

  public RSA(BigInteger p, BigInteger q) {
    this.p = p;
    this.q = q;

    // n = p * q
    n = p.multiply(q);

    // phi(n) = (p - 1)(q - 1)
    phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    // Choose random e where gcd(e, Phi(n)) = 1
    BigInteger e;
    BigInteger two = new BigInteger("2");
    BigInteger three = new BigInteger("3");
    do {
        e = new BigInteger(phi_n.bitLength(), new SecureRandom());
        e = e.mod(phi_n.subtract(three)).add(two);
    } while (!e.gcd(phi_n).equals(BigInteger.ONE));

    // Set up publicKey
    publicKey = new Key(n, e);

    // Set up the private key
    BigInteger d = e.modInverse(phi_n);
    privateKey = new Key(n, d);
  }

  private RSA(BigInteger p, BigInteger q, BigInteger e) {
    this.p = p;
    this.q = q;

    // n = p * q
    n = p.multiply(q);

    // phi(n) = (p - 1)(q - 1)
    phi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

    // Set up publicKey
    publicKey = new Key(n, e);

    // Set up the private key
    BigInteger d = e.modInverse(phi_n);
    privateKey = new Key(n, d);
  }

  public Key getPublicKey() {
    return publicKey;
  }

  public void sendMessage(BigInteger y) {
    message = privateKey.decrypt(y);
  }

  class Key {
    
    private BigInteger n, exp;

    public Key(BigInteger n, BigInteger exp) {
      this.n = n;
      this.exp = exp;
    }

    public BigInteger encrypt(BigInteger x) {
      return SaM.SquareAndMultiply(x, exp, n);
    }

    public BigInteger decrypt(BigInteger y) {
      return SaM.SquareAndMultiply(y, exp, n);
    }
  }

  private static void test(RSA person, BigInteger message, String testName) {
    if (person.message.compareTo(message) != 0) {
      System.out.println("Failed test " + testName);
      System.out.println("  Message: " + message);
      System.out.println("  Value:   " + person.message);
    }
  }

  private static void test(RSA person, BigInteger n, BigInteger e, BigInteger d, String testName) {
    if (!person.privateKey.n.equals(n)) {
      System.out.println("Failed n test on " + testName);
      System.out.println("  Correct n: " + n);
      System.out.println("  Wrong n:   " + person.privateKey.n);
    }
    if (!person.publicKey.exp.equals(e)) {
      System.out.println("Failed e test on " + testName);
      System.out.println("  Correct e: " + e);
      System.out.println("  Wrong e:   " + person.publicKey.exp);
    }
    if (!person.privateKey.exp.equals(d)) {
      System.out.println("Failed d test on " + testName);
      System.out.println("  Correct d: " + d);
      System.out.println("  Wrong d:   " + person.privateKey.exp);
    }
  }

  // Test the RSA implementation (silence is good)
  public static void main(String[] args) {
    RSA Bob;
    RSA.Key pubKey;
    BigInteger x;
    BigInteger y;
    
    /// Base test
    Bob = new RSA(new BigInteger("3"), new BigInteger("11"));

    // Get Bob's public key, encrypt and send the message
    pubKey = Bob.getPublicKey();
    x = new BigInteger("4");
    y = pubKey.encrypt(x);
    Bob.sendMessage(y);

    // Test that Bob got the message
    test(Bob, x, "Base");

    /// Book test
    BigInteger p = new BigInteger("E0DFD2C2A288ACEBC705EFAB30E4447541A8C5A47A37185C5A9CB98389CE4DE19199AA3069B404FD98C801568CB9170EB712BF10B4955CE9C9DC8CE6855C6123", 16);
    BigInteger q = new BigInteger("EBE0FCF21866FD9A9F0D72F7994875A8D92E67AEE4B515136B2A778A8048B149828AEA30BD0BA34B977982A3D42168F594CA99F3981DDABFAB2369F229640115", 16);
    BigInteger e = new BigInteger("40B028E1E4CCF07537643101FF72444A0BE1D7682F1EDB553E3AB4F6DD8293CA1945DB12D796AE9244D60565C2EB692A89B8881D58D278562ED60066DD8211E67315CF89857167206120405B08B54D10D4EC4ED4253C75FA74098FE3F7FB751FF5121353C554391E114C85B56A9725E9BD5685D6C9C7EED8EE442366353DC39", 16);
    Bob = new RSA(p, q, e);

    // Test n, e, d
    BigInteger n = new BigInteger("CF33188211FDF6052BDBB1A37235E0ABB5978A45C71FD381A91AD12FC76DA0544C47568AC83D855D47CA8D8A779579AB72E635D0B0AAAC22D28341E998E90F82122A2C06090F43A37E0203C2B72E401FD06890EC8EAD4F07E686E906F01B2468AE7B30CBD670255C1FEDE1A2762CF4392C0759499CC0ABECFF008728D9A11ADF", 16);
    BigInteger d = new BigInteger("C21A93EE751A8D4FBFD77285D79D6768C58EBF283743D2889A395F266C78F4A28E86F545960C2CE01EB8AD5246905163B28D0B8BAABB959CC03F4EC499186168AE9ED6D88058898907E61C7CCCC584D65D801CFE32DFC983707F87F5AA6AE4B9E77B9CE630E2C0DF05841B5E4984D059A35D7270D500514891F7B77B804BED81", 16);
    test(Bob, n, e, d, "Book");

    // Get Bob's public key, encrypt and send the message
    pubKey = Bob.getPublicKey();
    x = new BigInteger("4");
    y = pubKey.encrypt(x);
    Bob.sendMessage(y);

    // Test that Bob got the message
    test(Bob, x, "Book");

    // Get Bob's public key, encrypt and send the message
    x = new BigInteger("4019283740198273058971406510749501982365017640571629387461923874610943867501983475092873405982304651928734601982650198465017834950178236"); // Random number I typed in.
    y = pubKey.encrypt(x);
    Bob.sendMessage(y);

    // Test that Bob got the large message
    test(Bob, x, "Book_Large_Message");


    /// Loop Book_Random_e test
    for (int i = 0; i < 100; ++i) {
      Bob = new RSA(p, q);

      // Get Bob's public key, encrypt and send the message
      pubKey = Bob.getPublicKey();
      x = new BigInteger(n.bitLength(), new SecureRandom());
      x = x.mod(n); // Ensure less than n
      y = pubKey.encrypt(x);
      Bob.sendMessage(y);

      // Test that Bob got the large message
      test(Bob, x, "Book_Random_e #" + i);
    }
    
  }
}