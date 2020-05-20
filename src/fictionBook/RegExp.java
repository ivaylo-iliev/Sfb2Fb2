package fictionBook;

//~--- JDK imports ------------------------------------------------------------

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {
    public boolean regExMatch(String pattern, String srchString) {
        Pattern ptrn  = Pattern.compile(pattern);
        Matcher match = ptrn.matcher(srchString);

        return match.find();
    }

    public String regExReplace(String pattern, String replace, String srchString) {
        Pattern ptrn  = Pattern.compile(pattern);
        Matcher match = ptrn.matcher(srchString);

        return match.replaceAll(replace);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
