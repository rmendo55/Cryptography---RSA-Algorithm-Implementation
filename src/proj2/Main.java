package proj2;

import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter q: ");
        BigInteger q = scanner.nextBigInteger();
        System.out.print("\nEnter p: ");
        BigInteger p = scanner.nextBigInteger();

//        instantiate a RSAGenKey
        RSAGenKey rsaGenKey = new RSAGenKey(p, q);
//        call the method to generate public and private key
        rsaGenKey.generatePublicPrivateKey();
    }
}
