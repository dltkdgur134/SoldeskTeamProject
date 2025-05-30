package com.soldesk6F.ondal.menu.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.soldesk6F.ondal.menu.entity.Menu;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MenuMapper {

	private static final String SEPARATOR = "@@__@@";

	public static MenuDto convertToDto(Menu menu) {
		log.info("📦 raw menuOptions1: {}", menu.getMenuOptions1());
		log.info("🎯 추출된 그룹명: {}", MenuMapper.extractGroupName(menu.getMenuOptions1()));
		log.info("✅ 옵션 목록: {}", MenuOptionParser.parseOptionNames(menu.getMenuOptions1()));
		return MenuDto.builder()
			.menuId(menu.getMenuId())
			.storeId(menu.getStore().getStoreId())
			.menuName(menu.getMenuName())
			.description(menu.getDescription())
			.price(menu.getPrice())
			.menuImg(menu.getMenuImg())
			
			.menuOptions1(MenuOptionParser.parseOptionNames(menu.getMenuOptions1()))
			.menuOptions1Price(MenuOptionParser.parseOptionPrices(menu.getMenuOptions1Price()))
			.menuOptions2(MenuOptionParser.parseOptionNames(menu.getMenuOptions2()))
			.menuOptions2Price(MenuOptionParser.parseOptionPrices(menu.getMenuOptions2Price()))
			.menuOptions3(MenuOptionParser.parseOptionNames(menu.getMenuOptions3()))
			.menuOptions3Price(MenuOptionParser.parseOptionPrices(menu.getMenuOptions3Price()))
			.menuOptions1GroupName(MenuMapper.extractGroupName(menu.getMenuOptions1()))
			.menuOptions2GroupName(MenuMapper.extractGroupName(menu.getMenuOptions2()))
			.menuOptions3GroupName(MenuMapper.extractGroupName(menu.getMenuOptions3()))
			
			.menuCategory(menu.getMenuCategory() != null ? menu.getMenuCategory().getCategoryName() : null)
			.menuCategoryId(menu.getMenuCategory() != null ? menu.getMenuCategory().getId() : null)
			.build();
	}
	
	public static String listToJoinedString(List<String> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(s -> s == null ? "" : s.trim())
			.collect(Collectors.joining(SEPARATOR));
	}

	public static String priceListToJoinedString(List<String> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.flatMap(s -> Arrays.stream(s.split(SEPARATOR)))
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.map(s -> {
				try {
					return Integer.parseInt(s.replaceAll("[^0-9]", ""));
				} catch (NumberFormatException e) {
					System.err.println("⚠️ priceListToJoinedString 변환 실패: '" + s + "' → 0으로 처리");
					return 0;
				}
			})
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
			.filter(s -> !s.isEmpty())
			.map(s -> {
				try {
					return Integer.parseInt(s.replaceAll("[^0-9]", ""));
				} catch (NumberFormatException e) {
					System.err.println("❌ joinedPriceStringToIntList 변환 실패: '" + s + "'");
					return 0;
				}
			})
			.collect(Collectors.toList());
	}

	public static String listToString(List<?> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(Object::toString)
			.collect(Collectors.joining(SEPARATOR));
	}

	public static List<String> stringToStringList(String str) {
		if (str == null || str.trim().isEmpty()) return List.of();
		return Arrays.asList(str.split(SEPARATOR));
	}

	public static List<Integer> stringToIntList(String str) {
		if (str == null || str.trim().isEmpty()) return List.of();
		return Arrays.stream(str.split(SEPARATOR))
			.map(String::trim)
			.flatMap(s -> Arrays.stream(s.split(SEPARATOR)))
			.map(s -> {
				try {
					return Integer.parseInt(s.replaceAll("[^0-9]", ""));
				} catch (NumberFormatException e) {
					System.err.println("❌ stringToIntList 실패: " + s);
					return 0;
				}
			})
			.collect(Collectors.toList());
	}

	public static String intListToString(List<Integer> list) {
		if (list == null || list.isEmpty()) return "";
		return list.stream()
			.map(String::valueOf)
			.collect(Collectors.joining(SEPARATOR));
	}
	
	public static String combineGroupNameAndOptions(String groupName, List<String> options) {
		if (groupName == null || groupName.isBlank()) return listToJoinedString(options);
		String cleanGroupName = groupName.trim();
		String joinedOptions = listToJoinedString(options);
		return cleanGroupName + ":" + joinedOptions;
	}
	
	public static String extractGroupName(String combined) {
		if (combined == null || !combined.contains(":")) return "";
		return combined.substring(0, combined.indexOf(":")).trim();
	}

	public static List<String> extractOptionList(String combined) {
		if (combined == null || !combined.contains(":")) return joinedStringToList(combined);
		String optionsPart = combined.substring(combined.indexOf(":") + 1);
		return joinedStringToList(optionsPart);
	}
}
