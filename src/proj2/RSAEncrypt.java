package proj2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RSAEncrypt {

    public static void main(String[] args)
    {
        /*
        Instantiate a HashMap
        Ex's: key a, value = 00         key b, value = 01
        Will be used when reading the test.txt file to encrypt each block of 3 bytes
         */
        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        /*
        populateMap is a method to populate the hash map with values with keys and its corresponding values
         */
        populateMap(map);
        /*
            Read the test.txt file, create all the blocks of 3 bytes
            Then encrypt each block with the encoding scheme 00-26
            Then write to a file creating the test.enc file
         */
        encryptFile(map);
    }

    public static void populateMap(HashMap<Character, Integer> map)
    {
        /*
        loop from 0-26 (a-z)
        will change a (00) to (27)
        because there are cases such as the word: an -> 0013 -> 13
        Issue: when encrypting 00 is gone, not used. So when decrypting the output will only be n
         */
        int character = 97;         //a starts at decimal value of 97
        for (int i = 0; i < 26; i++) {
                char current = ((char) character);          //convert number to its assigned character value
            if (i == 0)
            {
                map.put(current, 27);                        //append character to the hashmap
            }
            else
            {
                map.put(current, i);
            }
            character++;
        }
    }

    public static void encryptFile(HashMap<Character, Integer> map)
    {
        try
        {
            //grab pub_key.txt to get the public key to encrypt each block
            File file = new File("pub_key.txt");
            Scanner myScanner = new Scanner(file);          //using scanner to read the pub_key.txt
            BigInteger e = null;                            //e is for the encryption key
            BigInteger n = null;
            while (myScanner.hasNextLine())
            {
                String[] arr = myScanner.nextLine().split(",");        //using split array to store e and n into different indexes of the array
                e = new BigInteger(arr[0].substring(3, arr[0].length()));    //ex: {e=#, n=#}
                n = new BigInteger(arr[1].substring(3, arr[1].length() - 1));   //ex: {d=#,n=#}
            }

            File testTxt = new File("test.txt"); //grab test.txt file
            myScanner = new Scanner(testTxt);     //reinitialize myScanner to read test.txt
            ArrayList<char[]> list = new ArrayList<>();         //store each block of 3 bytes into an arrayList
            /*
            count is needed to indicate when 3 characters have been concatenated
            if so then encrypt the block

             */
            int count = 0;
            while (myScanner.hasNextLine())
            {
                /*
                concat is the variable getting the characters from test.txt
                ex: test.txt = this is a test
                concat = thi, s26i s26a 26te st
                26 refers to a space. Helps when decrypting.
                 */
                String concat = "";
                count = 0;              //when next line is read the count has to reset back to 0
                /*
                storing all blocks into an array
                ex: [[thi],[26,i,s]]
                To be able to loop through each character
                 */
                String[] arr = myScanner.nextLine().split(" ");
                for (int i = 0; i < arr.length; i++)
                {
                    for (int j = 0; j < arr[i].length(); j++)
                    {
                        if (count == 3) {
                            list.add(concat.toCharArray());         //concat length is 3(three characters) add it to arraylist
                            concat = "";                            //rest concat back to an empty string
                            concat += arr[i].charAt(j);             //concat appends the next character
                            count = 1;                              //count = 1 because a character was concatenated to concat
                        }
                        else
                            {
                                if (arr[i].charAt(j) != '.' && arr[i].charAt(j) != ',') //don 't concatenate , and .
                                {
                                    /*
                                    if current character does not equal to '.' and ',' then concatenate the current character
                                     */
                                    concat += arr[i].charAt(j);
                                    count++;                //increase count since character was concatenated
                                }
                            }
                    }
                    if (i + 1 != arr.length) {
                        if (count == 3)
                        {
                            list.add(concat.toCharArray());
                            concat = "";
                            count = 0;
                        }
                        concat += '\u001A';
                        count++;
                    }
                }
                /*
                when the inner for loop ends the concat variable cna still be initialized with values.
                If so added to list, else if concat is empty then don't add it to list
                 */
                if (concat.length() != 0)
                {
                    //append to list
                    list.add(concat.toCharArray());   //append list with concat
                }
                /*
                this is to indicate to write to the next line
                to help keep the same format as test.txt when decrypting
                 */
                list.add(new char[]{});
            }

            /*
            Creating a new file called test.enc
            Will be the file where all the block encryption's will be written to
             */
            File enc = new File("test.enc");
            //Use FileWrite object to write to the test.enc file
            FileWriter writeToEnc = new FileWriter(enc.getAbsolutePath());
            for (int k = 0; k < list.size(); k++)
            {
                /*
                current is a variable that get the current character
                current2 is a variable that conversion value of the current character to a number
                ex: current = a, current2 = 00     (character to decimal)
                 */
                String current = "";
                String current2 = "";
                if (list.get(k).length != 0)
                {
                    for (int k2 = 0; k2 < list.get(k).length; k2++)
                    {
                        if (list.get(k)[k2] == 26)
                        {
                            current2 += "26";
                        }
                        else
                        {
                            current = (list.get(k)[k2] + "").toLowerCase();
//                    check if value is (0 - 9) to add 00, 01, 02, 03, 04, 05, 06, 07, 08, 09
                            if (map.get(current.charAt(0)) < 10) {
                                current2 += "0";
                            }
                            current2 += map.get(current.charAt(0));
                        }
                    }
                    //encrypt p
                    /*
                    convert current2 to a BigInteger
                    ex: current2 = 190708 (string type) -> convert to BigInteger (Number type)
                     */
                    BigInteger p = new BigInteger(current2);   //conversion value from character to number
                    BigInteger c = p.modPow(e, n);             //encryption value
                    writeToEnc.write(c.toString());            //write encryption value to test.enc
                    writeToEnc.write(" ");                 //separate each encryption value
                }
                else
                {
                    writeToEnc.write("\n");                //start writing next encryption values in the next line
                }
            }
            writeToEnc.close();                               //close file writer
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
