package com.example.word;

import com.example.word.util.JsonHelper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON 解析测试
 * @author DaY1zz
 */
public class ResolveJsonTest {

    @Test
    public void testObjectToJson() {
        TestUser user = new TestUser("张三", 25, "test@example.com");
        String json = JsonHelper.toJson(user);
        
        assertNotNull(json);
        assertTrue(json.contains("张三"));
        assertTrue(json.contains("25"));
    }

    @Test
    public void testJsonToObject() {
        String json = "{\"name\":\"李四\",\"age\":30,\"email\":\"lisi@example.com\"}";
        TestUser user = JsonHelper.fromJson(json, TestUser.class);
        
        assertNotNull(user);
        assertEquals("李四", user.getName());
        assertEquals(30, user.getAge());
        assertEquals("lisi@example.com", user.getEmail());
    }

    @Test
    public void testJsonToList() {
        String json = "[{\"name\":\"王五\",\"age\":25},{\"name\":\"赵六\",\"age\":28}]";
        List<TestUser> users = JsonHelper.fromJsonToList(json, TestUser.class);
        
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("王五", users.get(0).getName());
        assertEquals("赵六", users.get(1).getName());
    }

    @Test
    public void testJsonToMap() {
        String json = "{\"name\":\"测试\",\"count\":5,\"active\":true}";
        Map<String, Object> map = JsonHelper.fromJsonToMap(json);
        
        assertNotNull(map);
        assertEquals("测试", map.get("name"));
        assertEquals(5, map.get("count"));
        assertEquals(true, map.get("active"));
    }

    @Test
    public void testFormatJson() {
        String json = "{\"name\":\"test\",\"age\":25}";
        String formatted = JsonHelper.formatJson(json);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\n"));
        assertTrue(formatted.contains("  "));
    }

    @Test
    public void testIsValidJson() {
        String validJson = "{\"name\":\"test\",\"age\":25}";
        String invalidJson = "{name:test,age:25}";
        
        assertTrue(JsonHelper.isValidJson(validJson));
        assertFalse(JsonHelper.isValidJson(invalidJson));
    }

    @Test
    public void testComplexObject() {
        TestComplex complex = new TestComplex();
        complex.setName("复杂对象");
        complex.setNumbers(Arrays.asList(1, 2, 3));
        complex.setNested(new TestNested("嵌套对象", 100));
        
        String json = JsonHelper.toJson(complex);
        TestComplex parsed = JsonHelper.fromJson(json, TestComplex.class);
        
        assertNotNull(parsed);
        assertEquals("复杂对象", parsed.getName());
        assertEquals(3, parsed.getNumbers().size());
        assertEquals("嵌套对象", parsed.getNested().getTitle());
    }

    // 测试用的内部类
    public static class TestUser {
        private String name;
        private Integer age;
        private String email;

        public TestUser() {}

        public TestUser(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class TestComplex {
        private String name;
        private List<Integer> numbers;
        private TestNested nested;

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Integer> getNumbers() { return numbers; }
        public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }
        public TestNested getNested() { return nested; }
        public void setNested(TestNested nested) { this.nested = nested; }
    }

    public static class TestNested {
        private String title;
        private Integer value;

        public TestNested() {}

        public TestNested(String title, Integer value) {
            this.title = title;
            this.value = value;
        }

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }
}