package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.AddressLayout;
import com.v7878.foreign.Linker;
import com.v7878.foreign.MemoryLayout;
import com.v7878.foreign.ValueLayout;

class NativeHelper {

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
    // Note: Depending on the bit depth of the system, the long type from the C language can be equivalent to JAVA_INT or JAVA_LONG
    public static final ValueLayout C_LONG = (ValueLayout.OfLong) LINKER.canonicalLayouts().get("long");

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
    // Note: Depending on the bit depth of the system, the size_t type from the C language can be equivalent to JAVA_INT or JAVA_LONG
    public static final ValueLayout C_SIZE_T = (ValueLayout) LINKER.canonicalLayouts().get("size_t");
}