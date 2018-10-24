import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.Runnable;

public class BSGS {
	
	public static BigInteger solve(BigInteger h, BigInteger g, BigInteger p) {
		System.out.println("Solving");
		System.out.println("h: " + h);
		System.out.println("g: " + g);
		System.out.println("p: " + p);
		
		/* Compute m */
		BigInteger[] mp = p.sqrtAndRemainder();
		BigInteger m = (mp[1].intValue() >= 0)? mp[0].add(BigInteger.ONE) : mp[0]; // Ceiling ...
		BigInteger q = BigInteger.ZERO, r = BigInteger.ZERO;

		HashMap<BigInteger, BigInteger> gs = new HashMap<>();

		/* Compute g^0, g^1 ... g^(m-1) mod p */
		for (BigInteger i = BigInteger.ZERO; i.compareTo(m) == -1; i = i.add(BigInteger.ONE)) {
			BigInteger key = SaM.SquareAndMultiply(g, i, p);
			gs.put(key, i);
		}

		/* Compute g^-m mod p */
		BigInteger gm = SaM.SquareAndMultiply(g.modInverse(p), m, p);

		/* Compute h(g^-m)^0, h(g^-m)^1 ... h(g^-m)^q where q = 0,1,2 ... */
		for (BigInteger j = BigInteger.ZERO; j.compareTo(m) == -1; j = j.add(BigInteger.ONE)) {
			BigInteger hi = gm.pow(j.intValue());
			BigInteger hj = h.multiply(hi);
			BigInteger hs = hj.mod(p);

			/* Check in gs HashMap for collison */
			BigInteger collision = gs.get(hs);
			if ( collision != null) {
				r = collision;
				q = j;
				break;
			}
		}
		
		return m.multiply(q).add(r);
	}

	private BlockingQueue<BigInteger> queue = new LinkedBlockingQueue<>();

	private class QueueFiller implements Runnable {
		BigInteger g;
		BigInteger p;
		BigInteger start;
		BigInteger stop;

		QueueFiller(BigInteger g, BigInteger p, BigInteger start, BigInteger stop) {
			this.g = g;
			this.p = p;
			this.start = start;
			this.stop = stop;
		}

		@Override
		public void run() {
			BigInteger key = SaM.SquareAndMultiply(g, start, p);
			for (BigInteger i = start; i.compareTo(stop) < 0; i = i.add(BigInteger.ONE)) {
				try {
					queue.put(key);
				}
				catch (Exception e) 
				{ 
					// Throwing an exception 
					System.out.println ("Exception is caught");

					i = i.subtract(BigInteger.ONE);
					continue;
				} 
				key = key.multiply(g).mod(p);
			}	
		}
	}
		
	public BigInteger solve2(BigInteger h, BigInteger g, BigInteger p) {
		System.out.println("Solving");
		System.out.println("h: " + h);
		System.out.println("g: " + g);
		System.out.println("p: " + p);

		/* Compute m */
		BigInteger[] mp = p.sqrtAndRemainder();
		BigInteger m = (mp[1].compareTo(BigInteger.ZERO) >= 0) ? mp[0].add(BigInteger.ONE) : mp[0]; // Ceiling ...
		BigInteger q = BigInteger.ZERO, r = BigInteger.ZERO;

		HashMap<BigInteger, BigInteger> gs = new HashMap<>();

		/* Compute g^0, g^1 ... g^(m-1) mod p */
		BigInteger fraction = m.shiftRight(3);
		BigInteger next;
		for (BigInteger i = BigInteger.ZERO; i.compareTo(m) < 0; i = next) {
			next = i.add(fraction);
			BigInteger stop = m.compareTo(next) < 0 ? m : next;
			Thread filler = new Thread(new QueueFiller(g, p, i, stop));
			filler.start();

			if (i.mod(fraction).equals(BigInteger.ZERO))
				System.out.println(i + " of " + m);
		}

		/* Compute g^-m mod p */
		BigInteger gm = SaM.SquareAndMultiply(g.modInverse(p), m, p);

		/* Compute h(g^-m)^0, h(g^-m)^1 ... h(g^-m)^q where q = 0,1,2 ... */
		BigInteger hi = BigInteger.ONE;
		BigInteger hj = h;
		for (BigInteger j = BigInteger.ZERO; j.compareTo(m) < 0; j = j.add(BigInteger.ONE)) {
			BigInteger hs = hj.mod(p);

			/* Check in queue for collison */
			boolean collision = queue.contains(hs);
			if (collision) {
				//r = collision; // Uhhh
				r = hs; // ??
				q = j;
				break;
			}
			
			hi = hi.multiply(gm);
			hj = h.multiply(hi);
		}
		
		return m.multiply(q).add(r);
	}

	public static void main(String[] args) {
		BigInteger h;
		BigInteger g;
		BigInteger p;
		BigInteger correct;
		BigInteger check;

		// Base test
		h = new BigInteger("6");
		g = new BigInteger("3");
		p = new BigInteger("31");
		correct = new BigInteger("25");
		check = BSGS.solve(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("Base test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// Base2 test
		h = new BigInteger("1132");
		g = new BigInteger("22");
		p = new BigInteger("9587");
		correct = new BigInteger("389");
		check = BSGS.solve(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("Base2 test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// Base3 test
		h = new BigInteger("22479");
		g = new BigInteger("99");
		p = new BigInteger("32413");
		correct = new BigInteger("521");
		check = BSGS.solve(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("Base3 test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// Base4 test
		h = new BigInteger("4338246301");
		g = new BigInteger("1208");
		p = new BigInteger("9048610007");
		correct = new BigInteger("948603");
		check = BSGS.solve(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("Base4 test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// Small prime test
		h = new BigInteger("135110061184");
		g = new BigInteger("2");
		p = new BigInteger("617335750949");
		correct = new BigInteger("5634918");
		check = BSGS.solve(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("Small prime test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// 64-bit prime test
		h = new BigInteger("9042873266636947420");
		g = new BigInteger("2");
		p = new BigInteger("11384842107348937439");
		correct = new BigInteger("46712584");
		BSGS bsgs = new BSGS();
		check = bsgs.solve2(h, g, p);
		if (correct.compareTo(check) != 0) {
			System.out.println("64-bit prime test fail");
			System.out.println("  Correct: " + correct);
			System.out.println("  Check: " + check);
		}

		// 128-bit prime test (Takes too long)
		//h = new BigInteger("188294788269775576929570250900781751822");
		//g = new BigInteger("2");
		//p = new BigInteger("195241548412374747688105409717824721599");
		//correct = new BigInteger("563364918");
		//check = BSGS.solve2(h, g, p);
		//if (correct.compareTo(check) != 0) {
		//	System.out.println("128-bit prime test fail");
		//	System.out.println("  Correct: " + correct);
		//	System.out.println("  Check: " + check);
		//}
	}
}