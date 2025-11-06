package de.tomsit.dummy.codekata.karatechop;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomBeansExtension.class)
class KarateChopRecursiveImplTest {

  private KarateChopRecursiveImpl karateChopRecursive = new KarateChopRecursiveImpl();

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5})
  void testChop_Found_SearchValEqualsIndex(Integer searchVal) {
    assertThat(karateChopRecursive.chop(searchVal, asList(0, 1, 2, 3, 4, 5)))
        .as("the search value is found at the index equal to its value")
        .isEqualTo(searchVal);
  }

  @Test
  void testChop_Found_SingleList(@Random Integer searchVal) {
    assertThat(karateChopRecursive.chop(searchVal, Collections.singletonList(searchVal)))
        .as("the search value is always found at index 0")
        .isZero();
  }

  @Test
  void testChop_Found_RandomValue(@Random(type = Integer.class) List<Integer> list) {

    list.sort(Comparator.naturalOrder());

    //insert the search value at pos to have a deterministic index
    @SuppressWarnings("deprecation")
    var expectedIndex = RandomUtils.nextInt(0, list.size());
    var searchVal = list.get(expectedIndex);

    assertThat(karateChopRecursive.chop(searchVal, list))
        .as("the search value [%d] should have been found @ [%d] in array %s", searchVal, expectedIndex, list)
        .isEqualTo(expectedIndex);
  }


  @ParameterizedTest
  @ValueSource(ints = {-1, 6})
  void testChop_NotFound_SearchValue(Integer searchVal) {
    assertThat(karateChopRecursive.chop(searchVal, asList(0, 1, 2, 3, 4, 5)))
        .as("null list always results in -1")
        .isEqualTo(-1);
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  void testChop_NotFound_EmptyList(List<Integer> list) {
    assertThat(karateChopRecursive.chop(47, list))
        .as("empty list always results in -1")
        .isEqualTo(-1);
  }

  @ParameterizedTest
  @CsvSource(textBlock = """
      # list    | expectedIndexes | comment
      3 3 7 8 9 | 0 1             | left edge
      1 3 3 7   | 1 2             | in the middle
      0 1 2 3 3 | 4 5             | right edge
      """, delimiter = '|')
  void testChop_Found_InListWithDups(String listStr, String expectedIndexesStr, String comment) {

    assertThat(karateChopRecursive.chop(3, toList(listStr)))
        .as("any index where the element is found is valid (arbitrarily choosing the first found occurrence)")
        .satisfies(index ->
                       assertThat(index).isIn(toList(expectedIndexesStr))
        );
  }

  private List<Integer> toList(String listStr) {
    return Arrays.stream(listStr.split(" "))
                 .map(Integer::parseInt)
                 .toList();
  }


}
