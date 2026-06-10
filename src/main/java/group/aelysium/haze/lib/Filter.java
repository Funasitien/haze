package group.aelysium.haze.lib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Filter {
    private int pointer = -1;
    private final List<KeyValue<String, Value>> filters = new Vector<>();
    private final List<Operator> operators = new Vector<>();
    
    private Filter(@NotNull KeyValue<String, Value> initialFilter) {
        this.filters.add(initialFilter);
    }
    
    protected void add(@NotNull Filter.Operator operator, @NotNull KeyValue<String, Value> filter) {
        this.operators.add(operator);
        this.filters.add(filter);
    }
    
    public Filter AND(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        this.add(Operator.AND, new KeyValue<>(key, new Value(value, equality)));
        return this;
    }
    public Filter OR(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        this.add(Operator.OR, new KeyValue<>(key, new Value(value, equality)));
        return this;
    }
    public Filter AND_NOT(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        this.add(Operator.AND_NOT, new KeyValue<>(key, new Value(value, equality)));
        return this;
    }
    public Filter OR_NOT(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        this.add(Operator.OR_NOT, new KeyValue<>(key, new Value(value, equality)));
        return this;
    }
    public Filter XOR(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        this.add(Operator.EXCLUSIVE_OR, new KeyValue<>(key, new Value(value, equality)));
        return this;
    }
    
    /**
     * This method exists for Haze implementors.
     * Navigates the internal pointer to the next value.
     * In order to read the value the pointer is pointing to, call {@link #get()}.
     */
    public boolean next() {
        if(this.pointer >= this.filters.size() - 1) {
            return false;
        }
        this.pointer++;
        return true;
    }
    
    
    public void resetPointer() {
        this.pointer = -1;
    }
    
    /**
     * @return The value being pointed to by the internal pointer.
     *         On the first entry in the filter, the JoinOperator will be null.
     * @throws IndexOutOfBoundsException If the pointer has moved out of the bounds of the dataset.
     */
    public @NotNull KeyValue<@Nullable Operator, @NotNull KeyValue<String, Value>> get() throws IndexOutOfBoundsException {
        Operator operator = null;
        try {
            operator = this.operators.get(this.pointer - 1);
        } catch (Exception ignore) {}
        
        KeyValue<String, Value> value = this.filters.get(this.pointer);
        
        return new KeyValue<>(operator, value);
    }
    
    /**
     * Starts a new Filter chain with the provided key/value as a starting point.
     * @param key The key to filter.
     * @param value The value to filter with.
     * @return A new Filter.
     */
    public static Filter by(@NotNull String key, @NotNull Object value, @NotNull Qualifier equality) {
        return new Filter(new KeyValue<>(key, new Value(value, equality)));
    }
    
    public record Value(Object value, Qualifier equality) {}
    public enum Operator {
        AND,
        OR,
        AND_NOT,
        OR_NOT,
        EXCLUSIVE_OR
    }
    public record Qualifier(String value) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Qualifier qualifier = (Qualifier) o;
            return Objects.equals(value, qualifier.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    /**
     * Validates that the two values are equal.
     */
    public static final Qualifier EQUALS = new Qualifier("EQUALS");

    /**
     * Validates that the two values are not equal.
     */
    public static final Qualifier NOT_EQUALS = new Qualifier("NOT_EQUALS");

    /**
     * Validates that the original value contains the passed value.
     * This option is only valid for string data types.
     * If this is used with any other type, the behavior is undefined.
     */
    public static final Qualifier CONTAINS = new Qualifier("CONTAINS");

    /**
     * Validates that the original value does not contain the passed value.
     * This option is only valid for string data types.
     * If this is used with any other type, the behavior is undefined.
     */
    public static final Qualifier NOT_CONTAINS = new Qualifier("NOT_CONTAINS");

    /**
     * Validates that the passed value is greater than the original value.
     */
    public static final Qualifier GREATER_THAN = new Qualifier("GREATER_THAN");

    /**
     * Validates that the passed value is less than the original value.
     */
    public static final Qualifier LESS_THAN = new Qualifier("LESS_THAN");

    /**
     * Validates that the passed value is greater than or equal to the original value.
     */
    public static final Qualifier GREATER_THAN_OR_EQUAL = new Qualifier("GREATER_THAN_OR_EQUAL");

    /**
     * Validates that the passed value is less than or equal the original value.
     */
    public static final Qualifier LESS_THAN_OR_EQUAL = new Qualifier("LESS_THAN_OR_EQUAL");

    /**
     * Validates that the passed value is null.
     */
    public static final Qualifier IS_NULL = new Qualifier("IS_NULL");

    /**
     * Validates that the passed value is not null.
     */
    public static final Qualifier IS_NOT_NULL = new Qualifier("IS_NOT_NULL");
}
