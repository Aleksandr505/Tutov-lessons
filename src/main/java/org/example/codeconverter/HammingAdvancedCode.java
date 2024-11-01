package org.example.codeconverter;

import java.util.Scanner;

public class HammingAdvancedCode {

    private static Scanner scanner;

    /**
     * This method prints 8-bit data in binary including any leading zeros.
     * @param data The byte to be printed.
     */
    private static void printByte(byte data) {
        int scanbit = 128;
        while (scanbit != 0) {
            if((data & scanbit) == 0)
                System.out.print("0");
            else
                System.out.print("1");

            scanbit /= 2;
        }
    }

    /**
     * This method encodes the data using Hamming(7,4) code.
     * It prints the encoded bits and returns the encoded value as integer.
     * @param data The byte to be encoded
     * @return It returns the corresponding encoded 2-byte value as integer.
     */
    public static int encode(byte data){
        byte nibble;
        byte encodedByte[] = new byte[2];
        int encodedValue = 0;

        // get left nibble and encode it
        nibble = (byte) ((data >>> 4) & 0x0F);
        encodedByte[0] =  encodeNibble(nibble);

        // get right nibble and encode it
        nibble = (byte)(data & 0x0F);
        encodedByte[1] = encodeNibble(nibble);

        // print the encoded bytes
        printByte(encodedByte[0]);
        System.out.print(" ");
        printByte(encodedByte[1]);
        System.out.println();

        encodedValue = encodedByte[0] << 8 | encodedByte[1];
        return encodedValue;
    }

    /**
     * Encodes the lower 4 bits of the argument using Hamming(7,4) code and returns the
     * encoded byte.
     * @param dataNibble The lower nibble contains bits to be encoded and the upper nibble is zero.
     * @return Returns the encoded byte.
     */
    private static byte encodeNibble(byte dataNibble) {
        byte encoded = 0;

		/*
		 even parity is used here.
		 dataNibble: 0  0   0   0  d1 d2 d3 d4
		 encoded    : 0 p1 p2 d1 p3 d2 d3 d4
		 parity p1   :    p1      d1      d2      d4
		 parity p2   :         p2 d1           d3 d4
		 parity p3                       p3 d2 d3 d4

		 Example 1: 1 0 1 1
		 dataNibble: 0  0   0  0 1   0 1 1
		 encoded    : 0 p1 p2 1 p3 0 1 1
		 parity p1   : d1 + d2 + d4 = 2  => p1 = 0
		 parity p2   : d1 + d3 + d4 = 3  => p2 = 1
		 parity p3   : d2 + d3 + d4 = 2  => p3 = 0
		 encoded    : 0 0 1 1 0 0 1 1

		 Example 2: 0 0 0 1
		 dataNibble: 0  0   0  0 0   0 0 1
		 encoded    : 0 p1 p2 0 p3 0 0 1
		 parity p1   : d1 + d2 + d4 = 1  => p1 = 1
		 parity p2   : d1 + d3 + d4 = 1  => p2 = 1
		 parity p3   : d2 + d3 + d4 = 1  => p3 = 1
		 encoded    : 0 1 1 0 1 0 0 1
		*/

        // put data bits
        encoded = (byte) (encoded | (dataNibble & 0x07));       // put d2-d4 at position 5-7
        encoded = (byte) (encoded | (dataNibble & 0x08) << 1);  // put d1 at position 3

        if((encoded & 0x01) != 0) { // check d4
            encoded = (byte) (encoded ^ 0x68);	// 0x40 + 0x20 + 0x08 = 0x68 toggle p1,p2,p3 bit
        }

        if((encoded & 0x02) != 0) {  // check d3
            encoded = (byte) (encoded ^ 0x28);  // 0x20 + 0x08 = 0x28 toggle p2, p3 bit
        }

        if((encoded & 0x04) != 0) {  // check d2
            encoded = (byte) (encoded ^ 0x48);	// 0x40 + 0x08 = 0x48 toggle p1, p3 bit
        }

        if((encoded & 0x10) != 0) { // check d1
            encoded = (byte) (encoded ^ 0x60);	// 0x40 + 0x20 = 0x60 toggle p1,p2 bit
        }

        return encoded;
    }

    /**
     * Decodes Hamming(7,4) coded byte, prints the value in binary and returns the decoded value as integer.
     * @param encoded The encoded byte to decode.
     * @return Returns the decoded value as integer.
     */
    public static int decode(byte encoded) {
        return decode((byte)0, encoded);
    }

    /**
     * Decodes Hamming(7,4) coded bytes, prints the value in binary and returns the decoded value as integer.
     * @param encodedLeft The first byte to decode. The decoded 4-bits constitute the left nibble of the decoded byte.
     * @param encodedRight The second byte to decode. The decoded 4-bits constitute the right nibble of the decoded byte.
     * @return Returns the byte decoded from the two arguments.
     */
    public static int decode(byte encodedLeft, byte encodedRight) {
        int decodedByte = 0;

        byte leftNibbleDecoded = decodeNibble(encodedLeft);
        byte rightNibbleDecoded = decodeNibble(encodedRight);

        decodedByte =  leftNibbleDecoded << 4;
        decodedByte = decodedByte | rightNibbleDecoded;

        printByte((byte)decodedByte);
        return decodedByte;
    }

    /**
     * This method decodes a byte and returns the decoded byte.
     * @param encoded The encoded byte to be decoded.
     * @return Returns the decoded byte.
     */
    private static byte decodeNibble(byte encoded)
    {
        byte decoded = 0;

        //  encoded   : 0 p1 p2 d1 p3 d2 d3 d4
        //  decoded   : 0 p1 p2 p3 d1 d2 d3 d4

        // put data bits
        decoded = (byte) (decoded | (encoded & 0x07));       // put d2-d4 at position 5-7
        decoded = (byte) (decoded | (encoded & 0x10) >> 1);  // put d1 at position 3

        // put parity bits
        decoded = (byte) (decoded | (encoded & 0x60));       // put p1-p2 at position 1-2
        decoded = (byte) (decoded | (encoded & 0x08) << 1);  // put d1 at position 3


        if((decoded & 0x01) != 0) { // check d4
            decoded = (byte) (decoded ^ 0x70);	// 0x40 + 0x20 + 0x10 = 0x70 toggle p1,p2,p3 bit
        }

        if((decoded & 0x02) != 0) { // check d3
            decoded = (byte) (decoded ^ 0x30);  // 0x20 + 0x10 = 0x30 toggle p2, p3 bit
        }

        if((decoded & 0x04) != 0) {  // check d2
            decoded = (byte) (decoded ^ 0x50);	// 0x40 + 0x10 = 0x48 toggle p1, p3 bit
        }

        if((decoded & 0x08) != 0) {  // check d1
            decoded = (byte) (decoded ^ 0x60);	// 0x40 + 0x20 = 0x60 toggle p1,p2 bit
        }

        if ((decoded & 0x70) == 0) { // p1 = p2 = p3 = 0, all even, no error
            return decoded;
        }

        if (((decoded & 0x10) != 0) && ((decoded & 0x20) != 0) && ((decoded & 0x40) != 0)) {
            // p1 = p2 = p3 = 1, d4 is in error
            decoded = (byte) ((decoded ^ 0x01) & 0x0F);
            return decoded;
        }

        if (((decoded & 0x20) != 0) && ((decoded & 0x40) != 0)) {
            // p1 = p2 = 1, d1 is in error
            decoded = (byte) ((decoded ^ 0x08) & 0x0F);
            return decoded;
        }

        if (((decoded & 0x10) != 0) && ((decoded & 0x40) != 0)) {
            // p1 = p3 = 1, d2 is in error
            decoded = (byte) ((decoded ^ 0x04) & 0x0F);
            return decoded;
        }

        if (((decoded & 0x10) != 0) && ((decoded & 0x20) != 0)) {
            // p2 = p3 = 1, d3 is in error
            decoded = (byte) ((decoded ^ 0x02) & 0x0F);
            return decoded;
        }

        if (((decoded & 0x10) != 0) || ((decoded & 0x20) != 0) || ((decoded & 0x40) != 0)) {
            // p1 = 1 OR p2 = 1 OR p3 = 1, any parity bit is in error
            decoded = (byte) (decoded & 0x0F);
            return decoded;
        }

        return decoded;
    }

    /**
     * The main method.
     * @param args
     */
    public static void main(String[] args) {
        String command;
        int b1, b2;

        while (true) {
            scanner = new Scanner(System.in);
            command = scanner.next();
            if (command.equalsIgnoreCase("encode")) {
                b1 = scanner.nextInt(2);
                System.out.print("Encoded: ");
                encode((byte)b1);
            } else if (command.equalsIgnoreCase("decode")) {
                b1 = scanner.nextInt(2);
                b2 = scanner.nextInt(2);
                System.out.println("Decoded:");
                decode((byte)b1, (byte)b2);
            } else {
                break;
            }
        }
    }

}
