package io.github.giovannilamarmora.utils.interceptors.utilities;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.giovannilamarmora.utils.utilities.Mapper;
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import lombok.Getter;
import org.junit.jupiter.api.Test;

public class ObjectToolkitTest {
  /*---------------------------------------------------------------------------
   * Classi di supporto per i test (non statiche)
   *--------------------------------------------------------------------------*/

  // Dummy per testare isInstanceOf
  static class Dummy {
    public String name;
    public Integer value;

    public Dummy() {} // Costruttore di default necessario per Jackson

    public Dummy(String name, Integer value) {
      this.name = name;
      this.value = value;
    }
  }

  // Classe per testare fieldsNullOrEmpty
  @Getter
  static class TestClass {
    private String field1;
    private Integer field2;

    public TestClass(String field1, Integer field2) {
      this.field1 = field1;
      this.field2 = field2;
    }
  }

  // Classe per testare getValueOrNull e getValueOrDefault
  static class Holder {
    private String value;

    public Holder(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  // Classi per testare la catena di getter (areNotNullOrEmpty e areNotNullOrEmptyCast)
  class UserInfoResponse {
    private JWTData userInfo;

    public UserInfoResponse(JWTData userInfo) {
      this.userInfo = userInfo;
    }

    public JWTData getUserInfo() {
      return userInfo;
    }
  }

  class JWTData {
    private List<String> roles;

    public JWTData(List<String> roles) {
      this.roles = roles;
    }

    public List<String> getRoles() {
      return roles;
    }
  }

  // Enum per testare isEnumValue
  enum Color {
    RED,
    BLUE
  }

  /*---------------------------------------------------------------------------
   * Test per getFinalMapFromValue
   *--------------------------------------------------------------------------*/
  @Test
  void testGetFinalMapFromValue() {
    Map<String, String> source = new HashMap<>();
    source.put("user", "Alice");
    source.put("role", "Admin");

    Map<String, String> target = new HashMap<>();
    target.put("message", "Hello, {{user}}! Your role is {{role}}.");
    target.put("unchanged", "No placeholders here.");

    Map<String, String> result = ObjectToolkit.getFinalMapFromValue(source, target);

    assertEquals("Hello, Alice! Your role is Admin.", result.get("message"));
    assertEquals("No placeholders here.", result.get("unchanged"));
  }

  /*---------------------------------------------------------------------------
   * Test per fieldsNullOrEmpty
   *--------------------------------------------------------------------------*/
  @Test
  void testFieldsNullOrEmpty_AllFieldsNull() {
    TestClass obj = new TestClass(null, null);
    assertTrue(ObjectToolkit.fieldsNullOrEmpty(obj));
  }

  @Test
  void testFieldsNullOrEmpty_FieldNotNull() {
    TestClass obj = new TestClass("not null", null);
    assertFalse(ObjectToolkit.fieldsNullOrEmpty(obj));
  }

  /*---------------------------------------------------------------------------
   * Test per isNullOrEmpty
   *--------------------------------------------------------------------------*/
  @Test
  void testIsNullOrEmpty() {
    // Null
    assertTrue(ObjectToolkit.isNullOrEmpty(null));
    // Empty String
    assertTrue(ObjectToolkit.isNullOrEmpty(""));
    // Non-empty String
    assertFalse(ObjectToolkit.isNullOrEmpty("abc"));
    // Empty Optional
    assertTrue(ObjectToolkit.isNullOrEmpty(Optional.empty()));
    // Non-empty Optional
    assertFalse(ObjectToolkit.isNullOrEmpty(Optional.of("value")));
    // Empty Collection
    assertTrue(ObjectToolkit.isNullOrEmpty(Collections.emptyList()));
    // Non-empty Collection
    assertFalse(ObjectToolkit.isNullOrEmpty(Arrays.asList(1, 2, 3)));
    // Empty Map
    assertTrue(ObjectToolkit.isNullOrEmpty(Collections.emptyMap()));
    // Non-empty Map
    Map<String, String> map = new HashMap<>();
    map.put("key", "value");
    assertFalse(ObjectToolkit.isNullOrEmpty(map));
    // Empty Array
    Object emptyArray = Array.newInstance(String.class, 0);
    assertTrue(ObjectToolkit.isNullOrEmpty(emptyArray));
    // Non-empty Array
    Object nonEmptyArray = new String[] {"a"};
    assertFalse(ObjectToolkit.isNullOrEmpty(nonEmptyArray));
  }

  /*---------------------------------------------------------------------------
   * Test per isEnumValue
   *--------------------------------------------------------------------------*/
  @Test
  void testIsEnumValue() {
    assertTrue(ObjectToolkit.isEnumValue("RED", Color.class));
    assertFalse(ObjectToolkit.isEnumValue("GREEN", Color.class));
    assertFalse(ObjectToolkit.isEnumValue(null, Color.class));
  }

  /*---------------------------------------------------------------------------
   * Test per getValueOrNull
   *--------------------------------------------------------------------------*/
  @Test
  void testGetValueOrNull() {
    Holder holder1 = new Holder("abc");
    Holder holder2 = new Holder("");
    String value1 = ObjectToolkit.getValueOrNull(holder1, Holder::getValue);
    String value2 = ObjectToolkit.getValueOrNull(holder2, Holder::getValue);
    assertEquals("abc", value1);
    assertNull(value2);
  }

  /*---------------------------------------------------------------------------
   * Test per getValueOrDefault
   *--------------------------------------------------------------------------*/
  @Test
  void testGetValueOrDefault() {
    Holder holder1 = new Holder("abc");
    Holder holder2 = new Holder("");
    String value1 = ObjectToolkit.getValueOrDefault(holder1, Holder::getValue, "default");
    String value2 = ObjectToolkit.getValueOrDefault(holder2, Holder::getValue, "default");
    assertEquals("abc", value1);
    assertEquals("default", value2);
  }

  /*---------------------------------------------------------------------------
   * Test per areNotNullOrEmptyCast
   *--------------------------------------------------------------------------*/
  @Test
  void testAreNotNullOrEmptyCast() {
    JWTData jwtData = new JWTData(Arrays.asList("role1", "role2"));
    UserInfoResponse userInfoResponse = new UserInfoResponse(jwtData);

    // Poiché le classi non sono statiche, usiamo lambda con tipi espliciti
    boolean result =
        ObjectToolkit.areNotNullOrEmptyCast(
            userInfoResponse, (UserInfoResponse u) -> u.getUserInfo(), (JWTData j) -> j.getRoles());
    assertTrue(result);

    // Test con una proprietà annidata nulla
    UserInfoResponse userInfoResponseNull = new UserInfoResponse(null);
    boolean resultNull =
        ObjectToolkit.areNotNullOrEmptyCast(
            userInfoResponseNull,
            (UserInfoResponse u) -> u.getUserInfo(),
            (JWTData j) -> j == null ? null : j.getRoles());
    assertFalse(resultNull);
  }

  /*---------------------------------------------------------------------------
   * Test per areNotNullOrEmpty (versione con Function<Object, Object> e lift)
   *--------------------------------------------------------------------------*/
  @Test
  void testAreNotNullOrEmpty() {
    JWTData jwtData = new JWTData(List.of("role1", "role2"));
    UserInfoResponse userInfoResponse = new UserInfoResponse(jwtData);

    boolean result =
        ObjectToolkit.areNotNullOrEmpty(
            userInfoResponse,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertTrue(result);

    // Test con proprietà annidata mancante
    UserInfoResponse userInfoResponseNull = new UserInfoResponse(null);
    boolean resultNull =
        ObjectToolkit.areNotNullOrEmpty(
            userInfoResponseNull,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift((JWTData j) -> j == null ? null : j.getRoles()));
    assertFalse(resultNull);
  }

  /*---------------------------------------------------------------------------
   * Test per lift
   *--------------------------------------------------------------------------*/
  @Test
  void testLift() {
    Function<String, Integer> lengthFunction = String::length;
    Function<Object, Object> lifted = ObjectToolkit.lift(lengthFunction);
    Object result = lifted.apply("Hello");
    assertEquals(5, result);
  }

  /*---------------------------------------------------------------------------
   * Test per isInstanceOf
   *--------------------------------------------------------------------------*/
  @Test
  void testIsInstanceOf() {
    // JSON valido che deserializza in un oggetto Dummy
    String json = "{\"name\":\"TestName\",\"value\":123}";
    // Usiamo il metodo readObject che accetta una Class<T>
    Dummy dummy = Mapper.readObject(json, Dummy.class);
    assertNotNull(dummy, "Il dummy non deve essere null");
    // Verifichiamo che fieldsNullOrEmpty restituisca false (cioè l'oggetto non è vuoto)
    assertFalse(ObjectToolkit.fieldsNullOrEmpty(dummy));

    // Usando isInstanceOf con TypeReference, l'oggetto risultante non deve essere vuoto
    boolean instanceValid = ObjectToolkit.isInstanceOf(json, new TypeReference<Dummy>() {});
    assertTrue(instanceValid, "L'istanza deve essere considerata valida");

    // Test con JSON in cui il campo 'name' è null e 'value' è 0
    // Secondo la logica di fieldsNullOrEmpty, 0 (Integer) non è null, dunque l'oggetto non è
    // considerato vuoto
    String jsonWithNull = "{\"name\":null,\"value\":0}";
    boolean instanceWithNull =
        ObjectToolkit.isInstanceOf(jsonWithNull, new TypeReference<Dummy>() {});
    assertTrue(
        instanceWithNull, "Anche se 'name' è null, 'value' non lo è, quindi l'oggetto non è vuoto");
  }

  // Testa il metodo areNullOrEmptyCast con getter generici
  @Test
  void testAreNullOrEmptyCast() {
    // Creiamo un oggetto di esempio
    UserInfoResponse userInfoResponse =
        new UserInfoResponse(new JWTData(Arrays.asList("Admin", "User")));

    // Verifica che il valore della proprietà 'roles' non sia vuoto
    boolean result =
        ObjectToolkit.areNullOrEmptyCast(
            userInfoResponse,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertFalse(result);

    // Verifica che una proprietà annidata con valore null faccia restituire true
    UserInfoResponse userInfoWithNull = new UserInfoResponse(new JWTData(null));
    result =
        ObjectToolkit.areNullOrEmptyCast(
            userInfoWithNull,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertTrue(result);
  }

  // Testa il metodo areNullOrEmpty con getter tipizzati
  @Test
  void testAreNullOrEmpty() {
    // Creiamo un oggetto di esempio
    TestClass testClass = new TestClass("Test", 123);

    // Verifica che 'field1' non sia null o vuoto
    boolean result =
        ObjectToolkit.areNullOrEmpty(testClass, ObjectToolkit.lift(TestClass::getField1));
    assertFalse(result);

    // Verifica che 'field2' non sia null
    result = ObjectToolkit.areNullOrEmpty(testClass, ObjectToolkit.lift(TestClass::getField2));
    assertFalse(result);

    // Verifica che una proprietà null faccia restituire true
    TestClass testClassWithNull = new TestClass(null, null);
    result =
        ObjectToolkit.areNullOrEmpty(testClassWithNull, ObjectToolkit.lift(TestClass::getField1));
    assertTrue(result);
  }

  // Testa la lista e la collezione come proprietà annidate
  @Test
  void testAreNullOrEmptyWithCollections() {
    // Creiamo un oggetto di esempio
    JWTData jwtData = new JWTData(Arrays.asList("Admin", "User"));
    UserInfoResponse userInfoResponse = new UserInfoResponse(jwtData);

    // Verifica che la collezione 'roles' non sia vuota
    boolean result =
        ObjectToolkit.areNullOrEmpty(
            userInfoResponse,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertFalse(result);

    // Verifica una lista vuota
    jwtData = new JWTData(Arrays.asList());
    userInfoResponse = new UserInfoResponse(jwtData);
    result =
        ObjectToolkit.areNullOrEmpty(
            userInfoResponse,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertTrue(result);
  }

  // Test per le stringhe vuote e le collezioni vuote
  @Test
  void testAreNullOrEmptyWithEmptyStringAndCollection() {
    TestClass testClass = new TestClass("", 123);

    // Verifica che una stringa vuota faccia restituire true
    boolean result =
        ObjectToolkit.areNullOrEmpty(testClass, ObjectToolkit.lift(TestClass::getField1));
    assertTrue(result);

    // Verifica che una collezione vuota faccia restituire true
    JWTData jwtData = new JWTData(Arrays.asList());
    UserInfoResponse userInfoResponse = new UserInfoResponse(jwtData);
    result =
        ObjectToolkit.areNullOrEmpty(
            userInfoResponse,
            ObjectToolkit.lift(UserInfoResponse::getUserInfo),
            ObjectToolkit.lift(JWTData::getRoles));
    assertTrue(result);
  }

  // Test per fieldsNullOrEmpty
  @Test
  void testFieldsNullOrEmpty() {
    // Creiamo un oggetto di esempio
    TestClass testClass = new TestClass("Test", 123);

    // Verifica che l'oggetto non sia considerato vuoto
    boolean result = ObjectToolkit.fieldsNullOrEmpty(testClass);
    assertFalse(result);

    // Verifica che un oggetto con un campo nullo o vuoto sia considerato vuoto
    TestClass testClassWithNull = new TestClass(null, null);
    result = ObjectToolkit.fieldsNullOrEmpty(testClassWithNull);
    assertTrue(result);
  }
}
