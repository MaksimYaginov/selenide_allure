import exSelenide.AllureSelenideListener;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Selenide.open;

public class TestCase {

    BasePage basePage = new BasePage();


    @Before
    public void before(){
        SelenideLogger.addListener("allure", new AllureSelenideListener());
        open("http://www.yandex.ru");
    }

    @Test
    public void testCase1(){
        basePage.getLogo().click();
        basePage.getSearchField().setValue("кот");
        basePage.getSearchButton().click();

        /*for(SelenideElement element: basePage.getAllLinks())
            element.should(Condition.enabled);*/

        for (int i = 0; i< basePage.getAllLinks().size(); i++){
            SelenideElement element = basePage.getAllLinks().get(i);
            element.should(Condition.enabled);
        }
    }
}
