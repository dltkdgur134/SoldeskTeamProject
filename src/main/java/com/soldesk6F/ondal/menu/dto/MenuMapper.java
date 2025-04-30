package com.soldesk6F.ondal.menu.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuMapper {

	private static final String SEPARATOR = ",";

	public static String listToJoinedString(List<String> list) {
		if (list == null || list.isEmpty()) return "";
		return String.join(SEPARATOR, list);
	}

	public static String priceListToJoinedString(List<String> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(s -> (s == null || s.isBlank()) ? "0" : s)
			.map(Integer::parseInt)
			.map(String::valueOf)
			.collect(Collectors.joining(SEPARATOR));
	}

	public static List<String> joinedStringToList(String joined) {
		if (joined == null || joined.trim().isEmpty()) return List.of();
		return Arrays.asList(joined.split(SEPARATOR));
	}

	public static List<Integer> joinedPriceStringToIntList(String joined) {
		if (joined == null || joined.trim().isEmpty()) return List.of();
		return Arrays.stream(joined.split(SEPARATOR))
			.map(String::trim)
			.map(s -> s.isBlank() ? "0" : s)
			.map(Integer::parseInt)
			.collect(Collectors.toList());
	}
	
	// List<String> → "A,B,C"
	public static String listToString(List<?> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(Object::toString)
			.collect(Collectors.joining(","));
	}

	// "A,B,C" → List<String>
	public static List<String> stringToStringList(String str) {
		if (str == null || str.trim().isEmpty()) return List.of();
		return Arrays.asList(str.split(","));
	}

	// "1000,2000" → List<Integer>
	public static List<Integer> stringToIntList(String str) {
		if (str == null || str.trim().isEmpty()) return List.of();
		return Arrays.stream(str.split(","))
			.map(String::trim)
			.map(Integer::parseInt)
			.collect(Collectors.toList());
	}

	// List<Integer> → "1000,2000"
	public static String intListToString(List<Integer> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(","));
	}
}