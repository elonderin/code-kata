package de.tomsit.dummy.codekata.k02karatechop;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;

public class KarateChopRecursiveImpl implements KarateChop {


  @Override
  public int chop(Integer search, List<Integer> values) {

    if (isEmpty(values)) {// needed in case an null/empty array is given
      return -1;
    } else if (values.size() == 1) {
      return values.getFirst().equals(search) ? 0 : -1;
    } else {
      var middle = values.size() / 2;
      var val = values.get(middle);
      if (search.equals(val)) {
        return middle;
      } else if (search < val) {
        return chop(search, values.subList(0, middle));
      } else {
        var offset = middle + 1;
        var result = chop(search, values.subList(offset, values.size()));
        return result == -1 ? -1 : offset + result;
      }
    }


  }


}
