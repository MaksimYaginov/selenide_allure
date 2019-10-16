package exSelenide;

import com.codeborne.selenide.*;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.ElementFinder;
import org.openqa.selenium.By;


import static com.codeborne.selenide.WebDriverRunner.driver;

public class ExSelenide extends Selenide {

    public static SelenideElement $(By seleniumSelector) {
        return ElementFinder.wrap(driver(), SelenideElement.class, null, seleniumSelector, 0);
    }

    public static ElementsCollection $$(By seleniumSelector) {
        return new ElementsCollection(new BySelectorCollection(driver(), seleniumSelector));
    }
}
