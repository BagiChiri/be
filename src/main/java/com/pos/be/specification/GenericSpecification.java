package com.pos.be.specification;

import com.pos.be.entity.order.Consignment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericSpecification<T> implements Specification<T> {

    private final List<SearchCriteria> criteriaList;

    public GenericSpecification(Map<String, String> filters, Class<T> specClass) {
        this.criteriaList = SearchCriteria.fromFilterMap(filters, specClass);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> combinedPredicates = new ArrayList<>();

        for (SearchCriteria criteria : criteriaList) {
            Path<?> path = getPath(root, criteria.getKey());
            Class<?> type = path.getJavaType();

            List<Predicate> innerPredicates = criteria.getValues().stream()
                    .map(val -> buildPredicate(cb, path, criteria.getOperation(), convertValue(val, type)))
                    .toList();

            Predicate combined;
            if (innerPredicates.size() == 1) {
                combined = innerPredicates.get(0);
            } else {
                combined = cb.or(innerPredicates.toArray(new Predicate[0])); // OR for multi-values
            }

            combinedPredicates.add(combined);
        }

        return cb.and(combinedPredicates.toArray(new Predicate[0]));
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Path<?> path, String op, Object value) {
        return switch (op) {
            case "=" -> cb.equal(path, value);
            case "!=" -> cb.notEqual(path, value);
            case ">" -> cb.greaterThan(path.as(Comparable.class), (Comparable) value);
            case "<" -> cb.lessThan(path.as(Comparable.class), (Comparable) value);
            case ">=" -> cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
            case "<=" -> cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) value);
            case "~" -> cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%");
            default -> throw new IllegalArgumentException("Unknown operation: " + op);
        };
    }

    private Path<?> getPath(From<?, ?> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Path<?> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }

    private Object convertValue(String value, Class<?> type) {
        try {
            if (Enum.class.isAssignableFrom(type)) {
                return Enum.valueOf((Class<Enum>) type, value.toUpperCase());
            } else if (type == Boolean.class || type == boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (type == Integer.class || type == int.class) {
                return Integer.parseInt(value);
            } else if (type == Long.class || type == long.class) {
                return Long.parseLong(value);
            } else if (type == Double.class || type == double.class) {
                return Double.parseDouble(value);
            } else if (type == Float.class || type == float.class) {
                return Float.parseFloat(value);
            } else if (type == LocalDate.class) {
                return LocalDate.parse(value);
            } else if (type == LocalDateTime.class) {
                return LocalDateTime.parse(value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid value [" + value + "] for type: " + type.getSimpleName(), e);
        }
        return value;
    }
}
