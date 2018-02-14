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
        //divide the dictionaty into sub dictionaries, based on word's length
        //dividedDic's index is word's length-1, each sub dictionary is an ArrayList<String> of words wit same length
        ArrayList<ArrayList<String>> dividedDic = new ArrayList<ArrayList<String>>();
        //start with just one subdictionary with word length of 1
        dividedDic.add(new ArrayList<String>());
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            int maxLength = 0;
            while ((line = in.readLine()) != null) {
                int len = line.trim().length();
                //put words into sub dictionaries based on their length
                //create new subdictionaries if needed
                if (len - 1 > maxLength) {
                    for (int i = maxLength; i < len; ++i) {
                        dividedDic.add(new ArrayList<String>());
                    }
                    maxLength = dividedDic.size() - 1;
                }
                dividedDic.get(len - 1).add(line.trim());
            }
            in.close();
            long starTime = System.currentTimeMillis();
            int res = size_of_networks(word, dividedDic);
            long endTime = System.currentTimeMillis();
            long runTime = endTime - starTime;
            System.out.println(word + "'s size of network is " + res);
            String timeFormatted = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(runTime),
                    TimeUnit.MILLISECONDS.toSeconds(runTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runTime)));
            System.out.println("Runtime is " + timeFormatted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //return a word's friends within a divided dictionary
    //because if word a and word b are friends, |a.length() - b.length()| <= 1
    //so we only have to lookup for friends in only 3 subdictionaries at most
    public static ArrayList<String> findFriends(String word, ArrayList<ArrayList<String>> dividedDic) {
        ArrayList<String> res;
        int len = word.length();
        int maxLength = dividedDic.size() - 1;
        if (len > 1 && (len - 1) < maxLength) {
            ArrayList<String> r1 = _findFriendsIndic(word, dividedDic.get(len - 2));
            ArrayList<String> r2 = _findFriendsIndic(word, dividedDic.get(len - 1));
            ArrayList<String> r3 = _findFriendsIndic(word, dividedDic.get(len));
            r1.addAll(r2);
            r1.addAll(r3);
            res = r1;
        } else if (len == 1) {
            ArrayList<String> r2 = _findFriendsIndic(word, dividedDic.get(len - 1));
            ArrayList<String> r3 = _findFriendsIndic(word, dividedDic.get(len));
            r2.addAll(r3);
            res = r2;
        } else {
            ArrayList<String> r1 = _findFriendsIndic(word, dividedDic.get(len - 2));
            ArrayList<String> r2 = _findFriendsIndic(word, dividedDic.get(len - 1));
            r1.addAll(r2);
            res = r1;
        }
        return res;
    }

    //return a word's friends within a subdictionary 
    // then remove this word and its friends from the subdictionary
    private static ArrayList<String> _findFriendsIndic(String word, ArrayList<String> dic) {
        ArrayList<String> res = new ArrayList<String>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < dic.size(); ++i) {
            if (dic.get(i).equals(word)) {
                indices.add(i);
            }
            if (_isOneEditDistance(dic.get(i), word)) {
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
    public static int size_of_networks(String word, ArrayList<ArrayList<String>> dividedDic) {
        ArrayList<String> friends = findFriends(word, dividedDic);
        int count = 1;
        //if a word has no friend the it network size is 1
        //if not, sum friend's network size
        if (friends.size() != 0) {
            for (String friend : friends) {
                count += size_of_networks(friend, dividedDic);
            }
        }
        return count;
    }

    //calculate if two words have edit distence of 1
    //reference: https://www.programcreek.com/2014/05/leetcode-one-edit-distance-java/
    private static boolean _isOneEditDistance(String s, String t) {
        if (s == null || t == null)
            return false;

        int m = s.length();
        int n = t.length();

        if (Math.abs(m - n) > 1) {
            return false;
        }

        int i = 0;
        int j = 0;
        int count = 0;

        while (i < m && j < n) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
                j++;
            } else {
                count++;
                if (count > 1)
                    return false;

                if (m > n) {
                    i++;
                } else if (m < n) {
                    j++;
                } else {
                    i++;
                    j++;
                }
            }
        }

        if (i < m || j < n) {
            count++;
        }

        if (count == 1)
            return true;

        return false;
    }
}