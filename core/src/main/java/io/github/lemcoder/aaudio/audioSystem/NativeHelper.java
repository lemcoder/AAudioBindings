package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

class NativeHelper {
    public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    public static boolean isIntegral(MemoryLayout layout) {
        return layout instanceof ValueLayout valueLayout && isIntegral(valueLayout.carrier());
    }

    static boolean isIntegral(Class<?> clazz) {
        return clazz == byte.class || clazz == char.class || clazz == short.class
                || clazz == int.class || clazz == long.class;
    }

    public static boolean isPointer(MemoryLayout layout) {
        return layout instanceof ValueLayout valueLayout && valueLayout.carrier() == MemorySegment.class;
    }

    public static final Linker LINKER = Linker.nativeLinker();

    // the constants below are useful aliases for C types. The type/carrier association is only valid for 64-bit platforms.

    /**
     * The layout for the {@code bool} C type
     */
    public static final ValueLayout.OfBoolean C_BOOL = (ValueLayout.OfBoolean) LINKER.canonicalLayouts().get("bool");
    /**
     * The layout for the {@code char} C type
     */
    public static final ValueLayout.OfByte C_CHAR = (ValueLayout.OfByte) LINKER.canonicalLayouts().get("char");
    /**
     * The layout for the {@code short} C type
     */
    public static final ValueLayout.OfShort C_SHORT = (ValueLayout.OfShort) LINKER.canonicalLayouts().get("short");
    /**
     * The layout for the {@code int} C type
     */
    public static final ValueLayout.OfInt C_INT = (ValueLayout.OfInt) LINKER.canonicalLayouts().get("int");

    /**
     * The layout for the {@code long long} C type.
     */
    public static final ValueLayout.OfLong C_LONG_LONG = (ValueLayout.OfLong) LINKER.canonicalLayouts().get("long long");

    /**
     * The layout for the {@code long long} C type.
     */
    public static final ValueLayout.OfLong C_LONG = (ValueLayout.OfLong) LINKER.canonicalLayouts().get("long");

    /**
     * The layout for the {@code float} C type
     */
    public static final ValueLayout.OfFloat C_FLOAT = (ValueLayout.OfFloat) LINKER.canonicalLayouts().get("float");
    /**
     * The layout for the {@code double} C type
     */
    public static final ValueLayout.OfDouble C_DOUBLE = (ValueLayout.OfDouble) LINKER.canonicalLayouts().get("double");
    /**
     * The {@code T*} native type.
     */
    public static final AddressLayout C_POINTER = ((AddressLayout) LINKER.canonicalLayouts().get("void*"))
            .withTargetLayout(MemoryLayout.sequenceLayout(Long.MAX_VALUE, C_CHAR));
    /**
     * The layout for the {@code size_t} C type
     */
    public static final ValueLayout C_SIZE_T = (ValueLayout) LINKER.canonicalLayouts().get("size_t");

    private static final MethodHandle FREE = LINKER.downcallHandle(
            LINKER.defaultLookup().find("free").get(), FunctionDescriptor.ofVoid(C_POINTER));

    private static final MethodHandle MALLOC = LINKER.downcallHandle(
            LINKER.defaultLookup().find("malloc").get(), FunctionDescriptor.of(C_POINTER, C_LONG_LONG));

    public static void freeMemory(MemorySegment address) {
        try {
            FREE.invokeExact(address);
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static MemorySegment allocateMemory(long size) {
        try {
            return (MemorySegment) MALLOC.invokeExact(size);
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static MemorySegment findNativeOrThrow(String name) {
        return SymbolLookup.loaderLookup().find(name).orElseThrow();
    }

    public static MethodHandle downcallHandle(String symbol, FunctionDescriptor desc, Linker.Option... options) {
        return LINKER.downcallHandle(findNativeOrThrow(symbol), desc, options);
    }

    public static MemorySegment upcallStub(Class<?> holder, String name, FunctionDescriptor descriptor) {
        return upcallStub(holder, name, descriptor, Arena.ofAuto());
    }

    public static MemorySegment upcallStub(Class<?> holder, String name, FunctionDescriptor descriptor, Arena arena) {
        try {
            MethodHandle target = MethodHandles.lookup().findStatic(holder, name, descriptor.toMethodType());
            return LINKER.upcallStub(target, descriptor, arena);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object saver(Object[] o, List<MemoryLayout> argLayouts, AtomicReference<Object[]> ref, SegmentAllocator allocator, int retArg) {
        for (int i = 0; i < o.length; i++) {
            if (argLayouts.get(i) instanceof GroupLayout gl) {
                MemorySegment ms = (MemorySegment) o[i];
                MemorySegment copy = allocator.allocate(gl);
                copy.copyFrom(ms);
                o[i] = copy;
            }
        }
        ref.set(o);
        return retArg != -1 ? o[retArg] : null;
    }
}