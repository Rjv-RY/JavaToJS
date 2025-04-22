
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class arr {

    public static int find2nd (int[] arr){
        int l = Integer.MIN_VALUE;
        int l2 = Integer.MIN_VALUE;
        for(int n : arr){
            if(l < n){
                l2 = l;
                l = n;
            } else if (l2 < n && n < l){
                l2 = n;
            }
        }
        return l2;
    }

    public static String wordRev (String str){
        StringBuilder bufferString = new StringBuilder();
        StringBuilder revWhole = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == ' '){
                revWhole.append(bufferString.reverse());
                revWhole.append(' ');
                bufferString = new StringBuilder();
            } else {
                bufferString.append(str.charAt(i));
            }
        }
        revWhole.append(bufferString.reverse());
        return revWhole.toString();
    }

    public static Boolean palindroming (String str){
        str = str.replaceAll("[^a-zA-Z]", "");
        char[] arr = str.toLowerCase().toCharArray();
        char[] arr2 = new char[arr.length];

        for (int i = 0, j = arr.length - 1; i < arr.length; i++, j--){
            arr2[j] = arr[i];
        }

        return Arrays.equals(arr, arr2);
    }

    public static char noRep1 (String str){
        char[] arr = str.toLowerCase().toCharArray();
        Map<Character, Integer> map = new HashMap<>();

        for (char c : arr){
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        for (char c : arr) {
            if (map.get(c) == 1) {
                return c;
            }
        }
        return '?';
    }

    public static Boolean anagrmChek(String str1, String str2){
        str1 = str1.replaceAll("[^a-zA-Z]", "");
        str2 = str2.replaceAll("[^a-zA-Z]", "");

        if (str1.length() != str2.length()) return false;

        char[] arr1 = str1.toLowerCase().toCharArray();
        Map<Character, Integer> str1Map = new HashMap<>();

        char[] arr2 = str2.toLowerCase().toCharArray();
        Map<Character, Integer> str2Map = new HashMap<>();

        for (int i = 0; i < arr1.length; i++) {
            char c1 = arr1[i];
            char c2 = arr2[i];
            str1Map.put(c1, str1Map.getOrDefault(c1, 0) + 1);
            str2Map.put(c2, str2Map.getOrDefault(c2, 0) + 1);
        }
        if(str1Map.size() != str2Map.size()) return false;

        for(Character key : str1Map.keySet()){
            if (!str2Map.containsKey(key)) return false;
            if (!str1Map.get(key).equals(str2Map.get(key))) return false;
        }

        return true;
    }

    public static void main(String[] args) {
        // int[] arr = new int[]{1, 21, 5, 7, 2, 9, 5, 12, 2, 17, 9, 21};
    
        // int num = find2nd(arr);
        // System.out.println(num);
    
        // String a = wordRev("hello world this is java");
        // System.out.println(a);

        // boolean p = palindroming("A man, a plan, a canal: Panama");
        // System.out.println(p);

        // char c = noRep1("rogor");
        // System.out.println(c);

        boolean anagramYorN = anagrmChek("Lis tten to", "Stiletton");
        System.out.println(anagramYorN);
    }
}
