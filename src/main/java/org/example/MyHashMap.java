package org.example;

import static java.util.Objects.hash;

/**
 * Класс кастомного HashMap. Он позволяет хранить пары ключ-значение и предоставляет методы для добавления, удаления и получения элементов.
 * Коллизии решаются методом цепочек, при достижении порога заполнения массива происходит увеличение его размера в 2 раза.
 * Внутри содержится внутренний класс Node, представляющий собой структуру, инкапсулирующую пару ключ-значение.
 */
class MyHashMap<K, V> {
    private Node[] buckets;
    private static final int DEFAULT_CAPACITY = 16;
    private int capacity;
    final int MAXIMUM_CAPACITY = 1 << 30;
    private final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int DEFAULT_THRESHOLD = (int) (DEFAULT_CAPACITY*DEFAULT_LOAD_FACTOR);
    private int size; //кол-во пар ключ-значение
    private float loadFactor;
    public int threshold;
/**
 * Конструктор без параметров устанавливает дефолтные значения: порог заполнения - 0.75, размер массива - 16,
 * фактическое кол-во значений, после которых размер массива увеличивается в 2 раза - capacity*loadfactor (12).
 */
    public MyHashMap() {
        buckets = new Node[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = DEFAULT_THRESHOLD;
        capacity = DEFAULT_CAPACITY;
    }
/**
 * Конструктор с параметрами:
 * @param capacity
 * @param loadFactor
 * Внутри проверяется, чтобы параметры были соответствующими (неотрицательными, а loadfactor еще и не равен 0;
 * capacity должен быть степенью двойки и меньше максимального значения (1 <<30)).
 */
    public MyHashMap(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Емкость не может быть отрицательным числом: " + capacity);
        }
        if (!isPowerOfTwo(capacity)) {
            throw new IllegalArgumentException("Емкость должна быть степенью двойки: " + capacity);
        }
        if (capacity > MAXIMUM_CAPACITY) {
            this.capacity = MAXIMUM_CAPACITY;
        } else {
            this.capacity = capacity;
        }
        if (loadFactor <= 0) {
            throw new IllegalArgumentException("Load Factor не может принимать такое значение: " + loadFactor);
        }
        buckets = new Node[capacity];
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity*loadFactor);
    }
/**
 * Конструктор с параметром capccity, loadfactor стандартный - 0.75.
 */
    public MyHashMap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Емкость не может быть отрицательным числом: " + capacity);
        }
        if (!isPowerOfTwo(capacity)) {
            throw new IllegalArgumentException("Емкость должна быть степенью двойки: " + capacity);
        }
        if (capacity > MAXIMUM_CAPACITY) {
            this.capacity = MAXIMUM_CAPACITY;
        } else {
            this.capacity = capacity;
        }
        buckets = new Node[capacity];
        this.threshold = (int) (capacity*DEFAULT_LOAD_FACTOR);
    }

    /**
 * Метод проверяющий, является ли число степенью двойки. Логика работы:
 * 1. В двоичной системе счисления степени двойки всегда имеют ровно один ненулевой бит.
 * 2. Когда вы вычитаете 1 из степени двойки, этот ненулевой бит сбрасывается, а все биты правее него становятся единицами.
 * 3. Применяя операцию "И" (&) к исходному числу и числу, уменьшенному на 1, результат будет равен 0, если исходное число было степенью двойки.
 * Тогда вернется true, в противном случае - false.
 * @param n
 */
    private boolean isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }
/**
 * Возвращает фактическое кол-во пар ключ-значение
 */
    public int getSize() {
        return size;
    }
    //метод для того, чтобы в тестах проверить увеличивается ли размер в 2 раза при достижении порога заполнения
    public int getCapacity() {
        return capacity;
    }
/**
 * Метод, который "кладет" в HashMap элементы. Работает по системе метода цепочек: если в ячейке массива (высчитывается по ключу) нет
 * никаких элементов, то кладем туда и все. Если уже что-то раннее положили, то итерируем по связанному списку внутри этого бакета: есть
 * такой ключ - заменяем значение на новое, нет - проходимся до конца списка и кладем туда новую пару ключ-значение.
 * В случае если порог заполнения достигнут, вызывается метод resize(), который увеличивает массив в 2 раза и перекидывает элементы в новый массив.
 * @param newKey
 * @param newValue
*/
    public void put(K newKey, V newValue) {
        if (newKey == null) //нельзя добавлять нулевой ключ (для избежания бOльших проблем, хотя оригинальная реализация подразумевает null-ключ
            throw new IllegalArgumentException("Нулевой ключ не принимается");
        int hash = getBucketNumber(newKey); //вызывается метод, который вычисляет номер бакета. все это кладется в переменную getBucketNumber
        Node<K, V> newNode = new Node<K, V>(newKey, newValue, null); //создаем ноду с ключом, значением и нулевой ссылкой на следующий элемент, ибо его нет
        if (buckets[hash] == null) {  //если по этому индексу никакая нода еще не лежит, то кладем спокойно
            buckets[hash] = newNode;
            size++;
        } else {
            Node<K, V> currentNode = buckets[hash];
            while (currentNode.next != null) {              //пока мы идем по списку и ноды непустые, мы ищем последнюю ноду, чтобы вставить нашу
                if (currentNode.getKey().equals(newKey)) { //одинаковые ключи - заменяем значение и выходим
                    currentNode.setValue(newValue);
                    return;
                }
                currentNode = currentNode.next;
            }
            if (currentNode.key.equals(newKey)) { //т. к. мы в цикле проверяем является ли null следующий элемент от текущего,
                currentNode.setValue(newValue);  // то в один момент мы можем потерять проверку на текущий элемент.
            } else {                            // поэтому здесь повторяется данная проверка
                currentNode.next = newNode;
                size++;
            }
        }
        if (size>=threshold) {
            resize();
        }
    }
/**
 * Метод, увеличивающий размер массива в 2 раза и перекидывающий в новый массив все прошлые элементы.
 */
    private void resize() {
        capacity = capacity*2;
        Node[] newBuckets = new Node[capacity];
        for (Node<K,V> node : buckets) {
            Node<K, V> current = node;
            while (current != null) {
                int index = getBucketNumber(current.getKey());
                if (newBuckets[index] == null) {
                    newBuckets[index] = new Node(current.key, current.value,null);
                } else {
                    Node<K,V> temp = newBuckets[index];
                    while (temp.next != null) {
                        temp = temp.next;
                    }
                    temp.next = new Node(current.key, current.value,null);
                }
                current = current.next;
            }
        }
        buckets = newBuckets;
    }
/**
 * Метод строкового представления HashMap
 */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                Node<K, V> node = buckets[i];
                while (node != null) {
                   result.append(node);
                   node = node.next;
                }
            }
        }
        return result.toString();
    }
/**
 * Метод, который возвращает по ключу элемент. Внутри производится поиск, сравнивающий целевой ключ и каждый из тех, по которым мы проходимся.
 * Нашли такой элемент - возвращаем его значение, нет - возвращаем null
 * @param key
 */
    public V get(K key) {
        int hash = getBucketNumber(key);
        Node<K, V> currentNode = buckets[hash];
        if (currentNode==null) {
            return null;
        }
        while (currentNode != null) {
            if (currentNode.getKey().equals(key)) { //равны - достаем значение текушей ноды
                return currentNode.getValue();
            }
            currentNode = currentNode.next; //переход
        }
        return null; //нет ноды с таким ключом - возвращаем null
    }
/**
 * Метод удаления пары ключ-значение. Так как список односвязный и у текущего элемента нет ссылки на предыдущий эл., на каждом шаге итерации
 * нам необходимо в отдельный переменной хранить текущий эл. (на следующей итерации он станет предыдущим). Если эд. удален - возвращается true,
 * нет - false.
 * @param keyForRemove
 */
    public boolean remove(K keyForRemove) {
        int hash = getBucketNumber(keyForRemove);
        Node<K,V> currentNode = buckets[hash];
        Node<K,V> previous = null; //т. к. список односвязный, нужна переменная, в которой будет лежать предыдущий эл. от текущего, чтобы ссылки переставить
        if (currentNode==null) { //null - сразу возвращаем false, чтобы по циклу не идти
            return false;
        }
        while (currentNode!=null) {
            if (currentNode.getKey().equals(keyForRemove)) {
                if (previous==null) { //если ключи равны и предыдущий - null (т. к. текущий эл. - 1-ый
                    buckets[hash] = buckets[hash].next; // на место текущего ставим следующий после него, тем самым удаляя в памяти ссылку на него
                    size--;
                    return true;
                } else {
                    previous.next = currentNode.next; //если же нода лежит внутри списка, то просто следующим предыдущего делаем следующий от текущего
                    size--;
                    return true;
                }
            }
            previous = currentNode; //чтобы предыдущий поймать
            currentNode = currentNode.next; //переход текущей ноды
        }
        return false;
    }
/**
 * Метод, находящий индекс бакета по хэш-коду ключа.
 * @param key
 */
    //вычисляется номер бакета, куда нужно вставить пару ключ-значение
    private int getBucketNumber(K key) {
        return key == null ? 0 : Math.abs(key.hashCode());
    }
/**
 * Метод, проверяющий пустой ли HashMap.
 */
    boolean isEmpty() {
        return size==0;
    }
/**
 * Класс, инкапсулирующий пару ключ-значение.
 */
    static class Node<K, V> {
    private final K key;
    private V value;
    private Node<K, V> next;

    /**
     * Конструктор с параметрами:
     *
     * @param key
     * @param next
     * @param value
     */
    public Node(K key, V value, Node<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    /**
     * Геттер ключа.
     */
    public K getKey() {
        return key;
    }

    /**
     * Геттер значения.
     */
    public V getValue() {
        return value;
    }

    /**
     * Сеттер значения.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Переопределенный equals, который сравнивает узлы по ключу и значению.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof MyHashMap.Node) {
            Node<?, ?> node = (Node<?, ?>) obj;
            return key.equals(node.getKey()) &&
                    value.equals(node.getValue());
        }
        return false;
    }

    /**
     * Переопределенный hashcode, который генерирует hashcode именно узла.
     */
    @Override
    public int hashCode() {
        return hash(key, value);
    }
    /**
     * Метод строкового представления узла.
     */
    @Override
    public String toString() {
        return "Элемент {" +
                "ключ=" + key + ", значение=" + value +
                "}\n";
        }
    }
}
