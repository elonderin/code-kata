package de.tomsit.dummy.codekata.k05bloom;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.BitSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
public class BloomFilter {

  public static final Path FILE_PATH = Path.of("./data/kata04/wordlist.txt");
  public static final Function<byte[], int[]> TO_INTS_CONVERTER = BloomFilter::toInts;

  private static int[] toInts(byte[] bytes) {
    Assert.isTrue(bytes.length >= 4, "given byte[] must be at least of size 4 but was: " + bytes.length);

    var hashes = new int[bytes.length / 4];
    if (hashes.length * 4 < bytes.length) {
      log.warn("given byte[] size is not a multiple of 4 ({}). Higher bytes are ignored.", bytes.length);
    }

    for (int i = 0; i < hashes.length; i++) {
      hashes[i] |= (bytes[i + 0] & 0xFF) << 24;
      hashes[i] |= (bytes[i + 1] & 0xFF) << 16;
      hashes[i] |= (bytes[i + 2] & 0xFF) << 8;
      hashes[i] |= (bytes[i + 3] & 0xFF);
    }
    return hashes;
  }

  private final BitSet bits = new BitSet(64 * 1024);

  private final String algorithm = "SHA-256";


  @SneakyThrows
  public void readDictionary() {

    try (var lines = Files.lines(FILE_PATH)) {
      lines.flatMapToInt(this::calcHashes)
           .forEach(bits::set);
    }

  }

  @SneakyThrows
  private IntStream calcHashes(String s) {
    var digester = MessageDigest.getInstance(algorithm);
    var bytes = digester.digest(s.getBytes());

    var hashes = TO_INTS_CONVERTER.apply(bytes);

    return IntStream.of(hashes);
  }

}
