package lasdot.com;

import java.util.Arrays;

public class TruncateString {
    String s;
    int wordLength;

    public TruncateString(String s, int wordLength){
        this.s = s;
        this.wordLength = wordLength;
    }

    public String getTruncation() {
        String newString = this.s;
        if (newString.split(" ").length > this.wordLength) {
            String[] newStringArray = newString.split(" ");
            newStringArray = Arrays.copyOfRange(newStringArray, 0, this.wordLength);
            newString = String.join(" ", newStringArray);
            newString += "...";
        }
        return newString;
    }
}
