package proj2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class RSAGenKey {
    private BigInteger p;   //assign user input for p to p
    private BigInteger q;   //assign user input for q to q

    public RSAGenKey() {}   //default constructor

    public RSAGenKey(BigInteger p, BigInteger q) //initialize p and q from users input
    {
        this.p = p;
        this.q = q;
    }

    public void generatePublicPrivateKey()
    {
        BigInteger n = this.p.multiply(this.q); //first calculate n
        BigInteger p2 = this.p.subtract(new BigInteger("1")); //calculate 0(n)
        BigInteger q2 = this.q.subtract(new BigInteger("1"));
        BigInteger oN = p2.multiply(q2);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter any value for e that meets the constraint of (1<e<oN): ");
        BigInteger e = null;
        int count = 0;
        /*
        do while loop is used to validate users input because the number entered for e must be between 1 and oN
        and the gcd between oN and e must equal to 1. If that's not the case then renter value for e
         */
        do
        {
            count = count + 1;
            if (count > 1)
            {
                System.out.println("You entered an invalid value for e or the gcd of (" + oN + "," + e + ")" + "does not equal to 1. Renter e please!");
            }
            e = scanner.nextBigInteger();
        }while(e.compareTo(new BigInteger("1")) == -1 || e.compareTo(new BigInteger("1")) == 0 || e.compareTo(oN) == 1 || e.compareTo(oN) == 0 && !checkGCD(e, oN) || !checkGCD(e, oN)); //check if the gcd of e and oN equals 1

        BigInteger d = getInverseOfE(e, oN); //calculate d
        //public key = {e, oN), private key = {d, oN}
        File pubKey = new File("pub_key.txt");
        File priKey = new File("pri_key.txt");
        try
        {
            FileWriter writeToPubKey = new FileWriter(pubKey.getAbsolutePath()); //write e and n to pub_key.txt
            FileWriter writeToPrivateKey = new FileWriter(priKey.getAbsolutePath()); //write d and n to pri_key.txt
            writeToPubKey.write("{e=" + e + ", n=" + n + "}");
            writeToPrivateKey.write("{d=" + d + ", n=" + n + "}");
            writeToPubKey.close();          //close filewriter for pub_key.txt
            writeToPrivateKey.close();      //close filewriter for pri_key.txt
        } catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public BigInteger getInverseOfE(BigInteger e, BigInteger oN)
    {
        //stop when r1 becomes 1
        //variables needed
        BigInteger q, r, t;
        BigInteger t1 = new BigInteger("0");
        BigInteger r1 = oN;
        BigInteger r2 = e;
        BigInteger t2 = new BigInteger("1");
        while (r1.compareTo(new BigInteger("1")) == 1)
        {
            q = r1.divide(r2);
            r = r1.mod(r2);
            t = t1.subtract(t2.multiply(q));
            r1 = r2;
            r2 = r;
            t1 = t2;
            t2 = t;
        }
        if (t1.compareTo(new BigInteger("0")) == -1) //negative, then add it to t2;
        {
            t1 = t1.add(t2);
        }
        return t1;
    }

    public boolean checkGCD(BigInteger e, BigInteger oN)
    {
        if (e.compareTo(new BigInteger("0")) == 0)
        {
            return false;
        }
        BigInteger r = null;
        BigInteger r1 = oN;
        BigInteger r2 = e;
        while (r1.compareTo(new BigInteger("1")) == 1)
        {
            r = r1.mod(r2);
            r1 = r2;
            r2 = r;
            if (r2.compareTo(new BigInteger("0")) == 0)
            {
                break;
            }
        }
        if (r1.compareTo(new BigInteger("1")) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
