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

    @Test
    void severalInts() {
      assertThat(TO_INTS_CONVERTER.apply(new byte[]{
          0x00, 0x00, 0x00, 0x01,
          0x00, 0x00, 0x01, 0x00,
          0x00, 0x01, 0x00, 0x00,
          0x01, 0x00, 0x00, 0x00,
          0x04, 0x03, 0x02, 0x01
      }))
          .contains(
              0x0_0000_0001,
              0x0_0000_0100,
              0x0_0001_0000,
              0x0_0100_0000,
              0x0_0403_0201);
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
    var HEX_WORD_LENGTH = 6;
    hexStr = cleanse(hexStr);

    var ints = new int[hexStr.length() / HEX_WORD_LENGTH];
    for (int i = 0; i < ints.length; i++) {
      ints[i] = (int) Long.parseLong(hexStr.substring(i * HEX_WORD_LENGTH, (i + 1) * HEX_WORD_LENGTH), 16);
    }
    return ints;
  }

  private String cleanse(String bytesStr) {
    bytesStr = bytesStr.replace(" ", "");
    Assert.isTrue(bytesStr.length() % 2 == 0, "bytes has an uneven length:" + bytesStr.length());
    return bytesStr;
  }


}
