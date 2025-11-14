package de.tomsit.dummy.codekata.k04data_munging;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class WeatherDataDao {

  public record DayWeatherData(
      int day,
      int maxTemp,
      int minTemp
  ) {

    public int spread() {
      return maxTemp - minTemp;
    }
  }

  public int getMinTempSpread() {
    var dayWeatherDataSet = readDayData("/kata04/weather.dat");

    var minSpread = dayWeatherDataSet.stream()
                                     .min(Comparator.comparing(DayWeatherData::spread));

    return minSpread.orElseThrow().spread();
  }

  @SneakyThrows
  public Set<DayWeatherData> readDayData(String resourcePath) {
    try (var is = this.getClass().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new FileNotFoundException(resourcePath);
      }

      return IOUtils.readLines(is, StandardCharsets.UTF_8)
                    .stream()
                    /* shortcut: i should really do fixed column width splitting here,
                     but to read the 1st 3 vals in this case, this is simpler */
                    .map(s -> StringUtils.split(s, " "))
                    .filter(this::isDayLine)
                    .map(this::toWeatherData)
                    .collect(Collectors.toSet());

    }

  }

  private boolean isDayLine(String[] strVals) {
    return strVals.length > 0 && StringUtils.isNumeric(strVals[0]);
  }

  private DayWeatherData toWeatherData(String[] strVals) {
    return new DayWeatherData(stripInvalidCharToInt(strVals[0]),
                              stripInvalidCharToInt(strVals[1]),
                              stripInvalidCharToInt(strVals[2])
    );
  }

  private int stripInvalidCharToInt(String strVal) {
    return Integer.parseInt(strVal.replace("*", ""));
  }


}
