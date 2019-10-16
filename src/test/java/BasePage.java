import exSelenide.ExBy;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static exSelenide.ExSelenide.$;
import static exSelenide.ExSelenide.$$;


public class BasePage {

    private SelenideElement logo = $(ExBy.cssSelector("div.home-logo__default", "Логотип яндекса"));

    private SelenideElement searchField = $(ExBy.id("text", "Поле для ввода текста поиска"));

    private SelenideElement searchButton = $(ExBy.cssSelector("div.search2__button", "Кнопка 'Поиск'"));

    private ElementsCollection allLinks = $$(ExBy.cssSelector("a.Link", "Все ссылки"));

    public SelenideElement getSearchButton() {
        return searchButton;
    }

    public ElementsCollection getAllLinks() {
        return allLinks;
    }

    public SelenideElement getLogo() {
        return logo;
    }

    public SelenideElement getSearchField() {
        return searchField;
    }
}
