package io.github.lemcoder.mikroaall;

import com.v7878.foreign.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static io.github.lemcoder.mikroaall.NativeHelper.C_INT;
import static io.github.lemcoder.mikroaall.NativeHelper.C_POINTER;


public class AAudio {

    AAudio() {
        // Should not be called directly
    }

    static {
        System.loadLibrary("aaudio");
    }

    private final static Arena ARENA = Arena.ofConfined();
    private final static Linker LINKER = Linker.nativeLinker();

    // <------------------------------------------------------------->
    private final static MethodHandle AAudio_createStreamBuilder = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudio_createStreamBuilder").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static Pointer AAudioCreateStreamBuilder() throws Throwable {
        MemorySegment ptr = ARENA.allocate(C_POINTER);
        int result = (int) AAudio_createStreamBuilder.invokeExact(ptr);
        if (result != AAudioResult.OK.getValue()) {
            throw new RuntimeException("Failed to create AAudio stream builder: " + result);
        }

        return new Pointer(ptr.get(ValueLayout.ADDRESS, 0).address());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setFormat").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    public static void AAudioStreamBuilderSetFormat(Pointer builder, AAudioFormat format) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setFormat.invokeExact(pBuilder, format.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setSampleRate").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    public static void AAudioStreamBuilderSetSampleRate(Pointer builder, int sampleRate) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setSampleRate.invokeExact(pBuilder, sampleRate);
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setChannelCount").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    public static void AAudioStreamBuilderSetChannelCount(Pointer builder, int channelCount) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setChannelCount.invokeExact(pBuilder, channelCount);
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setDirection = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setDirection").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    public static void AAudioStreamBuilderSetDirection(Pointer builder, AAudioAudioDirection direction) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setDirection.invokeExact(pBuilder, direction.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStreamBuilder_setPerformanceMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setPerformanceMode").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    public static void AAudioStreamBuilderSetPerformanceMode(Pointer builder, AAudioPerformanceMode mode) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setPerformanceMode.invokeExact(pBuilder, mode.getValue());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStreamBuilder_setDataCallback = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_setDataCallback").get(),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER, C_POINTER)
    );

    public static void AAudioStreamBuilderSetDataCallback(Pointer builder, AAudioStreamDataCallbackApi callback) throws Throwable {
        // Create a stub as a native symbol to be passed into native function.
        AAudioStreamDataCallbackInternal callbackInternal = (stream, userData, audioData, numFrames) -> {
            if (audioData == null || audioData.equals(MemorySegment.NULL) || audioData.byteSize() == 0) {
                System.err.println("Error: Invalid or empty audioData buffer!");
                return AAudioCallbackResult.CONTINUE.getValue(); // Stop if buffer is unusable
            }

            int numChannels = 2; // Change based on your stream format
            int bytesPerSample = Float.BYTES; // Assuming 32-bit float PCM
            int expectedSize = numFrames * numChannels * bytesPerSample;

            byte[] data = callback.onData(numFrames);

            // Ensure we don't copy beyond available space
            int copySize = Math.min(data.length, expectedSize);

//            System.out.println("Expected size: " + expectedSize + ", Data size: " + data.length + ", Copying: " + copySize);
//            System.out.println("audioData size: " + audioData.byteSize() + ", Expected: " + expectedSize);


            // Copy only valid data
            MemorySegment.copy(data, 0, audioData, ValueLayout.JAVA_BYTE, 0, copySize);

            // Zero out any remaining space if needed
            if (copySize < expectedSize) {
                audioData.asSlice(copySize, expectedSize - copySize).fill((byte) 0);
            }

            return AAudioCallbackResult.CONTINUE.getValue();
        };

        MemorySegment pCallback = createCallbackPtr(callbackInternal);
        MemorySegment nullPtr = MemorySegment.ofAddress(0);

        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());

        AAudioStreamBuilder_setDataCallback.invokeExact(pBuilder, pCallback, nullPtr);
    }

    private static MemorySegment createCallbackPtr(AAudioStreamDataCallbackInternal callback) throws Throwable {
        FunctionDescriptor callbackDescriptor = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,  // aaudio_data_callback_result_t is int
                C_POINTER,   // AAudioStream* (non-null)
                C_POINTER,  // void* userData (nullable)
                C_POINTER,   // void* audioData (non-null)
                ValueLayout.JAVA_INT   // int32_t numFrames
        );

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findVirtual(
                AAudioStreamDataCallbackInternal.class,
                "onData",
                MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class, int.class)
        );

        return LINKER.upcallStub(mh.bindTo(callback), callbackDescriptor, ARENA);
    }

    // <------------------------------------------------------------->
    // STREAM METHODS
    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStreamBuilder_openStream = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStreamBuilder_openStream").get(),
            FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER)
    );

    public static Pointer AAudioStreamBuilderOpenStream(Pointer builder) throws Throwable {
        // dereference the pointer
        MemorySegment pBuilder = MemorySegment.ofAddress(builder.getAddress());
        // allocate a pointer to store the stream
        MemorySegment ptr = ARENA.allocate(C_POINTER);

        int result = (int) AAudioStreamBuilder_openStream.invokeExact(pBuilder, ptr);
        if (result != AAudioResult.OK.getValue()) {
            throw new RuntimeException("Failed to open stream: " + result);
        }
        return new Pointer(ptr.get(ValueLayout.ADDRESS, 0).address());
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getFormat").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioFormat AAudioStreamGetFormat(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return AAudioFormat.fromValue((int) AAudioStream_getFormat.invokeExact(pStream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getChannelCount").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetChannelCount(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return (int) AAudioStream_getChannelCount.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getHardwareChannelCount").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetHardwareChannelCount(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return (int) AAudioStream_getHardwareChannelCount.invokeExact(pStream);
    }


    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDeviceId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getDeviceId").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetDeviceId(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return (int) AAudioStream_getDeviceId.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDirection = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getDirection").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioAudioDirection AAudioStreamGetDirection(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return AAudioAudioDirection.fromValue((int) AAudioStream_getDirection.invokeExact(pStream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getPerformanceMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getPerformanceMode").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioPerformanceMode AAudioStreamGetPerformanceMode(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return AAudioPerformanceMode.fromValue((int) AAudioStream_getPerformanceMode.invokeExact(pStream));
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getSampleRate").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetSampleRate(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return (int) AAudioStream_getSampleRate.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getHardwareSampleRate").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetHardwareSampleRate(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return (int) AAudioStream_getHardwareSampleRate.invokeExact(pStream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getState = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_getState").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioStreamState AAudioStreamGetState(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return AAudioStreamState.fromValue((int) AAudioStream_getState.invokeExact(pStream));
    }

    // ============================================================
    // Stream Control
    // ============================================================


    private final static MethodHandle AAudioStream_requestStart = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().find("AAudioStream_requestStart").get(),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioResult AAudioStreamRequestStart(Pointer stream) throws Throwable {
        // dereference the pointer
        MemorySegment pStream = MemorySegment.ofAddress(stream.getAddress());

        return AAudioResult.fromValue((int) AAudioStream_requestStart.invokeExact(pStream));
    }
}
