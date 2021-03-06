package poiconvert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Convertor {

	private Map<String, String> config;
	private Map<Integer, List<SimpleRule>> rules;
	private Map<File, File> resultMap;
	private List<SimpleRule> defaults;
	private List<SimpleRule> sequences;

	public Convertor(Map<String, String> config,
			Map<Integer, List<SimpleRule>> rules, List<SimpleRule> defaults,
			List<SimpleRule> sequences) {
		this.config = config;
		this.rules = rules;
		this.defaults = defaults;
		this.sequences = sequences;
	}

	public Map<File, File> getResultMap() {
		return resultMap;
	}

	public Map<String, String> getConfig() {
		return config;
	}

	public String convert() {
		resultMap = new HashMap<File, File>();
		// 源文件的正则表达式，文件名符合此正则表达式的文件会被自动转化
		String sourceFileRegex = this.config.get("sourceFile");
		if (StringUtils.isBlank(sourceFileRegex)) {
			return "源文件名称表达式不能为空";
		}
		// 目标文件的前缀，输入的文件名称 = 前缀 + '-' +源文件名
		String targetFilePrefix = this.config.get("targetFilePrefix");
		if (StringUtils.isBlank(targetFilePrefix)) {
			return "目标文件前缀不能为空";
		}
		// 输入目录
		String sourceFolderName = this.config.get("sourceFolder");
		if (StringUtils.isBlank(sourceFolderName)) {
			return "输入目录不能为空";
		}
		File sourceFolder = new File(sourceFolderName);
		if (!sourceFolder.exists() || sourceFolder.isFile()) {
			return "输入目录不存在";
		}
		// 输出目录
		String targetFolderName = this.config.get("targetFolder");
		if (StringUtils.isBlank(targetFolderName)) {
			return "输出目录不能为空";
		}
		File targetFolder = new File(targetFolderName);
		if (targetFolder.isFile()) {
			return "输出目录是文件！";
		}
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		// 输出模板
		String targetTemplate = this.config.get("targetTemplate");
		if (StringUtils.isBlank(targetTemplate)) {
			return "模板文件名称为空";
		}
		File targetTemplateFile = new File(targetTemplate);
		if (!targetTemplateFile.exists()) {
			return "模板文件不存在";
		}
		// 跳过几行
		String sourceSkipRowStr = this.config.get("sourceSkipRows");
		int sourceSkipRows;
		if (StringUtils.isBlank(sourceSkipRowStr)) {
			sourceSkipRows = 0;
		} else {
			sourceSkipRows = Integer.parseInt(sourceSkipRowStr);
		}
		String targetSkipRowStr = this.config.get("targetSkipRows");
		int targetSkipRows;
		if (StringUtils.isBlank(targetSkipRowStr)) {
			targetSkipRows = 0;
		} else {
			targetSkipRows = Integer.parseInt(targetSkipRowStr);
		}

		FileFilter filter = new RegexFileFilter(sourceFileRegex);
		File[] sourceFiles = sourceFolder.listFiles(filter);

		for (File file : sourceFiles) {
			File targetFile = new File(targetFolder, targetFilePrefix
					+ file.getName());
			System.out.print("开始转化文件:" + file.getName() + " -> "
					+ targetFile.getName());
			boolean flag = convertSingle(file, targetFile, targetTemplateFile,
					this.rules, this.defaults, sourceSkipRows, targetSkipRows);
			if (flag) {
				resultMap.put(file, targetFile);
				System.out.println(" 转化成功");
			} else {
				resultMap.put(file, null);
				System.out.println(" 转化失败");
			}
		}

		return "运行成功";
	}

	public static Convertor createByConfigFile(File config)
			throws FileNotFoundException, IOException {
		Map<String, String> configMap = new HashMap<>();
		Map<Integer, List<SimpleRule>> rules = new HashMap<>();
		List<SimpleRule> defaults = new ArrayList<>();
		List<SimpleRule> sequences = new ArrayList<>();
		String line = null;
		boolean ruleBegin = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				// 跳过注释和空行
				if (StringUtils.isBlank(line)
						|| StringUtils.startsWith(line, "//")) {
					continue;
				}
				if (StringUtils.equals(line, "Rule")) {
					ruleBegin = true;
					continue;
				}
				if (ruleBegin) {
					String[] parts = line.split("=");
					SimpleRule rule = new SimpleRule();
					// 默认值情况
					if ((StringUtils.startsWith(parts[0], "[")
							|| StringUtils.startsWith(parts[0], "s[")
							|| StringUtils.startsWith(parts[0], "d[") || StringUtils
								.startsWith(parts[0], "t["))
							&& StringUtils.endsWith(parts[0], "]")) {
						if (StringUtils.startsWith(parts[0], "[")) {
							rule.defaultValue = StringUtils.substring(parts[0],
									1, parts[0].length() - 1).trim();
						} else {
							rule.defaultValue = StringUtils.substring(parts[0],
									2, parts[0].length() - 1).trim();
							rule.defaultValueType = StringUtils.substring(
									parts[0], 0, 1);
						}
						// if (StringUtils.startsWith(parts[0], "s[")) {
						// rule.defaultValue = StringUtils.substring(parts[0],
						// 2, parts[0].length() - 1).trim();
						// } else if (StringUtils.startsWith(parts[0], "d[")) {
						// rule.defaultValue = StringUtils.substring(parts[0],
						// 2, parts[0].length() - 1).trim();
						// rule.defaultValueType = "d";
						// } else if(StringUtils.startsWith(parts[0], "t[")){
						// rule.defaultValue = StringUtils.substring(parts[0],
						// 2, parts[0].length() - 1).trim();
						// rule.defaultValueType = "t";
						// }

						defaults.add(rule);
					} else if (StringUtils.startsWith(parts[0], "#")
							&& StringUtils.endsWith(parts[0], "#")) {
						// 序列号情况
						String sequence = StringUtils.substring(parts[0], 1,
								parts[0].length() - 1).trim();
						String sparts[] = sequence.split("\\|");
						double startNumber = Double.parseDouble(sparts[0]);
						double step = Double.parseDouble(sparts[1]);
						rule.sequenceStartNumber = startNumber;
						rule.sequenceStep = step;
						if (sparts.length == 3) {
							int index = Integer.parseInt(sparts[2]);
							rule.sequenceIndex = index;
						}
						sequences.add(rule);
					} else {
						// 列转换
						String[] sIndexs = parts[0].split("\\.");
						rule.sourceSheetIndex = Integer.parseInt(sIndexs[0]);
						rule.sourceColumnIndex = Integer.parseInt(sIndexs[1]);
						List<SimpleRule> ruleList = rules
								.get(rule.sourceSheetIndex);
						if (ruleList == null) {
							ruleList = new ArrayList<SimpleRule>();
							rules.put(rule.sourceSheetIndex, ruleList);
						}
						ruleList.add(rule);
					}
					String[] tIndexs = parts[1].split("\\.");
					rule.targetSheetIndex = Integer.parseInt(tIndexs[0]);
					rule.targetColumnIndex = Integer.parseInt(tIndexs[1]);

				} else {
					String[] parts = line.split("=");
					configMap.put(StringUtils.trim(parts[0]),
							StringUtils.trim(parts[1]));
				}
			}
		}

		return new Convertor(configMap, rules, defaults, sequences);
	}

	private boolean convertSingle(File file, File targetFile,
			File targetTemplate, Map<Integer, List<SimpleRule>> rules,
			List<SimpleRule> defaults, int sourceSkipRows, int targetSkipRows) {
		try {
			targetFile.createNewFile();
			try (FileInputStream sourceInput = new FileInputStream(file);
					FileOutputStream targetOutput = new FileOutputStream(
							targetFile);
					FileInputStream tin = new FileInputStream(targetTemplate)) {
				IOUtils.copy(tin, targetOutput);
				targetOutput.close();
				HSSFWorkbook book = new HSSFWorkbook(sourceInput);
				int srow = sourceSkipRows;
				int trow = targetSkipRows;
				try (FileInputStream in = new FileInputStream(targetFile)) {
					HSSFWorkbook target = new HSSFWorkbook(in);
					// 处理转换规则
					for (Entry<Integer, List<SimpleRule>> entry : rules
							.entrySet()) {
						int sSheet = entry.getKey();
						HSSFSheet inSheet = book.getSheetAt(sSheet);
						int rowCount = inSheet.getLastRowNum() + 1;
						for (int i = srow, j = trow; i < rowCount; i++, j++) {
							HSSFRow row = inSheet.getRow(i);
							List<SimpleRule> ruleList = entry.getValue();
							for (SimpleRule simpleRule : ruleList) {
								int sourceColumnIndex = simpleRule.sourceColumnIndex;
								int targetSheetIndex = simpleRule.targetSheetIndex;
								int targetColumnIndex = simpleRule.targetColumnIndex;
								HSSFCell cell = row.getCell(sourceColumnIndex);
								HSSFSheet tSheet = target
										.getSheetAt(targetSheetIndex);
								HSSFRow tRow = tSheet.getRow(j);
								if (tRow == null) {
									tRow = tSheet.createRow(j);
								}
								HSSFCell tCell = tRow
										.getCell(targetColumnIndex);
								if (tCell == null) {
									tCell = tRow
											.createCell((short) targetColumnIndex);
								}
								copyCell(cell, tCell);
							}
						}
					}
					// 处理默认值
					for (SimpleRule simpleRule : defaults) {
						int targetSheetIndex = simpleRule.targetSheetIndex;
						int targetColumnIndex = simpleRule.targetColumnIndex;
						HSSFSheet tSheet = target.getSheetAt(targetSheetIndex);
						int rowCount = tSheet.getLastRowNum() + 1;
						for (int i = targetSkipRows; i < rowCount; i++) {
							HSSFRow row = tSheet.getRow(i);
							if(row == null){
								System.out.println(simpleRule + " " + i + " " + targetColumnIndex);
							}
							HSSFCell cell = row.getCell(targetColumnIndex);
							if (cell == null) {
								cell = row
										.createCell((short) targetColumnIndex);
							}
							String defaultValue = simpleRule.defaultValue;
							try {
								if (StringUtils.equals("d",
										simpleRule.defaultValueType)) {
									double d = Double.parseDouble(defaultValue);
									cell.setCellValue(d);
								} else if (StringUtils.equals("t",
										simpleRule.defaultValueType)) {
									// 当天日期
									if (StringUtils
											.equals(defaultValue, "date")) {
										Calendar calendar = Calendar
												.getInstance();
										calendar.set(Calendar.HOUR_OF_DAY, 0);
										calendar.set(Calendar.MINUTE, 0);
										calendar.set(Calendar.SECOND, 0);
										calendar.set(Calendar.MILLISECOND, 0);
										cell.setCellValue(calendar);
									}else if(StringUtils
											.equals(defaultValue, "datetime")){
										Calendar calendar = Calendar
												.getInstance();
										cell.setCellValue(calendar);
									}else{
										Calendar calendar = Calendar
												.getInstance();
										String parts[] = defaultValue.split(" ");
										String dateParts[] = parts[0].split("/");
										calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
										calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
										calendar.set(Calendar.DATE, Integer.parseInt(dateParts[2]));
										if(parts.length == 1){
											calendar.set(Calendar.HOUR_OF_DAY, 0);
											calendar.set(Calendar.MINUTE, 0);
											calendar.set(Calendar.SECOND, 0);
										}else{
											String timeParts[] = parts[1].split(":");
											calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
											calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
											calendar.set(Calendar.SECOND, Integer.parseInt(timeParts[2]));
										}
										calendar.set(Calendar.MILLISECOND, 0);
										cell.setCellValue(calendar);
									}
								} else {
									cell.setCellValue(new HSSFRichTextString(
											defaultValue));
								}
							} catch (Exception e) {
								cell.setCellValue(new HSSFRichTextString(
										defaultValue));
							}
						}
					}
					// 处理序列号
					for (SimpleRule seq : sequences) {
						int targetSheetIndex = seq.targetSheetIndex;
						int targetColumnIndex = seq.targetColumnIndex;
						HSSFSheet tSheet = target.getSheetAt(targetSheetIndex);
						int rowCount = tSheet.getLastRowNum() + 1;
						double seqNum = seq.sequenceStartNumber;
						double step = seq.sequenceStep;
						int seqIndex = seq.sequenceIndex;
						HashMap<String, Double> indexMap = new HashMap<>();
						for (int i = targetSkipRows; i < rowCount; i++) {
							HSSFRow row = tSheet.getRow(i);
							HSSFCell cell = row.getCell(targetColumnIndex);
							if (cell == null) {
								cell = row
										.createCell((short) targetColumnIndex);
							}
							if (seqIndex == -1) {
								// 不需要映射列的情况
								cell.setCellValue(seqNum);
								seqNum += step;
							} else {
								HSSFCell indexCell = row.getCell(seqIndex);
								if (indexCell == null) {
									cell.setCellValue(seqNum);
									seqNum += step;
								} else {
									String index = indexCell
											.getRichStringCellValue()
											.getString();
									Double indexSeq = indexMap.get(index);
									if (indexSeq == null) {
										cell.setCellValue(seqNum);
										indexMap.put(index, seqNum);
										seqNum += step;
									} else {
										cell.setCellValue(indexSeq);
									}
								}
							}
						}
					}
					try (FileOutputStream output = new FileOutputStream(
							targetFile)) {
						target.write(output);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void copyCell(HSSFCell source, HSSFCell target) {
		int type = source.getCellType();
		switch (type) {
		case HSSFCell.CELL_TYPE_BLANK:
			return;
		case HSSFCell.CELL_TYPE_ERROR:
			target.setCellErrorValue(source.getErrorCellValue());
			return;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			target.setCellValue(source.getBooleanCellValue());
			return;
		case HSSFCell.CELL_TYPE_FORMULA:
			target.setCellFormula(source.getCellFormula());
			return;
		case HSSFCell.CELL_TYPE_NUMERIC:
			target.setCellValue(source.getNumericCellValue());
			return;
		case HSSFCell.CELL_TYPE_STRING:
			target.setCellValue(source.getRichStringCellValue());
			return;
		}

	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(new File("testSource/PLM导出格式-xx导物料.xls")));
		System.out.println(book.getSheetAt(0).getLastRowNum());
	}
}