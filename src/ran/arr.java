
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public static String stringCompressor(String str){
        str = str.replaceAll("[^a-zA-Z]", "");
        char[] arr = str.toLowerCase().toCharArray();
        StringBuilder comStr = new StringBuilder();
        int c = 1;

        for (int i = 0; i < arr.length; i++){
            if (i < arr.length - 1 && arr[i] == arr[i + 1]){
                c++;
            } else {
                if (c > 1){
                    comStr.append(arr[i]);
                    comStr.append(c);
                    c = 1;
                } else {
                    comStr.append(arr[i]);
                    c = 1;
                }
            }
        }
        return comStr.toString();
    }

    public static String stringCompressor2(String str){
        str = str.replaceAll("[^a-zA-Z]", "");
        StringBuilder comStr = new StringBuilder();
        int c = 1;

        for (int i = 0; i < str.length(); i++){
            if (i < str.length() - 1 && str.charAt(i) == str.charAt(i + 1)){
                c++;
            } else {
                if (c > 1){
                    comStr.append(str.charAt(i));
                    comStr.append(c);
                    c = 1;
                } else {
                    comStr.append(str.charAt(i));
                    c = 1;
                }
            }
        }
        return comStr.toString();
    }

    public static char firstNonRep(String str){
        str = str.replaceAll("[^a-zA-Z]", "").toLowerCase();
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            map.put(str.charAt(i), map.getOrDefault(str.charAt(i), 0) + 1);
        }

        for (int i = 0; i < str.length(); i++) {
            if (map.get(str.charAt(i)) == 1) {
                return str.charAt(i);
            }
        }
        return ' ';
    }

    public static String firstUniqueCharOrDefault(String str){
        str = str.replaceAll("[^a-zA-Z]", "").toLowerCase();
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            map.put(str.charAt(i), map.getOrDefault(str.charAt(i), 0) + 1);
        }

        for (int i = 0; i < str.length(); i++) {
            if (map.get(str.charAt(i)) == 1) {
                return String.valueOf(str.charAt(i));
            }
        }
        return "NO_UNIQUE_CHAR";
    }

    public static String firstUniqueWord (String str){
        List<String> wordList = sentenceBreakerWordlistMaker(str);
        List<Map<Character, Integer>> wordMapList = new ArrayList<>();

        for (int i = 0; i < wordList.size(); i++) {
            String listWord = wordList.get(i);
            Map<Character, Integer> wordMap = new HashMap<>();
            for (int j = 0; j < listWord.length(); j++) {
                wordMap.put(listWord.charAt(j), wordMap.getOrDefault(listWord.charAt(j), 0) + 1);
            }
            wordMapList.add(wordMap);
        }

        for (int i = 0; i < wordMapList.size(); i++){
            boolean allUnique = true;
            for(Character key : wordMapList.get(i).keySet()){
                if(wordMapList.get(i).get(key) != 1){
                    allUnique = false;
                    break;
                }
            }
            if (allUnique) {
                return wordList.get(i);
            }
        }
        return "NO_UNIQUE_WORD";
    }

    public static List<String> sentenceBreakerWordlistMaker(String str){
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != ' ') {
                currentWord.append(c);
            } else {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0); // reset StringBuilder
                }
            }
        }
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        return words;
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

        // boolean anagramYorN = anagrmChek("Lis tten to", "Stiletton");
        // System.out.println(anagramYorN);

        // String ans = stringCompressor("aaacccgeala");
        // System.out.println(ans);

        // String ans2 = stringCompressor2("aaacccgeala");
        // System.out.println(ans2);

        String str = firstUniqueWord("the quick brown fox jumps over the lazy dog");
        System.out.println(str);
    }
}
