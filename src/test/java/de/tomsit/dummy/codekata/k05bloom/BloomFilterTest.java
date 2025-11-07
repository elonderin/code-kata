package de.tomsit.dummy.codekata.k05bloom;

import static de.tomsit.dummy.codekata.k05bloom.BloomFilter.TO_INTS_CONVERTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.Assert;

class BloomFilterTest {

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


}
