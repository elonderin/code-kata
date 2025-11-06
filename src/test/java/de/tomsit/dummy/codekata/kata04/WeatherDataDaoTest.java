package de.tomsit.dummy.codekata.kata04;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeatherDataDaoTest {

  final WeatherDataDao weatherDataDao = new WeatherDataDao();

  @Test
  void getMinSpread() {
    assertThat(weatherDataDao.getMinTempSpread())
        .isEqualTo(2);
  }
}
