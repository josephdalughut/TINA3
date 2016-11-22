package ng.edu.aun.tina3.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Value extends com.litigy.lib.java.util.Value {

    public static class CLONE {

        public static <T> List<T> list(List<T> listToClone){
            List<T> clone = new ArrayList<>();
            if(!IS.emptyValue(listToClone))
                for(T t: listToClone)
                    clone.add(t);
            return clone;
        }
    }

    public static class RandomComparator<T> implements Comparator<T>{
        @Override
        public int compare(T o1, T o2) {
            return Math.random() >= 0.5 ? 1 : 0;
        }
    }

    public static class IS extends com.litigy.lib.java.util.Value.IS{
        public static class VALID extends com.litigy.lib.java.util.Value.IS.VALID{
            public static boolean aunId(String string){
                String regex = "[a-zA-Z]\\d{8}";
                Pattern pattern = Pattern.compile(regex);
                return pattern.matcher(string).matches();
            }
        }
    }
}
