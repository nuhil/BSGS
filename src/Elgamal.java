import java.math.BigInteger;
import java.util.Random;

/**
 * 
 * Class representation of Exponential Elgamal
 *
 */
public class Elgamal {
	private PublicKey pubKey;
	private BigInteger privateKey;

	/**
	 * RSA constructor that initializes a public and private key
	 * 
	 * @param p, prime number
	 * @param alpha, generator number
	 * @param d, the private key
	 */
	public Elgamal(BigInteger p, BigInteger alpha, BigInteger d) {
		BigInteger beta = SaM.SquareAndMultiply(alpha, d, p);
		this.pubKey = new PublicKey(p, alpha, beta);
		this.privateKey = d;
	}

	/**
	 * Encrypts the message and returns the cipher containing both the ephemeral key
	 * and the actual message cipher text.
	 * 
	 * @param message, the message to encrypt
	 * @param i, the random exponent
	 * @return cipher object
	 */
	public Cipher encrypt(BigInteger message, BigInteger i) {
		BigInteger ke = SaM.SquareAndMultiply(pubKey.getAlpha(), i, pubKey.getP());
		BigInteger km = SaM.SquareAndMultiply(pubKey.getBeta(), i, pubKey.getP());
		// BigInteger ctext = SaM.SquareAndMultiply(km, message, pubKey.getP());
		BigInteger xkm = SaM.SquareAndMultiply(pubKey.getAlpha(), message).multiply(km);
		BigInteger ctext = xkm.mod(pubKey.getP());
		return new Cipher(ke, ctext);
	}

	/**
	 * Decrypts the cipher by using its own private key in the RSA object
	 * 
	 * @param cipher, cipher containing the ephemeral key and ciphertext
	 * @return the decrypted message
	 */
	public BigInteger decrypt(Cipher cipher) {
		BigInteger km = SaM.SquareAndMultiply(cipher.getKe(), privateKey, pubKey.getP());
		BigInteger xkm = cipher.getCtext().multiply(km.modInverse(pubKey.getP())).mod(pubKey.getP());
		return BSGS.solve(xkm, pubKey.getAlpha(), pubKey.getP());
	}
	
	public Cipher add(Cipher cipher, BigInteger value) {
		BigInteger sum = cipher.getCtext().multiply(SaM.SquareAndMultiply(pubKey.getAlpha(), value)).mod(pubKey.getP());
		return new Cipher(cipher.getKe(), sum);
	}

	/**
	 * 
	 * Represents the Cipher containing the ephemeral key and ciphertext
	 *
	 */
	class Cipher {
		private BigInteger ke;
		private BigInteger ctext;

		public Cipher(BigInteger ke, BigInteger ctext) {
			this.ke = ke;
			this.ctext = ctext;
		}

		public BigInteger getKe() {
			return ke;
		}

		public BigInteger getCtext() {
			return ctext;
		}
	}

	/**
	 * 
	 * Represents the public key containing the prime p, generator alpha, and beta
	 *
	 */
	class PublicKey {
		private BigInteger beta;
		private BigInteger alpha;
		private BigInteger p;

		public PublicKey(BigInteger p, BigInteger alpha, BigInteger beta) {
			this.beta = beta;
			this.p = p;
			this.alpha = alpha;
		}

		public BigInteger getBeta() {
			return beta;
		}

		public BigInteger getAlpha() {
			return alpha;
		}

		public BigInteger getP() {
			return p;
		}
	}

	public static void main(String[] args) {
		Random rand = new Random();
		// Basic random p test
		BigInteger p = BigInteger.probablePrime(32, new Random());
		System.out.println(p);
		
		//2 is always a generator
		BigInteger alpha = new BigInteger("2");
		
		//some random d and i
		BigInteger d = new BigInteger(Integer.toString(rand.nextInt(Integer.MAX_VALUE)));
		System.out.println("d: " + d);
		BigInteger i = new BigInteger(Integer.toString(rand.nextInt(Integer.MAX_VALUE)));
		System.out.println("i: " + i);
		Elgamal elg = new Elgamal(p, alpha, d);
		
		for (int m = 1; m < 50; m++) {

			BigInteger message = new BigInteger(Integer.toString(m));
			Cipher cipher = elg.encrypt(message, i);
			BigInteger mp = (elg.decrypt(cipher));
			
			if((mp.compareTo(message) != 0)) {
				System.out.println(mp + " does not equal " + message);
				System.exit(1);
			}else {
				System.out.println(mp + " equals " + message);
			}
		}
		
		// Addition test
		for(int m = 1; m < 50; m++) {
			BigInteger message = new BigInteger(Integer.toString(m));
			System.out.println("message value: " + message);
			BigInteger add = new BigInteger(Integer.toString(rand.nextInt(1000)));
			System.out.println("addition value: " + add);
			BigInteger sum = message.add(add);
			
			Cipher cipher = elg.encrypt(message, i);
			cipher = elg.add(cipher, add);
			
			BigInteger mp = elg.decrypt(cipher);
			if(sum.compareTo(mp) != 0) {
				System.out.println(mp + " does not equal " + sum);
				System.exit(1);
			}
		}
		
		
	}
}