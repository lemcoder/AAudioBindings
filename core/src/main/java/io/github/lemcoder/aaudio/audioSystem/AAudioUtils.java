package io.github.lemcoder.aaudio.audioSystem;

import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.C_INT;
import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.C_POINTER;
import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.LINKER;

import com.v7878.foreign.FunctionDescriptor;
import com.v7878.foreign.MemorySegment;
import com.v7878.foreign.SymbolLookup;

import java.lang.invoke.MethodHandle;

import io.github.lemcoder.aaudio.model.AAudioResult;
import io.github.lemcoder.aaudio.model.AAudioStreamState;


public class AAudioUtils {

    AAudioUtils() {
        // Should not be called directly
    }

    public static int AAUDIO_UNSPECIFIED = 0;

    /**
     * The text is the ASCII symbol corresponding to the returnCode,
     * or an English message saying the returnCode is unrecognized.
     * This is intended for developers to use when debugging.
     * It is not for display to users.
     * <p>
     * Available since API level 26.
     *
     * @return pointer to a text representation of an AAudio result code.
     */
    public static String AAudioConvertResultToText(AAudioResult result) throws Throwable {
        MemorySegment pResult = (MemorySegment) AAudio_convertResultToText.invokeExact(result.getValue());

        return pResult.getString(0);
    }

    private final static MethodHandle AAudio_convertResultToText = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudio_convertResultToText"),
            FunctionDescriptor.of(C_POINTER, C_INT)
    );

    /**
     * The text is the ASCII symbol corresponding to the stream state,
     * or an English message saying the state is unrecognized.
     * This is intended for developers to use when debugging.
     * It is not for display to users.
     * <p>
     * Available since API level 26.
     *
     * @return pointer to a text representation of an AAudio state.
     */
    public static String AAudioConvertStreamStateToText(AAudioStreamState state) throws Throwable {
        MemorySegment pResult = (MemorySegment) AAudio_convertStreamStateToText.invokeExact(state.getValue());

        return pResult.getString(0);
    }

    private final static MethodHandle AAudio_convertStreamStateToText = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudio_convertStreamStateToText"),
            FunctionDescriptor.of(C_POINTER, C_INT)
    );
}
