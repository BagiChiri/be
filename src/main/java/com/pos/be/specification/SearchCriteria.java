//package com.pos.be.specification;
//
//import lombok.Getter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Getter
//public class SearchCriteria {
//    private final String key;
//    private final String operation;
//    private final List<String> values;
//
//    public SearchCriteria(String key, String operation, List<String> values) {
//        this.key = key;
//        this.operation = operation;
//        this.values = values;
//    }
//
//    public static List<SearchCriteria> fromFilterMap(Map<String, String> filters) {
//        List<SearchCriteria> criteriaList = new ArrayList<>();
//        Pattern pattern = Pattern.compile("(.+?)([!><=~]{1,2})(.+)");
//
//        for (Map.Entry<String, String> entry : filters.entrySet()) {
//            String key = entry.getKey();
//            String rawValue = entry.getValue();
//
//            Matcher matcher = pattern.matcher(key + rawValue);
//            if (matcher.find()) {
//                String field = matcher.group(1);
//                String op = matcher.group(2);
//                List<String> values = List.of(matcher.group(3).split(";"));
//                criteriaList.add(new SearchCriteria(field, op, values));
//            } else {
//                criteriaList.add(new SearchCriteria(key, "=", List.of(rawValue)));
//            }
//        }
//        return criteriaList;
//    }
//
//}
package com.pos.be.specification;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class SearchCriteria {
    private final String key;
    private final String operation;
    private final List<String> values;

    public SearchCriteria(String key, String operation, List<String> values) {
        this.key = key;
        this.operation = operation;
        this.values = values;
    }

    public static List<SearchCriteria> fromFilterMap(Map<String, String> filters, Class<?> entityClass) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        Pattern opPattern = Pattern.compile("(.*?)([!><=~]{1,2})?$"); // Support optional ops in keys

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String keyWithOp = entry.getKey();
            String rawValue = entry.getValue();

            Matcher matcher = opPattern.matcher(keyWithOp);
            if (!matcher.matches()) continue;

            String field = matcher.group(1);
            String op = matcher.group(2) != null ? matcher.group(2) : "=";

            if (!fieldExistsInEntity(entityClass, field)) {
                continue;
            }

            List<String> values = List.of(rawValue.split(";"));
            criteriaList.add(new SearchCriteria(field, op, values));
        }

        return criteriaList;
    }


    // Utility method to check if a field exists in the given entity class
    private static boolean fieldExistsInEntity(Class<?> entityClass, String fieldName) {
        try {
            // Attempt to get the field using reflection
            Field field = entityClass.getDeclaredField(fieldName);
            return field != null;
        } catch (NoSuchFieldException e) {
            // If the field doesn't exist, return false
            return false;
        }
    }
}
