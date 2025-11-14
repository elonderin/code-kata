package de.tomsit.dummy.codekata.k06anagrams;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

class SimpleParsers {

  public static List<String> parseToStrings(String inside) {
    return List.of(StringUtils.split(inside));
  }

  public static List<Set<String>> parseGroups(String input) {
    List<Set<String>> result = new ArrayList<>();

    // Regex to match each `{...}` group
    Pattern groupPattern = Pattern.compile("\\{([^}]*)\\}");
    Matcher matcher = groupPattern.matcher(input);

    while (matcher.find()) {
      String inside = matcher.group(1).trim(); // content inside {}
      if (!inside.isEmpty()) {
        // Split by whitespace
        Set<String> set = new LinkedHashSet<>(parseToStrings(inside));
        result.add(set);
      }
    }

    return result;
  }

}
