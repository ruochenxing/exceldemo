package com.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;

public class AnnotationUtil {
	private static Map<String, Field> gambleFileInfoFieldMap;
	private static Map<String, Field> updateGambleFileInfoFieldMap;
	private static Map<String, Field> downSaleFileInfoFieldMap;
	private static Map<String, Field> downGamleFileInfoFieldMap;

	public void init() {
		gambleFileInfoFieldMap = getFieldMap(GambleFileInfo.class);
		downGamleFileInfoFieldMap = getFieldMap(DownGamleFileInfo.class).entrySet().stream()
				.sorted(Comparator.comparingInt(a -> a.getValue().getAnnotation(ExcelField.class).rank())).collect(
						Collectors.toMap(a -> a.getKey(), a -> a.getValue(), (key1, key2) -> key2, LinkedHashMap::new));
	}

	public static Field getField(String fieldName, Class<?> sheetClass) {
		if (sheetClass.equals(GambleFileInfo.class)) {
			return gambleFileInfoFieldMap.get(fieldName);
		}
		return getDownGamleFileInfoFieldMap(fieldName);
	}

	public static Field getGambleFileInfoField(String fieldName) {
		return gambleFileInfoFieldMap.get(fieldName);
	}

	public static Field getUpdateGambleFileInfoField(String fieldName) {
		return updateGambleFileInfoFieldMap.get(fieldName);
	}

	public static Field getDownSaleFileInfoFieldMap(String fieldName) {
		return downSaleFileInfoFieldMap.get(fieldName);
	}

	public static Field getDownGamleFileInfoFieldMap(String fieldName) {
		return downGamleFileInfoFieldMap.get(fieldName);
	}

	private Map<String, Field> getFieldMap(Class<?> sheetClass) {
		// sheet field
		Map<String, Field> fieldHashMap = new HashMap<>();
		List<Field> fieldList = new ArrayList<>();

		if (sheetClass.getDeclaredFields() != null && sheetClass.getDeclaredFields().length > 0) {
			fieldList.addAll(Arrays.asList(sheetClass.getDeclaredFields()));
		}
		Class<?> superSheetClass = sheetClass.getSuperclass();
		if (superSheetClass.getDeclaredFields() != null && superSheetClass.getDeclaredFields().length > 0) {
			fieldList.addAll(Arrays.asList(superSheetClass.getDeclaredFields()));
		}
		for (Field field : fieldList) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			ExcelField excelField = field.getAnnotation(ExcelField.class);
			field.setAccessible(true);
			fieldHashMap.put(excelField.name(), field);
		}
		if (MapUtils.isEmpty(fieldHashMap)) {
		}
		return fieldHashMap;
	}

	public static Map<String, Field> getDownSaleFileInfoFieldMap() {
		return downSaleFileInfoFieldMap;
	}

	public static <T> Map<String, Field> getClassFieldMap(Class<T> sheetClass) {
		return downGamleFileInfoFieldMap;
	}
}
