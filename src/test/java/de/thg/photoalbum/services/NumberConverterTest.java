package de.thg.photoalbum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberConverterTest {

	Pattern intPattern = null;
	Pattern floatPattern = null;

	@BeforeEach
	public void setUp() {
		intPattern = Pattern.compile("[+-]?[1-9]{1}\\d*");
		floatPattern = Pattern.compile("[+-]?([0-9]*[.])+[0-9]+");
	}

	@Test
	public void test() {
		assertThat(checkForNumber("12345")).isEqualTo(NumberFormat.INT);
		assertThat(checkForNumber("12.345")).isEqualTo(NumberFormat.FLOAT);
		assertThat(checkForNumber("01234")).isEqualTo(NumberFormat.NONE);
	}

	private NumberFormat checkForNumber(String numericString) {
		if (intPattern.matcher(numericString).matches()) {
			return NumberFormat.INT;
		} else if(floatPattern.matcher(numericString).matches()) {
			return NumberFormat.FLOAT;
		}
		return NumberFormat.NONE;
	}

	private enum NumberFormat {
		INT, FLOAT, NONE
	}

}
