package xxl.container.classic;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static xxl.java.container.classic.MetaMap.containsAllKeys;
import static xxl.java.container.classic.MetaMap.copyOf;
import static xxl.java.container.classic.MetaMap.extractedWithKeys;
import static xxl.java.container.classic.MetaMap.frequencies;
import static xxl.java.container.classic.MetaMap.getIfAbsent;
import static xxl.java.container.classic.MetaMap.keySetIntersection;
import static xxl.java.container.classic.MetaMap.keySetUnion;
import static xxl.java.container.classic.MetaMap.keysWithValue;
import static xxl.java.container.classic.MetaMap.keysWithValuesIn;
import static xxl.java.container.classic.MetaMap.newHashMap;
import static xxl.java.container.classic.MetaMap.newIdentityHashMap;
import static xxl.java.container.classic.MetaMap.newLinkedHashMap;
import static xxl.java.container.classic.MetaMap.onlyValueIs;
import static xxl.java.container.classic.MetaMap.putAllFlat;
import static xxl.java.container.classic.MetaMap.putMany;
import static xxl.java.container.classic.MetaMap.sameContent;
import static xxl.java.container.classic.MetaMap.allValuesIn;
import static xxl.java.container.classic.MetaMap.valuesParsedAsInteger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MetaMapTest {
	
	@Test
	public void putValueInManyKeys() {
		Map<String, String> map = newHashMap();
		assertEquals(0, map.size());
		List<String> keys = Arrays.asList("a", "b", "c", "d");
		putMany(map, "C", keys);
		assertEquals(4, map.size());
		for (String key : keys) {
			assertTrue(map.containsKey(key));
			assertEquals("C", map.get(key));
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void unionOfkeySets() {
		Map<String, String> stringMap = newHashMap();
		Map<String, Byte> byteMap = newHashMap();
		Collection<String> keyUnion = keySetUnion((Collection) asList(stringMap, byteMap));
		assertTrue(keyUnion.isEmpty());
		stringMap.put("a", "b");
		keyUnion = keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(1, keyUnion.size());
		assertTrue(keyUnion.contains("a"));
		byteMap.put("a", (byte) 0x29);
		keyUnion = keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(1, keyUnion.size());
		assertTrue(keyUnion.contains("a"));
		stringMap.put("b", "c");
		byteMap.put("z", (byte) 0x23);
		keyUnion = keySetUnion((Collection) asList(stringMap, byteMap));
		assertEquals(3, keyUnion.size());
		assertTrue(keyUnion.containsAll(asList("a", "b", "z")));
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void intersectionOfKeySets() {
		Map<String, Object> empty = newHashMap();
		Map<String, Object> mapA = newHashMap(asList("a", "c"), asList(null, null));
		Map<String, Object> mapB = newHashMap(asList("a", "b", "c"), asList(null, null, null));
		Map<String, Object> mapC = newHashMap(asList("x", "y", "z"), asList(null, null, null));
		assertEquals(0, keySetIntersection((Collection) asList()).size());
		assertEquals(0, keySetIntersection(asList(empty)).size());
		assertEquals(2, keySetIntersection(asList(mapA)).size());
		assertTrue(keySetIntersection(asList(mapA)).containsAll(asList("a", "c")));
		assertEquals(0, keySetIntersection(asList(empty, mapA)).size());
		assertEquals(2, keySetIntersection(asList(mapA, mapB)).size());
		assertTrue(keySetIntersection(asList(mapA)).containsAll(asList("a", "c")));
		assertEquals(0, keySetIntersection(asList(mapA, mapC)).size());
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void mapContainsKeys() {
		Map<String, Integer> map = newHashMap(asList("a", "b", "c"), asList(10, 20, 30));
		assertTrue(containsAllKeys(asList("a", "b", "c"), map));
		assertTrue(containsAllKeys(asList("a", "b"), map));
		assertTrue(containsAllKeys((List) asList(), map));
		assertFalse(containsAllKeys(asList("a", "b", "C"), map));
		assertFalse(containsAllKeys(asList(""), map));
		assertFalse(containsAllKeys(asList("C"), map));
	}
	
	@Test
	public void assertContentOfMap() {
		Map<String, Character> aMap = newHashMap();
		aMap.put("a", 'a');
		aMap.put("b", 'b');
		aMap.put("c", 'c');
		assertTrue(sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'c')));
		assertFalse(sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'd')));
		assertFalse(sameContent(aMap, asList("a", "b", "d"), asList('a', 'b', 'c')));
		assertFalse(sameContent(aMap, asList("a", "b", "c", "d"), asList('a', 'b', 'c', 'd')));
		assertFalse(sameContent(aMap, asList("a", "b"), asList('a', 'b')));
		assertFalse(sameContent(aMap, asList("a", "b", "c"), asList('a', 'b')));
		assertFalse(sameContent(aMap, asList("a", "b", "c"), asList('a', 'b', 'c', 'd')));
	}

	@Test
	public void adHocHashMap() {
		Map<String, Integer> adHocMap = newHashMap(asList("A", "b", "C"), asList(1, 2, 3));
		assertEquals(3, adHocMap.size());
		assertTrue(adHocMap.containsKey("A"));
		assertTrue(adHocMap.containsKey("b"));
		assertTrue(adHocMap.containsKey("C"));
		assertEquals(Integer.valueOf(1), adHocMap.get("A"));
		assertEquals(Integer.valueOf(2), adHocMap.get("b"));
		assertEquals(Integer.valueOf(3), adHocMap.get("C"));
	}
	
	@Test
	public void parseValuesOfMapAsIntegers() {
		Map<String, String> toBeParsed = newHashMap(asList("a", "b", "c"), asList("10", "20", "30"));
		Map<String, Integer> parsed = valuesParsedAsInteger(toBeParsed);
		assertTrue(sameContent(parsed, asList("a", "b", "c"), asList(10, 20, 30)));
	}
	
	@Test
	public void addAMapToManyMaps() {
		Map<String, String> newMap = newHashMap(asList("a", "b", "c"), asList("+", "++", "+++"));
		Map<String, String> firstMap = newHashMap(asList("d", "e", "f"), asList(".", "..", "..."));
		Map<String, String> secondMap = newHashMap(asList("g", "h", "i"), asList("-", "--", "---"));
		putAllFlat(newMap, asList(firstMap, secondMap));
		assertTrue(sameContent(firstMap, asList("a", "b", "c", "d", "e", "f"), asList("+", "++", "+++", ".", "..", "...")));
		assertTrue(sameContent(secondMap, asList("a", "b", "c", "g", "h", "i"), asList("+", "++", "+++", "-", "--", "---")));
	}
	
	@Test
	public void mapConstructorWithOneAssociation() {
		Map<String, Integer> adHocMap = newHashMap("aaaa", 4);
		assertFalse(adHocMap.isEmpty());
		assertEquals(1, adHocMap.size());
		assertTrue(adHocMap.containsKey("aaaa"));
		assertEquals(Integer.valueOf(4),  adHocMap.get("aaaa"));
	}
	
	@Test
	public void restrictedValueOfAMap() {
		Map<String, String> map = newHashMap(asList("a", "b", "c"), asList("z", "z", "z"));
		assertTrue(onlyValueIs("z", map));
		assertFalse(onlyValueIs("y", map));
		map.put("x", "x");
		assertFalse(onlyValueIs("x", map));
		assertFalse(onlyValueIs("z", map));
		assertFalse(onlyValueIs("z", newHashMap()));
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void restricedValuesOfAMap() {
		Map<String, String> map = newHashMap(asList("a", "b", "c"), asList("x", "y", "z"));
		assertTrue(allValuesIn(asList("x", "y", "z"), map));
		assertTrue(allValuesIn(asList("x", "y", "z", "t"), map));
		assertFalse(allValuesIn(asList("w", "y", "z"), map));
		assertFalse(allValuesIn(asList("y", "z"), map));
		assertFalse(allValuesIn((List) asList(), map));
		map.put("w", "w");
		assertFalse(allValuesIn(asList("x", "y", "z"), map));
		assertFalse(allValuesIn((List) asList(), newHashMap()));
	}
	
	@Test
	public void mapKeysByValue() {
		Map<String, String> map = newHashMap(asList("a", "b", "c"), asList("z", "z", "z"));
		Collection<String> keys = keysWithValue("z", map);
		assertEquals(3, keys.size());
		assertEquals(map.keySet(), keys);
		keys = keysWithValue("", map);
		assertEquals(0, keys.size());
		keys = keysWithValue("y", map);
		assertEquals(0, keys.size());
	}
	
	@Test
	public void mapKeysByValues() {
		Map<String, String> map = newHashMap(asList("a", "b", "c"), asList("x", "y", "z"));
		Collection<String> keys = keysWithValuesIn(asList("x", "y"), map);
		assertEquals(2, keys.size());
		assertTrue(keys.contains("a"));
		assertTrue(keys.contains("b"));
		keys = keysWithValuesIn(asList("v", "w", "x", "y", "z"), map);
		assertEquals(3, keys.size());
		assertEquals(map.keySet(), keys);
	}
	
	@Test
	public void frequenciesInList() {
		List<String> withRepetitions = asList("a", "b", "c", "a", "a", "b");
		Map<String, Integer> frequencies = frequencies(withRepetitions);
		Map<String, Integer> actualFrequencies = newHashMap(asList("a", "b", "c"), asList(3, 2, 1));
		assertEquals(actualFrequencies, frequencies);
	}
	
	@Test
	public void getDefaultValueIfAbsent() {
		Map<String, String> adHocMap = newHashMap("a", "A");
		assertEquals(1, adHocMap.size());
		assertEquals("A", getIfAbsent(adHocMap, "a", "_"));
		assertEquals("B", getIfAbsent(adHocMap, "b", "B"));
		assertFalse(adHocMap.containsKey("b"));
		assertEquals(1, adHocMap.size());
	}
	
	@Test
	public void linkedHashMapPreservesOrder() {
		Map<String, String> linkedHashMap = newLinkedHashMap();
		linkedHashMap.put("a", "A");
		linkedHashMap.put("0", "10");
		linkedHashMap.put("-", "+");
		linkedHashMap.put(".", ":");
		linkedHashMap.put("B", "a");
		linkedHashMap.put("A", "a");
		List<String> linkedHashMapKeys = new LinkedList<String>(linkedHashMap.keySet());
		assertEquals(linkedHashMapKeys.get(0), "a");
		assertEquals(linkedHashMapKeys.get(1), "0");
		assertEquals(linkedHashMapKeys.get(2), "-");
		assertEquals(linkedHashMapKeys.get(3), ".");
		assertEquals(linkedHashMapKeys.get(4), "B");
		assertEquals(linkedHashMapKeys.get(5), "A");
	}
	
	@Test
	public void copyOfMapRetainsMapClass() {
		Map<String, String> copy;
		Map<String, String> hashMap = newHashMap();
		Map<String, String> linkedHashMap = newLinkedHashMap();
		Map<String, String> identityHashMap = newIdentityHashMap(10);
		copy = copyOf(hashMap);
		assertEquals(copy, hashMap);
		assertEquals(HashMap.class, copy.getClass());
		copy = copyOf(linkedHashMap);
		assertEquals(copy, linkedHashMap);
		assertEquals(LinkedHashMap.class, copy.getClass());
		copy = copyOf(identityHashMap);
		assertEquals(copy, identityHashMap);
		assertEquals(IdentityHashMap.class, copy.getClass());
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void reduceMapToSelectedKeys() {
		Map<String, Integer> map = newHashMap(asList("a", "b", "c", "d"), asList(1, 2, 3, 4));
		assertEquals(0, extractedWithKeys((Collection) asList(), map).size());
		assertEquals(1, extractedWithKeys(asList("a"), map).size());
		assertTrue(extractedWithKeys(asList("a"), map).containsKey("a"));
		assertEquals(1, extractedWithKeys(asList("a"), map).get("a").intValue());
		assertEquals(1, extractedWithKeys(asList("a", "x", "y", "z"), map).size());
		assertEquals(2, extractedWithKeys(asList("a", "x", "c", "y"), map).size());
		assertTrue(extractedWithKeys(asList("a", "x", "c", "y"), map).containsKey("a"));
		assertTrue(extractedWithKeys(asList("a", "x", "c", "y"), map).containsKey("c"));
		assertEquals(map, extractedWithKeys(asList("a", "b", "c", "d"), map));
	}
}
