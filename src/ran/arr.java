
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

    public static void main(String[] args) {
        // int[] arr = new int[]{1, 21, 5, 7, 2, 9, 5, 12, 2, 17, 9, 21};
    
        // int num = find2nd(arr);
        // System.out.println(num);
    
        String a = wordRev("hello world this is java");
        System.out.println(a);
    }
}
