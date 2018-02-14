import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;
/**
 * network_size
 * this program takes two cmd line arguments
 * the first is the filename of dictionary text
 * the second is the word we are interested in
 */
public class network_size {
    public static void main(String[] args) {
        String filename = args[0];
        String word = args[1];
        ArrayList<String> dic = new ArrayList<String>();
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                dic.add(line.trim());
            }
            in.close();
            long starTime = System.currentTimeMillis();
            int res = size_of_networks(word, dic);
            long endTime = System.currentTimeMillis();
            long runTime = endTime - starTime;
            System.out.println(word + "'s size of network is "+res);
            String timeFormatted = String.format("%d min, %d sec", 
                TimeUnit.MILLISECONDS.toMinutes(runTime),
                TimeUnit.MILLISECONDS.toSeconds(runTime) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime))
            );
            System.out.println("Runtime is " + timeFormatted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //return a word's friends within a dictionary 
    // then remove this word and its friends from the dictionary
    public static ArrayList<String> findFriends(String word, ArrayList<String> dic) {
        ArrayList<String> res = new ArrayList<String>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < dic.size(); ++i) {
            if (dic.get(i).equals(word)) {
                indices.add(i);
            }
            if (editDistance(dic.get(i), word) == 1) {
                res.add(dic.get(i));
                indices.add(i);
            }
        }
        for (int i = 0; i < indices.size(); ++i) {
            dic.remove(indices.get(i) - i);
        }
        return res;
    }

    //recursively return the size of a word's network
    //we could use tail recursion in languages that surpport it to
    //reduce memory usage
    public static int size_of_networks(String word, ArrayList<String> dic) {
        ArrayList<String> friends = findFriends(word, dic);
        int count = 1;
        //if a word has no friend the it network size is 1
        //if not, sum friend's network size
        if (friends.size() != 0) {
            for (String friend : friends) {
                count += size_of_networks(friend, dic);
            }
        }
        return count;
    }

    //compute edit distence of two strings
    //reference: https://www.programcreek.com/2013/12/edit-distance-in-java/
    public static int editDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        return dp[len1][len2];
    }
}