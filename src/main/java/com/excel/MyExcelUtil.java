package com.excel;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

public class MyExcelUtil {
	private static final List<String> EXCEL_FILES = new ArrayList<>(Arrays.asList(".xls", ".xlsx"));
	private static final int sheetIndex = 0;

	public static List<GambleFileInfo> getGambleFileInfoList(MultipartFile file) throws IOException {
		return getSheetList(file, GambleFileInfo.class);
	}

	private static <T> List<T> getSheetList(MultipartFile file, Class<T> sheetClass) throws IOException {
		String fileName = file.getOriginalFilename();
		checkFileExt(fileName);
		List<T> sheetList = new Vector<>();
		Map<Integer, Field> fieldMaps = new HashMap<>();
		if (fileName.toLowerCase().endsWith("xls")) {
			ExcelUtil.read03BySax(file.getInputStream(), sheetIndex,
					(int sheetIndex, int rowIndex, List<Object> rowlist) -> {
						read(rowIndex, rowlist, sheetList, fieldMaps, sheetClass);
					});
		} else {
			ExcelUtil.read07BySax(file.getInputStream(), sheetIndex,
					(int sheetIndex, int rowIndex, List<Object> rowlist) -> {
						read(rowIndex, rowlist, sheetList, fieldMaps, sheetClass);
					});

		}
		return sheetList;
	}

	private static <T> void read(int rowIndex, List<Object> rowlist, List<T> sheetList, Map<Integer, Field> fieldMaps,
			Class<T> sheetClass) {
		if (rowIndex == 0) {
			for (int i = 0; i < rowlist.size(); i++) {
				Field field = AnnotationUtil.getField((String) rowlist.get(i), sheetClass);
				fieldMaps.put(i, field);
			}
		} else {
			T sheet = null;
			try {
				sheet = sheetClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
			}
			// 首列为空，表示该行无数据，跳过
			if (StringUtils.isEmpty(rowlist.get(0).toString()))
				return;
			for (int i = 0; i < rowlist.size(); i++) {
				try {
					Field field = fieldMaps.get(i);
					if (StringUtils.equals(field.getType().getCanonicalName(), "java.lang.String")) {
						field.set(sheet, StringUtils.trim(rowlist.get(i).toString()));
					} else if (StringUtils.equals(field.getType().getCanonicalName(), "java.lang.Long")) {
						BigDecimal bigDecimal = new BigDecimal(StringUtils.trim(rowlist.get(i).toString()));
						Long value = bigDecimal.longValue();
						field.set(sheet, value);
					} else if (StringUtils.equals(field.getType().getCanonicalName(), "java.lang.Integer")) {
						BigDecimal bigDecimal = new BigDecimal(StringUtils.trim(rowlist.get(i).toString()));
						Integer value = bigDecimal.intValue();
						field.set(sheet, value);
					} else {
						field.set(sheet, rowlist.get(i));
					}
				} catch (IllegalAccessException e) {
				}
			}
			sheetList.add(sheet);
		}
	}

	/**
	 * 检查文件名后缀
	 * 
	 * @param fileName
	 * @throws BusinessException
	 */
	private static void checkFileExt(String fileName) {
		try {
			int prefixIndex = fileName.lastIndexOf(".");
			String ext = fileName.substring(prefixIndex);
			if (!EXCEL_FILES.contains(ext)) {
			}
		} catch (Exception e) {
		}
	}

	// 直接将文件流扔到响应流里面
	public static void downGambling(List<DownGamleFileInfo> infos, ServletOutputStream outputStream,
			Class<DownGamleFileInfo> downGamleFileInfoClass) throws IOException, IllegalAccessException {
		down(infos, outputStream, DownGamleFileInfo.class);
	}

	public static <T> void down(List<T> infos, ServletOutputStream outputStream, Class<T> sheetClass)
			throws IllegalAccessException, IOException {

		Map<String, Field> fieldMap = AnnotationUtil.getClassFieldMap(sheetClass);
		List<String> heads = new ArrayList<>();
		ExcelWriter writer = ExcelUtil.getWriter(true);
		for (String head : fieldMap.keySet()) {
			heads.add(head);
		}
		writer.writeHeadRow(heads);

		List<String> rows = new ArrayList<>();
		for (int i = 0; i < infos.size(); i++) {
			rows.clear();
			T fileInfo = infos.get(i);
			for (Field field : fieldMap.values()) {
				Object row = field.get(fileInfo);
				rows.add(row == null ? "" : row.toString());
			}
			writer.writeRow(rows);
		}
		writer.flush(outputStream);
		writer.close();
		outputStream.flush();
		;
		outputStream.close();
	}

}
