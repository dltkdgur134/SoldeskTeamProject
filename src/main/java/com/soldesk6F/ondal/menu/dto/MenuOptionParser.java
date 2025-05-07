package com.soldesk6F.ondal.menu.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuOptionParser {

	public static String extractGroupName(String optionString) {
		if (optionString == null || !optionString.contains(":")) return "";
		return optionString.substring(0, optionString.indexOf(":")).trim();
	}

	public static List<String> parseOptionNames(String optionString) {
		if (optionString == null || optionString.isBlank()) return List.of();
		return MenuMapper.extractOptionList(optionString);  // ✅ 그룹명 제외한 옵션만 리스트로
	}

	public static List<Integer> parseOptionPrices(String priceString) {
		if (priceString == null || priceString.isBlank()) return List.of();
		return Arrays.stream(priceString.split("@@__@@"))
			.map(String::trim)
			.map(s -> s.isBlank() ? "0" : s)
			.map(s -> {
				try {
					return Integer.parseInt(s.replaceAll("[^0-9]", ""));
				} catch (NumberFormatException e) {
					System.err.println("❌ parseOptionPrices 실패: " + s);
					return 0;
				}
			})
			.collect(Collectors.toList());
	}
}