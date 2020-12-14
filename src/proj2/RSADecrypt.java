package proj2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class RSADecrypt {
    public static void main(String[] args) {
        /*
        Instantiate a HashMap
        Ex's: key 1, value = b  key 2, value = c
        Will be used when reading the test.enc file to decrypt each block of 3 bytes
         */
        HashMap<Integer, Character> map = new HashMap<Integer, Character>();
        /*
        populateMap is a method to populate the hash map will values with keys with its corresponding values
         */
        populateMap(map);
        /*
         decrypt test.enc and write decryption text to test.dec
         */
        decryptFile(map);
    }

    public static void populateMap(HashMap<Integer, Character> map) {
        //loop from 0-26 (a-z)
        int character = 97; //a starts at decimal value of 97
        for (int i = 0; i < 26; i++) {
            char current = ((char) character);      //convert number to its assigned character value
            if (i == 0)
            {
                map.put(27, current);               //special case for a. To avoid issues with 00 when decrypting
            }
            else
            {
                map.put(i, current);                    //append character in the hashmap
            }
            character++;
        }
    }

    public static void decryptFile(HashMap<Integer, Character> map)
    {
        try {
            //get private key to decrypt each ciphertext text
            File priKey = new File("pri_key.txt");
            Scanner myScanner = new Scanner(priKey);        //use scanner to read pri_key.txt
            BigInteger d = null;                            //variable for decryption value
            BigInteger n = null;                            //variable for mod p = c^e mod n
            while (myScanner.hasNextLine()) {               //read each line from pre_key.txt
                String[] arr = myScanner.nextLine().split(",");         //store e and n in separate indexes into array
                d = new BigInteger(arr[0].substring(3, arr[0].length()));     //grab d
                n = new BigInteger(arr[1].substring(3, arr[1].length() - 1)); //grab n
            }

            //read test.enc
            File test = new File("test.enc");
            //write decryption to test.dec
            FileWriter writeToOutPut = new FileWriter("test.dec");
            myScanner = new Scanner(test);      //read test.enc
            while (myScanner.hasNextLine())     //read each line from test.enc
            {
                String word = "";              //word be assigned the entire text of each line
                String[] arr = myScanner.nextLine().split(" ");  //store each block of 3 bytes into array
                for (int i = 0; i < arr.length; i++) {
                    BigInteger c = new BigInteger(arr[i]);            //grab ciphertext
                    //decrypt c to p
                    BigInteger p = c.modPow(d, n);                    //convert ciphertext to plaintext number
                    /*
                    Method convertDecimalToString does the following
                    p = 190708 convertDecimalToString returns word = whe
                     */
                    String conversion = convertDecimalToString(p.toString(), map);
                    word += conversion;     //word will obtain the entire string for each line
                }
                writeToOutPut.write(word); //after word is assigned every word then write to test.dec
                writeToOutPut.write("\n");  //start a new line. To keep the same format as test.txt
            }
            /*
            Close FileWriter to save text
            if not closed then the test.dec will be empty
             */
            writeToOutPut.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String convertDecimalToString(String value, HashMap<Integer, Character> map)
    {
        /*
        variable used to grab two values at a time
        ex: concat = 19 -> count = 2 -> use hashmap -> map return "w"
         */
        String concat = "";
        /*
        conversion will have the final word
        ex: value sent to convertDecimalToString 190708
        conversion is returned back to where the method was called and the value returned would be "whe"
         */
        String conversion = "";
        /*
        firstIteration is used to help keep track of special cases
        This algorithm is meant to grab two numbers at a time. ex: 19 -> w 18 -> s
        However two numbers can be from 0 -10. 01, 02, 03, 04. So when mapping 02, 03 a runtime error happens
        So first iteration will help identify to only grab the second character
        01 -> use 1 only, 03 -> use 3 only
         */
        boolean firstIteration = true;
        int count = 0;
            for (int i = 0; i < value.length() + 1; i++)
            {
                /*
                case: 12019, length = 5 % 2 = 1, uneven so this tells me that I must only grab the
                first index if its the first iteration.
                12019 equivalent to 012019 but 01 doesn't work for mapping so use 1 only
                 */
                if (value.length() % 2 == 1 && firstIteration)
                {
                    conversion += map.get(Integer.parseInt(value.charAt(0) + ""));  //concatenate current number
                    firstIteration = false;
                }
                else if (count == 2)
                {
                    if (concat.equals("26"))
                    {
                            conversion += " "; //add a space because there is a next word
                    }
                    else if ((concat.charAt(0) + "").equals("0"))
                    {
                        if (map.get(Integer.parseInt((concat.charAt(1) + ""))) != null)
                        {
                            conversion += map.get(Integer.parseInt((concat.charAt(1) + ""))); //use only 1 value (00-09)
                        }
                    }
                    else
                    {
                        if (map.get(Integer.parseInt(concat)) != null)
                        {
                            conversion += map.get(Integer.parseInt(concat));        //convert number to text
                        }
                    }
                    if (i != value.length()) //when the last loop executes this grabs the last character
                    {
                        concat = "";
                        concat += value.charAt(i);
                        count = 1;
                    }
                }
                else
                {
                    if (i < value.length())
                    {
                        concat += value.charAt(i);
                        count++;
                    }
                }
            }
        return conversion;
    }
}
