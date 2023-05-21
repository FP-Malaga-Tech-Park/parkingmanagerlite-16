package com.hormigo.david.parkingmanager.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.hormigo.david.parkingmanager.bdd.CucumberConfiguration;
import com.hormigo.david.parkingmanager.core.exceptions.UserExistsException;
import com.hormigo.david.parkingmanager.draw.domain.Draw;
import com.hormigo.david.parkingmanager.draw.domain.DrawRepository;
import com.hormigo.david.parkingmanager.draw.service.DrawServiceImpl;
import com.hormigo.david.parkingmanager.user.domain.Role;
import com.hormigo.david.parkingmanager.user.domain.User;
import com.hormigo.david.parkingmanager.user.domain.UserRepository;
import com.hormigo.david.parkingmanager.user.service.UserServiceImpl;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
public class CucumberSteps extends CucumberConfiguration {

    private static ChromeDriver driver;

    @BeforeAll
    public static void prepareWebDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\ChromeDriver\\chromedriver.exe");

    }
    @MockBean
    private UserRepository mockedRepository;
    @Spy
    @InjectMocks
    private UserServiceImpl mockedUserService;

    @MockBean
    private DrawRepository drawMockedRepository;
    @Spy
    @InjectMocks
    private DrawServiceImpl mockedDrawService;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        MockitoAnnotations.openMocks(this);
        driver = new ChromeDriver(options);
        clearInvocations(mockedRepository);
        clearInvocations(mockedUserService);
    }

    @After
    public void quitDriver() {
        driver.quit();

    }

    @Dado("un usuario esta en la pagina {}")
    public void openPage(String pageName) {
        driver.get(getUrlFromPageName(pageName));

    }

    @Dado("el correo {} no esta asignado a otro usuario")
    public void mockUserNotExists(String email){
        when(mockedUserService.userExists(email)).thenReturn(false);
        when(mockedRepository.findByEmail(email)).thenReturn(null);
        
    }

    @Dado("el correo {} si esta asignado a otro usuario")
    public void mockUserExists(String email){
        when(mockedUserService.userExists(email)).thenReturn(true);
        when(mockedRepository.findByEmail(email)).thenReturn(new User(email,"test","testApellido",Role.STUDENT));
    }


    @Cuando("relleno el campo {} con {}")
    public void populateField(String fieldName, String fieldValue) {
        WebElement inputField = driver.findElement(By.id(getFieldIdFromName(fieldName)));
        inputField.sendKeys(fieldValue);
    }

    @Cuando("el usuario hace click sobre el botón de {}")
    public void clickButton(String buttonName) {
        String buttonId = "";
        switch (buttonName) {
            case "Usuarios":
                buttonId = "to-users-link";
                break;
            case "Sorteos":
                buttonId = "to-draws-link";
                break;
            case "crear usuario":
                buttonId = "user-create-button-submit";
                break;
            default:
                break;
        }
        driver.findElement(By.id(buttonId)).click();
    }

    @Entonces("esta en la pagina de {}")
    public void isInPage(String pageName) {

        assertTrue(driver.getCurrentUrl().equals(getUrlFromPageName(pageName)));
    }

    @Entonces("se ha persistido el usuario en la base de datos")
    public void checkUserWasSaved() {
        verify(mockedRepository, times(1)).save(any(User.class));
    }

    @Entonces("no se ha persistido el usuario en la base de datos")
    public void checkUserWasNotSaved(){
        verify(mockedRepository, times(0)).save(any(User.class));
    }

    @Entonces("se ha persistido el sorteo en la base de datos")
    public void checkDrawWasSaved(){
        verify(drawMockedRepository, times(1)).save(any(Draw.class));
    }


    @Entonces("se muestra un campo de {}")
    public void fieldIsDisplayed(String fieldName) {
        String fieldId = getFieldIdFromName(fieldName);
        WebElement field = driver.findElement(By.id(fieldId));

        assertTrue(field.isDisplayed());
    }

    @Entonces("se muestra un botón de {}")
    public void btnIsDisplayed(String btnName){
        String btnId = getBtnIdFromName(btnName);
        WebElement btn = driver.findElement(By.id(btnId));
        
        assertTrue(btn.isDisplayed());
    }

    @Entonces("salta un error porque el {}")
    public void errorIsDisplayed(String errorName) {
        String errorMessage = getErrorMessageFromName(errorName);
        String actualMessage = driver.findElement(By.id("hasErrors")).getText();
        
        assertEquals(errorMessage, actualMessage);
    }

    private String getUrlFromPageName(String pageName) {
        String endPoint = "";
        switch (pageName) {
            case "inicial":
                endPoint = "/";
                break;
            case "lista de usuarios":
                endPoint = "/users";
                break;
            case "lista de sorteos":
                endPoint = "/draws";
                break;
            case "creación de usuarios":
                endPoint = "/newUser";
                break;
            default:
                break;
        }
        return getUrlFromEndPoint(endPoint);
    }

    private String getFieldIdFromName(String fieldName) {
        String fieldId = "";
        switch (fieldName) {
            case "correo electrónico":
                fieldId = "user-create-field-email";
                break;
            case "nombre":
                fieldId = "user-create-field-name";
                break;
            case "primer apellido":
                fieldId = "user-create-field-lastname1";
                break;
            case "segundo apellido":
                fieldId = "user-create-field-lastname2";
                break;
            default:
                break;
        }
        return fieldId;
    }

    private String getUrlFromEndPoint(String endpoint) {
        return "http://localhost:" + port + endpoint;
    }

    private String getErrorMessageFromName(String errorName) {
        String errorMessage = "";
        switch (errorName) {
            case "nombre está vacío":
                errorMessage = "El nombre es obligatorio";
                break;
            case "primer apellido está vacío":
                errorMessage = "El primer apellido es obligatorio";
                break;
            case "correo está vacío":
                errorMessage = "El correo es obligatorio";
                break;
            case "correo ya existe":
                errorMessage = "Ya existe el usuario con el correo";
                break;
            default:
                break;
        }
        return errorMessage;
    }

    private String getBtnIdFromName(String btnName) {
        String btnId = "";
        switch (btnName) {
            case "Usuarios":
                btnId = "to-users-link";
                break;
            case "Sorteos":
                btnId = "to-draws-link";
                break;
            case "crear usuario":
                btnId = "user-create-button-submit";
                break;
            case "crear sorteo":
                btnId = "draw-button-submit";
                break;
            case "creación de sorteo":
                btnId = "draw-button-submit";
                break;
            case "creación de usuario":
                btnId = "user-create-button-submit";
                break;
            default:
                break;
        }
        return btnId;
    }

}
