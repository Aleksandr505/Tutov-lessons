package org.example.codeconverter.shennon;

public class ShannonFanoNode {
    public char character;
    public double probability;
    public String code;

    public ShannonFanoNode(char character, double probability) {
        this.character = character;
        this.probability = probability;
        this.code = "";
    }
}
