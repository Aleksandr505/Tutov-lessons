package org.example.codeconverter.huffman;

public class HuffmanNode {
    int frequency;
    char character;

    HuffmanNode left, right;

    HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        left = right = null;
    }
}
