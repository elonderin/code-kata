package de.tomsit.dummy.codekata.k05bloom;

import java.security.MessageDigest;
import java.util.BitSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class BloomFilter {

  public static final int INDEX_BYTES = 3;
  public static final Function<byte[], int[]> TO_INTS_CONVERTER = BloomFilter::toInts;

  private static int[] toInts(byte[] bytes) {
    Assert.isTrue(bytes.length >= INDEX_BYTES,
                  "given byte[] must be at least of size %d but was: %d ".formatted(INDEX_BYTES, bytes.length));

    var hashes = new int[bytes.length / INDEX_BYTES];
    if (hashes.length * INDEX_BYTES < bytes.length) {
      log.debug("given byte[] size is not a multiple of {} ({}). Bytes at the end are ignored.", INDEX_BYTES, bytes.length);
    }
    for (int i = 0; i < hashes.length; i++) {
      for (int j = 0; j < INDEX_BYTES; j++) {
        hashes[i] |= (bytes[i * INDEX_BYTES + j] & 0xFF) << (INDEX_BYTES - j - 1) * 8;
      }
    }
    return hashes;
  }

  // -- static end

  private int hashCount = 4;
  private final String algorithm = "SHA-256";

  private final BitSet bits = new BitSet((1 << (INDEX_BYTES * 8)) - 1);
  private MutableInt counter = new MutableInt();

  @SneakyThrows
  public void insert(Stream<String> value) {
    counter.setValue(0);

    value
        .flatMapToInt(this::calcHashes)
        .peek(x -> counter.increment())
        .forEach(bits::set);

    value.close();
  }

  public boolean contains(String item) {
    return calcHashes(item)
        .allMatch(bits::get);
  }

  void logInfo() {
    log.info("words read: " + getInserts());
    log.info("bits set: " + getSetOperations());
  }

  public Number getSetOperations() {
    return counter.get();
  }

  public int getInserts() {
    return counter.get().intValue() / hashCount;
  }

  @SneakyThrows
  private IntStream calcHashes(String s) {
    var digester = MessageDigest.getInstance(algorithm);
    var bytes = digester.digest(s.getBytes());

    var hashes = TO_INTS_CONVERTER.apply(bytes);

    return IntStream.of(hashes).limit(hashCount);
  }

}
