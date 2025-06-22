package edu.java;

public class CommentsDeleter {
    public String deleteComments(String code) {
        String tmp = code;
        int commentStart = code.indexOf("/*");
        int commentEnd = code.indexOf("*/");
        while (commentStart != -1) {
            String one = "";
            String two = "";
            if (commentStart > 0) {
                one = tmp.substring(0, commentStart - 1);
            }
            if (commentEnd < tmp.length() - 2) {
                two = tmp.substring(commentEnd + 2);
            }
            tmp = one + two;
            commentStart = tmp.indexOf("/*");
            commentEnd = tmp.indexOf("*/");
        }
        return String.join("", tmp.split("//(.*)")).trim();
    }
}




