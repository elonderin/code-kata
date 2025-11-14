package de.tomsit.dummy.codekata.k06anagrams;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.LazyMap;

@RequiredArgsConstructor
public class AnagramGrouper {

  private final Map<String, SortedSet<String>> anagramMap = LazyMap.lazyMap(new TreeMap<>(), () -> new TreeSet<>());

  public Set<String> addWord(String word) {
    var anagrams = getAnagrams(word);
    anagrams.add(word);

    return anagrams;
  }

  public SortedSet<String> getAnagrams(String word) {
    var anagrams = anagramMap.get(anagramKey(word));
    return anagrams;
  }

  public Stream<SortedSet<String>> stream() {
    return anagramMap.values().stream().map(TreeSet::new);
  }

  private static String anagramKey(String word) {
    var charArray = word.toCharArray();
    Arrays.sort(charArray);
    return new String(charArray);
  }

  public int size() {
    return anagramMap.size();
  }
}
