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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Convertor {

	private Map<String, String> config;
	private Map<Integer, List<SimpleRule>> rules;

	public Convertor(Map<String, String> config,
			Map<Integer, List<SimpleRule>> rules) {
		this.config = config;
		this.rules = rules;
	}

	public String convert() {
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
		Map<File, File> resultMap = new HashMap<File, File>();
		for (File file : sourceFiles) {
			File targetFile = new File(targetFolder, targetFilePrefix + "-"
					+ file.getName());
			System.out.print("开始转化文件:" + file.getName() + " -> "
					+ targetFile.getName());
			boolean flag = convertSingle(file, targetFile, targetTemplateFile,
					this.rules, sourceSkipRows, targetSkipRows);
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

		String line = null;
		boolean ruleBegin = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				// 跳过注释和空行
				if(StringUtils.isBlank(line) || StringUtils.startsWith(line, "//")){
					continue;
				}
				if (StringUtils.equals(line, "Rule")) {
					ruleBegin = true;
					continue;
				}
				if(ruleBegin){
					String[] parts = line.split("=");
					SimpleRule rule = new SimpleRule();
					String[] sIndexs = parts[0].split("\\.");
					rule.sourceSheetIndex = Integer.parseInt(sIndexs[0]);
					rule.sourceColumnIndex = Integer.parseInt(sIndexs[1]);
					String[] tIndexs = parts[1].split("\\.");
					rule.targetSheetIndex = Integer.parseInt(tIndexs[0]);
					rule.targetColumnIndex = Integer.parseInt(tIndexs[1]);
					
					List<SimpleRule> ruleList = rules.get(rule.sourceSheetIndex);
					if(ruleList == null){
						ruleList = new ArrayList<SimpleRule>();
						rules.put(rule.sourceSheetIndex, ruleList);
					}
					ruleList.add(rule);
				}else{
					String[] parts = line.split("=");
					configMap.put(StringUtils.trim(parts[0]), StringUtils.trim(parts[1]));
				}
			}
		}

		return new Convertor(configMap, rules);
	}

	public static void main(String[] args) {
		Convertor convertor = new Convertor(null, null);
		Map<Integer, List<SimpleRule>> rules = new HashMap<>();
		ArrayList<SimpleRule> ruleList = new ArrayList<>();
		for (int i = 0; i < 11; i++) {
			SimpleRule rule = new SimpleRule();
			rule.sourceSheetIndex = 0;
			rule.sourceColumnIndex = 0;
			rule.targetSheetIndex = i;
			rule.targetColumnIndex = 0;
			ruleList.add(rule);
		}
		rules.put(0, ruleList);

		convertor.convertSingle(new File("source.xls"), new File("target.xls"),
				new File("template.xls"), rules, 1, 1);
	}

	private boolean convertSingle(File file, File targetFile,
			File targetTemplate, Map<Integer, List<SimpleRule>> rules,
			int sourceSkipRows, int targetSkipRows) {
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
					for (Entry<Integer, List<SimpleRule>> entry : rules
							.entrySet()) {
						int sSheet = entry.getKey();
						HSSFSheet inSheet = book.getSheetAt(sSheet);
						int rowCount = inSheet.getLastRowNum();
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
}
