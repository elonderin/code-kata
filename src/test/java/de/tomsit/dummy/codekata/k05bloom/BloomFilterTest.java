package de.tomsit.dummy.codekata.k05bloom;

import static de.tomsit.dummy.codekata.k05bloom.BloomFilter.TO_INTS_CONVERTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BloomFilterTest {

  BloomFilter bloomFilter = new BloomFilter();


  @Nested
  class ToIntConverter {

    @Test
    void toSmall_YieldsIAEx() {
      assertThatThrownBy(() -> TO_INTS_CONVERTER.apply(new byte[]{1}))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void singleCorrect() {
      assertThat(TO_INTS_CONVERTER.apply(new byte[]{0, 0, 0x01, 0}))
          .hasSize(1)
          .contains(0x100);
    }

//    @ValueSource(bytes = {0x00, 0x00, 0x00, 0x01})
    @Test
    void twoCorrect() {
      Integer.parseInt()
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


}
