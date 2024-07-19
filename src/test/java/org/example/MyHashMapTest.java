package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {
    MyHashMap<Integer,String> map = new MyHashMap<>();
    @Test
    public void putAndGet() {
        map.put(1,"Jessica");
        assertEquals("Jessica",map.get(1));
    }
    @Test
    public void getNull() {
        assertEquals(null,map.get(1));
    }
    @Test
    public void initializeWithTwoParameters() {
        MyHashMap<Integer,String> map = new MyHashMap<>(32,0.75f);
        assertEquals(32,map.getCapacity());
    }
    @Test
    public void isPowerOfTwoException() {
        int capacity = 14;
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                MyHashMap<Integer,String> map = new MyHashMap<>(capacity);
            });
            String expectedMessage = "Емкость должна быть степенью двойки: " + capacity;
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage,actualMessage);
        }
    @Test
    public void problemOfLoadFactor() {
        float loadFactor = -5f;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MyHashMap<Integer,String> map = new MyHashMap<>(32,loadFactor);
        });
        String expectedMessage = "Load Factor не может принимать такое значение: " + loadFactor;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void negativeNumberException() {
        int capacity = -4;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            MyHashMap<Integer,String> map = new MyHashMap<>(capacity);
        });
        String expectedMessage = "Емкость не может быть отрицательным числом: " + capacity;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    public void keyNull() {
        Integer key = null;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            map.put(key,"f");
        });
        String expectedMessage = "Нулевой ключ не принимается";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage,actualMessage);
    }
    @Test
    public void putWithReplace() {
        map.put(1,"Jessica");
        map.put(1,"Khloyan");
        assertEquals("Khloyan",map.get(1));
    }
    @Test
    public void removeIfTrue() {
        map.put(1,"Jessica");
        assertTrue(map.remove(1));
    }
    @Test
    public void removeIfFalse() {
        map.put(1,"Jessica");
        assertFalse(map.remove(2));
    }
    @Test
    public void nodeEqualsIfTrue() {
        MyHashMap.Node<Integer,String> node1 = new MyHashMap.Node<>(1,"A",null);
        MyHashMap.Node<Integer,String> node2 = new MyHashMap.Node<>(1,"A",null);
        assertEquals(node1, node2);
    }
    @Test
    public void nodeEqualsIfFalse() {
        MyHashMap.Node<Integer,String> node1 = new MyHashMap.Node<>(1,"A",null);
        MyHashMap.Node<Integer,String> node2 = new MyHashMap.Node<>(1,"a",null);
        assertNotEquals(node1, node2);
    }
    @Test
    public void checkNodeHashcode() {
        MyHashMap.Node<Integer,String> node1 = new MyHashMap.Node<>(1,"j",null);
        MyHashMap.Node<Integer,String> node2 = new MyHashMap.Node<>(1,"j",null);
        assertEquals(node1.hashCode(),node2.hashCode());
    }
    @Test
    public void isEmpty() {
        assertEquals(true,map.isEmpty());
    }
    @Test
    public void resizeCheck() {
        map.put(1,"Jessica");
        map.put(2,"Khloyan");
        map.put(3,"A");
        map.put(4,"P");
        map.put(5,"K");
        map.put(6,"S");
        map.put(7,"F");
        map.put(8,"AF");
        map.put(9,"GH");
        map.put(10,"FG");
        map.put(11,"L");
        map.put(12,"PKS");
        assertEquals(32,map.getCapacity());
    }
    @Test
    public void sizeCheck() {
        map.put(1,"Jessica");
        map.put(2,"Khloyan");
        map.put(3,"A");
        map.put(4,"P");
        map.put(5,"K");
        map.put(6,"S");
        map.put(7,"F");
        map.put(8,"AF");
        map.put(9,"GH");
        map.put(10,"FG");
        map.put(11,"L");
        map.put(12,"PKS");
        assertEquals(12,map.getSize());
    }
    @Test
    public void remove() {
        map.put(1,"Jessica");
        map.put(2,"Khloyan");
        map.put(3,"A");
        map.put(4,"P");
        map.put(5,"K");
        map.put(6,"S");
        map.put(7,"F");
        map.put(8,"AF");
        map.put(9,"GH");
        map.put(10,"FG");
        map.put(11,"L");
        map.remove(9);
        assertEquals(null,map.get(9));
    }
    @Test
    public void getCapacity() {
        assertEquals(16,map.getCapacity());
    }
    @Test
    public void getThreshold() {
        MyHashMap<Integer,String> map = new MyHashMap<>(8);
        assertEquals(6,map.threshold);
    }
    @Test
    public void toStringHashMap() {
        map.put(1,"Jessica");
        assertEquals("Элемент {ключ=1, значение=Jessica}\n",map.toString());
    }
}