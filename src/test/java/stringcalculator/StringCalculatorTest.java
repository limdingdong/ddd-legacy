package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * 요구사항
 *  - 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환
 *      - (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 *  - 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.
 *      - 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.
 *      - 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 *  - 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
class StringCalculatorTest {

    @DisplayName("빈 문자열 또는 null 을 입력할 경우 0을 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmptyString(String input) {
        assertThat(StringCalculator.add(input)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2", "3", "4", "5"})
    void singleInput(String input) {
        assertThat(StringCalculator.add(input)).isEqualTo(Integer.parseInt(input));
    }

    @DisplayName("숫자 두개를 comma 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void commaDelimiter(final String text) {
        assertThat(StringCalculator.add(text)).isEqualTo(3);
    }

    @DisplayName("구분자를 comma 이외에 colon 을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void commaColonDelimiter(final String text) {
        assertThat(StringCalculator.add(text)).isEqualTo(6);
    }

    @DisplayName("\"//\"와 \"\\n\" 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(StringCalculator.add(text)).isEqualTo(6);
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> StringCalculator.add("-1"));
    }
}
