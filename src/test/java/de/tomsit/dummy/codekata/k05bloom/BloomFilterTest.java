package de.tomsit.dummy.codekata.k05bloom;

import static de.tomsit.dummy.codekata.k05bloom.BloomFilter.TO_INTS_CONVERTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

@ExtendWith(RandomBeansExtension.class)
class BloomFilterTest {

  public static final Path FILE_PATH = Path.of("./data/kata05/wordlist.txt");
  private static final Logger log = LoggerFactory.getLogger(BloomFilterTest.class);
  BloomFilter bloomFilter = new BloomFilter();


  @Nested
  class ToIntConverter {

    @Test
    void toSmall_YieldsIAEx() {
      assertThatThrownBy(() -> TO_INTS_CONVERTER.apply(new byte[]{1}))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        # hex
        00 0000
        00 0001
        00 0021
        00 0A00
        00 0301
        00 00ff
        00 ffff
        ff ffff
        """, delimiter = '|')
    void singleWord_Conversions(String bytesStr) {
      var bytes = parseAsBytes(bytesStr);

      assertThat(TO_INTS_CONVERTER.apply(bytes))
          .hasSize(1)
          .contains(parseAsInts(bytesStr));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        # hex bytes, 2 words
        00 0001 00 0A00
        ff ffff ff ffff
        """, delimiter = '|')
    void multiWord_Conversions(String bytesStr) {
      var bytes = parseAsBytes(bytesStr);

      assertThat(TO_INTS_CONVERTER.apply(bytes))
          .contains(parseAsInts(bytesStr));
    }
  }

  private byte[] parseAsBytes(String hexStr) {
    var HEX_WORD_LENGTH = 2;
    hexStr = cleanse(hexStr);

    var ints = new byte[hexStr.length() / HEX_WORD_LENGTH];
    for (int i = 0; i < ints.length; i++) {
      ints[i] = (byte) Long.parseLong(hexStr.substring(i * HEX_WORD_LENGTH, (i + 1) * HEX_WORD_LENGTH), 16);
    }
    return ints;
  }

  private int[] parseAsInts(String hexStr) {
    var HEX_WORD_LENGTH = BloomFilter.INDEX_BYTES * 2;
    hexStr = cleanse(hexStr);

    var ints = new int[hexStr.length() / HEX_WORD_LENGTH];
    for (int i = 0; i < ints.length; i++) {
      var substring = hexStr.substring(i * HEX_WORD_LENGTH, (i + 1) * HEX_WORD_LENGTH);
      ints[i] = (int) Long.parseLong(substring, 16);
    }
    return ints;
  }

  private String cleanse(String bytesStr) {
    bytesStr = bytesStr.replace(" ", "");
    Assert.isTrue(bytesStr.length() % 2 == 0, "bytes has an uneven length:" + bytesStr.length());
    return bytesStr;
  }

  @Test
  void testContains(@Random(type = String.class) List<String> entries) {
    bloomFilter.insertItems(entries.stream());

    var softly = new SoftAssertions();
    entries.forEach(
        e -> softly.assertThat(bloomFilter.contains(e))
                   .as("value: " + e)
                   .isTrue()
    );

    softly.assertAll();

  }


}
