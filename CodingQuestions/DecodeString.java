package CodingQuestions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class DecodeString {

    public static String decodeString(String s) {

        Stack<String> st = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (ch == '[') {
                st.push("[");
            } else if (s.charAt(i) == ']') {
                StringBuilder sb = new StringBuilder();
                List<String> parts = new ArrayList<>();

                // abcabc, de -> debabcabc -> cbacbaed

                while (!st.isEmpty() && !st.peek().equals("[")) {
                    parts.add(st.pop());
                }
                // abcabc, de -> de abcabc -> abcabc de
                Collections.reverse(parts);

                for (String part : parts) {
                    sb.append(part);
                }

                if (!st.isEmpty() && st.peek().equals("[")) {
                    st.pop();
                }

                StringBuilder num = new StringBuilder();
                while (!st.isEmpty() && st.peek().matches("\\d+")) {
                    num.append(st.pop());
                }
                int digit = Integer.parseInt(num.reverse().toString());

                StringBuilder ans = new StringBuilder();
                for (int j = 0; j < digit; j++) {
                    ans.append(sb.toString());
                }
                st.push(ans.toString());

            } else {
                st.push(String.valueOf(ch)); // 3a2c
            }
        }

        String ans = "";
        List<String> groups = new ArrayList<>();
        while (!st.isEmpty()) {
            groups.add(st.pop());
        }
        Collections.reverse(groups);

        for (String group : groups) {
            ans += group;
        }

        return ans;
    }

    public static void main(String[] args) {
        String ans = decodeString("3[z]2[2[y]pq4[2[jk]e1[f]]]ef");
        System.out.println(ans);
    }
}
