//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package name.expenses.utils.spring;

import jakarta.annotation.Nullable;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public abstract class ObjectUtils {
    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public ObjectUtils() {
    }

    public static boolean isCheckedException(Throwable ex) {
        return !(ex instanceof RuntimeException) && !(ex instanceof Error);
    }

    public static boolean isCompatibleWithThrowsClause(Throwable ex, @Nullable Class<?>... declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        } else {
            if (declaredExceptions != null) {
                Class[] var2 = declaredExceptions;
                int var3 = declaredExceptions.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    Class<?> declaredException = var2[var4];
                    if (declaredException.isInstance(ex)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isArray(@Nullable Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional)obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection)obj).isEmpty();
        } else {
            return obj instanceof Map ? ((Map)obj).isEmpty() : false;
        }
    }

    @Nullable
    public static Object unwrapOptional(@Nullable Object obj) {
        if (obj instanceof Optional) {
            Optional<?> optional = (Optional)obj;
            if (!optional.isPresent()) {
                return null;
            } else {
                Object result = optional.get();
                Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
                return result;
            }
        } else {
            return obj;
        }
    }

    public static boolean containsElement(@Nullable Object[] array, Object element) {
        if (array == null) {
            return false;
        } else {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object arrayEle = var2[var4];
                if (nullSafeEquals(arrayEle, element)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
        return containsConstant(enumValues, constant, false);
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
        Enum[] var3 = enumValues;
        int var4 = enumValues.length;
        int var5 = 0;

        while(true) {
            if (var5 >= var4) {
                return false;
            }

            Enum<?> candidate = var3[var5];
            if (caseSensitive) {
                if (candidate.toString().equals(constant)) {
                    break;
                }
            } else if (candidate.toString().equalsIgnoreCase(constant)) {
                break;
            }

            ++var5;
        }

        return true;
    }


    public static Object[] toObjectArray(@Nullable Object source) {
        if (source instanceof Object[]) {
            return (Object[])((Object[])source);
        } else if (source == null) {
            return EMPTY_OBJECT_ARRAY;
        } else if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        } else {
            int length = Array.getLength(source);
            if (length == 0) {
                return EMPTY_OBJECT_ARRAY;
            } else {
                Class<?> wrapperType = Array.get(source, 0).getClass();
                Object[] newArray = (Object[])((Object[])Array.newInstance(wrapperType, length));

                for(int i = 0; i < length; ++i) {
                    newArray[i] = Array.get(source, i);
                }

                return newArray;
            }
        }
    }

    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 != null && o2 != null) {
            if (o1.equals(o2)) {
                return true;
            } else {
                return o1.getClass().isArray() && o2.getClass().isArray() ? arrayEquals(o1, o2) : false;
            }
        } else {
            return false;
        }
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[])((Object[])o1), (Object[])((Object[])o2));
        } else if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[])((boolean[])o1), (boolean[])((boolean[])o2));
        } else if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[])((byte[])o1), (byte[])((byte[])o2));
        } else if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[])((char[])o1), (char[])((char[])o2));
        } else if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[])((double[])o1), (double[])((double[])o2));
        } else if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[])((float[])o1), (float[])((float[])o2));
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[])((int[])o1), (int[])((int[])o2));
        } else if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[])((long[])o1), (long[])((long[])o2));
        } else {
            return o1 instanceof short[] && o2 instanceof short[] ? Arrays.equals((short[])((short[])o1), (short[])((short[])o2)) : false;
        }
    }

    public static int nullSafeHashCode(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        } else {
            if (obj.getClass().isArray()) {
                if (obj instanceof Object[]) {
                    return nullSafeHashCode((Object[])((Object[])obj));
                }

                if (obj instanceof boolean[]) {
                    return nullSafeHashCode((boolean[])((boolean[])obj));
                }

                if (obj instanceof byte[]) {
                    return nullSafeHashCode((byte[])((byte[])obj));
                }

                if (obj instanceof char[]) {
                    return nullSafeHashCode((char[])((char[])obj));
                }

                if (obj instanceof double[]) {
                    return nullSafeHashCode((double[])((double[])obj));
                }

                if (obj instanceof float[]) {
                    return nullSafeHashCode((float[])((float[])obj));
                }

                if (obj instanceof int[]) {
                    return nullSafeHashCode((int[])((int[])obj));
                }

                if (obj instanceof long[]) {
                    return nullSafeHashCode((long[])((long[])obj));
                }

                if (obj instanceof short[]) {
                    return nullSafeHashCode((short[])((short[])obj));
                }
            }

            return obj.hashCode();
        }
    }

    public static int nullSafeHashCode(@Nullable Object[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                hash = 31 * hash + nullSafeHashCode(element);
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable boolean[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            boolean[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                boolean element = var2[var4];
                hash = 31 * hash + Boolean.hashCode(element);
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable byte[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            byte[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte element = var2[var4];
                hash = 31 * hash + element;
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable char[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            char[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                char element = var2[var4];
                hash = 31 * hash + element;
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable double[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            double[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                double element = var2[var4];
                hash = 31 * hash + Double.hashCode(element);
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable float[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            float[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                float element = var2[var4];
                hash = 31 * hash + Float.hashCode(element);
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable int[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            int[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                int element = var2[var4];
                hash = 31 * hash + element;
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable long[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            long[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                long element = var2[var4];
                hash = 31 * hash + Long.hashCode(element);
            }

            return hash;
        }
    }

    public static int nullSafeHashCode(@Nullable short[] array) {
        if (array == null) {
            return 0;
        } else {
            int hash = 7;
            short[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                short element = var2[var4];
                hash = 31 * hash + element;
            }

            return hash;
        }
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(boolean bool) {
        return Boolean.hashCode(bool);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(double dbl) {
        return Double.hashCode(dbl);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(float flt) {
        return Float.hashCode(flt);
    }

    /** @deprecated */
    @Deprecated
    public static int hashCode(long lng) {
        return Long.hashCode(lng);
    }

    public static String identityToString(@Nullable Object obj) {
        return obj == null ? "" : obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }

    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static String getDisplayString(@Nullable Object obj) {
        return obj == null ? "" : nullSafeToString(obj);
    }

    public static String nullSafeClassName(@Nullable Object obj) {
        return obj != null ? obj.getClass().getName() : "null";
    }

    public static String nullSafeToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof Object[]) {
            return nullSafeToString((Object[])((Object[])obj));
        } else if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[])((boolean[])obj));
        } else if (obj instanceof byte[]) {
            return nullSafeToString((byte[])((byte[])obj));
        } else if (obj instanceof char[]) {
            return nullSafeToString((char[])((char[])obj));
        } else if (obj instanceof double[]) {
            return nullSafeToString((double[])((double[])obj));
        } else if (obj instanceof float[]) {
            return nullSafeToString((float[])((float[])obj));
        } else if (obj instanceof int[]) {
            return nullSafeToString((int[])((int[])obj));
        } else if (obj instanceof long[]) {
            return nullSafeToString((long[])((long[])obj));
        } else if (obj instanceof short[]) {
            return nullSafeToString((short[])((short[])obj));
        } else {
            String str = obj.toString();
            return str != null ? str : "";
        }
    }

    public static String nullSafeToString(@Nullable Object[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                Object[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object o = var3[var5];
                    stringJoiner.add(String.valueOf(o));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable boolean[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                boolean[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    boolean b = var3[var5];
                    stringJoiner.add(String.valueOf(b));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable byte[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                byte[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    byte b = var3[var5];
                    stringJoiner.add(String.valueOf(b));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable char[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                char[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    char c = var3[var5];
                    stringJoiner.add('\'' + String.valueOf(c) + '\'');
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable double[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                double[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    double d = var3[var5];
                    stringJoiner.add(String.valueOf(d));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable float[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                float[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    float f = var3[var5];
                    stringJoiner.add(String.valueOf(f));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable int[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                int[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    int i = var3[var5];
                    stringJoiner.add(String.valueOf(i));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable long[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                long[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    long l = var3[var5];
                    stringJoiner.add(String.valueOf(l));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable short[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                short[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    short s = var3[var5];
                    stringJoiner.add(String.valueOf(s));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeConciseToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Class) {
            return ((Class)obj).getName();
        } else if (obj instanceof CharSequence) {
            return StringUtils.truncate((CharSequence)obj);
        } else {
            Class<?> type = obj.getClass();
            if (isSimpleValueType(type)) {
                String str = obj.toString();
                if (str != null) {
                    return StringUtils.truncate(str);
                }
            }

            return type.getTypeName() + "@" + getIdentityHexString(obj);
        }
    }

    private static boolean isSimpleValueType(Class<?> type) {
        return Void.class != type && Void.TYPE != type && (ClassUtils.isPrimitiveOrWrapper(type) || Enum.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) || URI.class == type || URL.class == type || Locale.class == type || Class.class == type);
    }
}
