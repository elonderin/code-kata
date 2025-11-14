package de.tomsit.dummy.codekata.k06anagrams;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
@ExtendWith(RandomBeansExtension.class)
class AnagramGrouperTest {

  AnagramGrouper anagramGrouper = new AnagramGrouper();

  @ParameterizedTest
  @CsvSource(textBlock = """
      # words        | result                | comments
      ab             | {ab}                  | trivial case
      ab ab          | {ab}                  | distinct same word behavior
      ab ba          | {ab ba}               | simple anagram
      ab ac          | {ab} {ac}             | 2 different groups
      ab ba ac ca cd | {ab ba } {ac ca} {cd} | 3 different groups
      """, delimiter = '|')
  void singleResult_Cases(String givenWordsStr, String expectedResultStr) {
    var gWords = SimpleParsers.parseToStrings(givenWordsStr);
    var expectedResult = SimpleParsers.parseGroups(expectedResultStr);

    gWords.forEach(anagramGrouper::addWord);

    gWords.forEach(word -> {
      var expectedAnagramList = expectedResult.stream()
                                              .filter(group -> group.contains(word))
                                              .findFirst()
                                              .orElseThrow(() -> new IllegalStateException("no expected result for word " + word));
      assertThat(anagramGrouper.getAnagrams(word))
          .containsExactlyElementsOf(expectedAnagramList);
    });

  }


  @Nested
  class RealWordListTests {

    public static final Path FILE_PATH = Path.of("./data/kata05/wordlist.txt");

    @Test
    void stats(@Random(type = String.class, size = 500 * 1000) List<String> testWords) throws Exception {
      var start = System.currentTimeMillis();

      Files.lines(FILE_PATH).forEach(anagramGrouper::addWord);

      var sum = new MutableInt(0);
      var max = new MutableInt(0);
      var anagramSets = anagramGrouper.stream().filter(t -> t.size() > 1)
                                      .peek(t -> sum.add(t.size()))
                                      .peek(t -> max.setValue(Math.max(max.get().intValue(), t.size())))
                                      .count();

      var end = System.currentTimeMillis();
      log.info("""
                   words read: {} in {} ms
                   anagram sets: {} tototal anagrams: {}
                   max anagrams in a set: {}
                   """,
               anagramGrouper.size(), end - start
          , anagramSets, sum.get()
          , max.get()

      );

      SimpleParsers.parseToStrings("""
                                       crepitus
                                       paste
                                       punctilio
                                       sunders
                                       """).forEach(word -> {
        log.info("anagrams for {}: {}", word, anagramGrouper.getAnagrams(word));
      });

    }
  }

}
