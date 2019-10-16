package exSelenide;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.LogEventListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ResultsUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class AllureSelenideListener implements LogEventListener {

    private final List<EventFormatter> formatters = getDefaultFormatters();

    private final AllureLifecycle lifecycle;

    public AllureSelenideListener() {
        this(Allure.getLifecycle());
    }

    public AllureSelenideListener(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void onEvent(LogEvent event) {
        getFormatter(event).ifPresent(formatter -> {
            String title = formatter.format(event);
            final String stepUUID = UUID.randomUUID().toString();
            lifecycle.startStep(stepUUID, new StepResult().withName(title).withStatus(Status.PASSED));
            if (event.getStatus().equals(LogEvent.EventStatus.FAIL)) {
                lifecycle.updateStep(stepResult -> {
                    final StatusDetails details = ResultsUtils.getStatusDetails(event.getError())
                            .orElse(new StatusDetails());
                    stepResult.setStatus(Status.FAILED);
                    stepResult.setStatusDetails(details);
                });
            }
            lifecycle.stopStep(stepUUID);
        });
    }

    private byte[] getPageSourceBytes() {
        return WebDriverRunner.getWebDriver().getPageSource().getBytes(StandardCharsets.UTF_8);
    }

    private Optional<EventFormatter> getFormatter(LogEvent event) {
        return formatters.stream()
                .filter(f -> f.isApplicable(event))
                .findFirst();
    }

    private List<EventFormatter> getDefaultFormatters() {
        List<EventFormatter> formatters = new ArrayList<>();
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\(open\\) (?<url>.*)"),
                "Открываем стрницу \"${url}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) click\\(\\)"),
                "Кликем на элемент \"${element}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) double click\\(\\)"),
                "Даблкликаем на элемент \"${element}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) press paste\\((?<value>.*)\\)"),
                "Вставляем в элемент \"${element}\" значение [${value}] из буфера обмена"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) press backspace\\(\\)"),
                "Очищаем элемент \"${element}\" по нажатию Backspace"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) hover\\(\\)"),
                "Наводим курсор мышки на элемент \"${element}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) wait until\\(\\[(?<condition>.*),(?<time>.*)\\]\\)"),
                "Ждем, пока элемент \"${element}\" будет в состоянии \"${condition}\" в течении [${time} млс]"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) wait while\\(\\[(?<condition>.*),(?<time>.*)\\]\\)"),
                "Ждем, пока элемент \"${element}\" не будет в состоянии \"${condition}\" в течении [${time} млс]"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) should\\((?<condition>.*)\\)"),
                "Проверяем, что элемент \"${element}\" в состоянии \"${condition}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) should matched\\(\\[(?<message>.*),(?<condition>.*)\\]\\)"),
                "Проверяем, что элемент \"${element}\" ${condition}"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) should matched\\((?<condition>.*)\\)"),
                "Проверяем, что элемент \"${element}\" ${condition}"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) should have\\((?<condition>.*)\\)"),
                "Проверяем, что элемент \"${element}\" в состоянии \"${condition}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) set value\\((?<value>.*)\\)"),
                "Вводим в \"${element}\" значение [${value}]"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) click \\(()\\)"),
                "Кликаем на элемент \"${element}\" "
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\(confirm\\) (?<value>.*)"),
                "Алерт содержит текст \"${value}\""
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\(assertThat\\) (?<value>.*)"),
                "${value}"
        ));
        formatters.add(new EventFormatter(
                Pattern.compile("\\$\\((?<element>.*)\\) select option containing text\\((?<value>.*)\\)"),
                "Выбираем из выпадающего списка \"${element}\" значение \"${value}\""
        ));
        return formatters;
    }

    private class EventFormatter {

        private final Pattern pattern;

        private final String replacement;

        EventFormatter(Pattern pattern, String replacement) {
            this.replacement = replacement;
            this.pattern = pattern;
        }

        boolean isApplicable(LogEvent event) {
            return pattern.matcher(event.toString()).find();
        }


        String format(LogEvent event) {
            return pattern.matcher(event.toString()).replaceAll(replacement);
        }
    }
}
