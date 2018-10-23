import java.math.BigInteger;

public class Elgamal {
	private SaM sam = new SaM();
	private BSGS bsgs = new BSGS();
	private PublicKey pubKey;
	private BigInteger privateKey;
	
	public Elgamal(BigInteger p, BigInteger alpha, BigInteger d) {
		BigInteger beta = sam.SquareAndMultiply(alpha, d, p);
		this.pubKey = new PublicKey(p, alpha, beta);
		this.privateKey = d;
	}
	
	public Cipher encrypt(BigInteger message, BigInteger i) {
		BigInteger ke = sam.SquareAndMultiply(pubKey.getAlpha(), i, pubKey.getP());
		BigInteger km = sam.SquareAndMultiply(pubKey.getBeta(), i, pubKey.getP());
		//BigInteger ctext = sam.SquareAndMultiply(km, message, pubKey.getP());
		BigInteger xkm = sam.SquareAndMultiply(pubKey.getAlpha(), message).multiply(km);
		BigInteger ctext = xkm.mod(pubKey.getP());
		return new Cipher(ke, ctext);
	}
	
	public BigInteger decrypt(Cipher cipher) {
		BigInteger km = sam.SquareAndMultiply(cipher.getKe(), privateKey, pubKey.getP());
		BigInteger xkm = cipher.getCtext().multiply(km.modInverse(pubKey.getP())).mod(pubKey.getP());
		return bsgs.solve(xkm, pubKey.getAlpha(), pubKey.getP());
	}
	
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
		BigInteger p = new BigInteger("29"); 
		BigInteger alpha = new BigInteger("2");
		BigInteger d = new BigInteger("12");
		BigInteger message = new BigInteger("3");
		BigInteger i = new BigInteger("5");
		
		Elgamal elg = new Elgamal(p, alpha, d);
		Cipher cipher = elg.encrypt(message, i);
		System.out.println(elg.decrypt(cipher));
	}
}
