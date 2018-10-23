import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class BSGS {
	
	public BigInteger solve(BigInteger h, BigInteger g, BigInteger p) {
	 
	        Long startTime = System.nanoTime();

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
}