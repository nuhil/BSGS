import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("data.txt");
        Scanner sc = new Scanner(file);

        ArrayList<BigInteger> data = new ArrayList<>();
        while (sc.hasNextBigInteger()) {
            try {
                BigInteger bigint = sc.nextBigInteger();
                data.add(bigint);
            } catch (NumberFormatException ex) {
                System.out.println(ex.toString());
            }
        }

        BigInteger h = data.get(0);
        BigInteger g = data.get(1);
        BigInteger p = data.get(2);

        Long startTime = System.nanoTime();

        /* Compute m */
        BigInteger[] mp = p.sqrtAndRemainder();
        BigInteger m = (mp[1].intValue() >= 0)? mp[0].add(new BigInteger("1")) : mp[0]; // Ceiling ...
        BigInteger q = BigInteger.ZERO, r = BigInteger.ZERO;

        HashMap<BigInteger, BigInteger> gs = new HashMap<>();

        /* Compute g^0, g^1 ... g^(m-1) mod p */
        for (BigInteger i = BigInteger.ZERO; i.compareTo(m) == -1; i = i.add(BigInteger.ONE)) {
            gs.put(g.modPow(i, p), i);
        }

        /* Compute g^-m mod p */
        BigInteger gm = g.modPow(m.negate(), p);

        /* Compute h(g^-m)^0, h(g^-m)^1 ... h(g^-m)^q where q = 0,1,2 ... */
        for (BigInteger j = BigInteger.ZERO; j.compareTo(m) == -1; j = j.add(BigInteger.ONE)) {
            BigInteger hs = h.multiply(gm.pow(j.intValue())).modPow(BigInteger.ONE, p);

            /* Check in gs HashMap for collison */
            BigInteger collision = gs.get(hs);
            if ( collision != null) {
                r = collision;
                q = j;
                break;
            }
        }

        Long stopTime = System.nanoTime();
        System.out.println(m.multiply(q).add(r));
        System.out.println("It took " + ((stopTime - startTime) / 100000000.0) + " seconds.");
    }
}
