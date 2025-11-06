package de.tomsit.dummy.codekata.karatechop;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class KarateChopRecursiveImplTest {

  private KarateChopRecursiveImpl karateChopRecursive = new KarateChopRecursiveImpl();

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  void testChop_SearchValEqualsIndex(Integer searchVal) {
    assertThat(karateChopRecursive.chop(searchVal, asList(0, 1, 2, 3, 4, 5)))
        .as("the search value is found at the index equal to its value")
        .isEqualTo(searchVal);
  }
}
