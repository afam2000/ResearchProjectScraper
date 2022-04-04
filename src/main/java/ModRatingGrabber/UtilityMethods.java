package ModRatingGrabber;

public class UtilityMethods {
    static String findValueAt(String target, String haystack,int n,int offset) //n is desired amount of characters in returned string
    {                                                                      //offset to find value near target word
        int start = haystack.indexOf(target)+offset;
        int end = start+n;
        return  haystack.substring(start,end);
    }
}
