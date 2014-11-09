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
			Map<Integer, List<SimpleRule>> rules, List<SimpleRule> defaults,List<SimpleRule> sequences) {
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
		// Դ�ļ���������ʽ���ļ������ϴ�������ʽ���ļ��ᱻ�Զ�ת��
		String sourceFileRegex = this.config.get("sourceFile");
		if (StringUtils.isBlank(sourceFileRegex)) {
			return "Դ�ļ����Ʊ��ʽ����Ϊ��";
		}
		// Ŀ���ļ���ǰ׺��������ļ����� = ǰ׺ + '-' +Դ�ļ���
		String targetFilePrefix = this.config.get("targetFilePrefix");
		if (StringUtils.isBlank(targetFilePrefix)) {
			return "Ŀ���ļ�ǰ׺����Ϊ��";
		}
		// ����Ŀ¼
		String sourceFolderName = this.config.get("sourceFolder");
		if (StringUtils.isBlank(sourceFolderName)) {
			return "����Ŀ¼����Ϊ��";
		}
		File sourceFolder = new File(sourceFolderName);
		if (!sourceFolder.exists() || sourceFolder.isFile()) {
			return "����Ŀ¼������";
		}
		// ���Ŀ¼
		String targetFolderName = this.config.get("targetFolder");
		if (StringUtils.isBlank(targetFolderName)) {
			return "���Ŀ¼����Ϊ��";
		}
		File targetFolder = new File(targetFolderName);
		if (targetFolder.isFile()) {
			return "���Ŀ¼���ļ���";
		}
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		// ���ģ��
		String targetTemplate = this.config.get("targetTemplate");
		if (StringUtils.isBlank(targetTemplate)) {
			return "ģ���ļ�����Ϊ��";
		}
		File targetTemplateFile = new File(targetTemplate);
		if (!targetTemplateFile.exists()) {
			return "ģ���ļ�������";
		}
		// ��������
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
			System.out.print("��ʼת���ļ�:" + file.getName() + " -> "
					+ targetFile.getName());
			boolean flag = convertSingle(file, targetFile, targetTemplateFile,
					this.rules, this.defaults, sourceSkipRows, targetSkipRows);
			if (flag) {
				resultMap.put(file, targetFile);
				System.out.println(" ת���ɹ�");
			} else {
				resultMap.put(file, null);
				System.out.println(" ת��ʧ��");
			}
		}

		return "���гɹ�";
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
				// ����ע�ͺͿ���
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
					// Ĭ��ֵ���
					if (StringUtils.startsWith(parts[0], "[")
							&& StringUtils.endsWith(parts[0], "]")) {
						rule.defaultValue = StringUtils.substring(parts[0], 1,
								parts[0].length() - 1).trim();
						defaults.add(rule);
					} else if(StringUtils.startsWith(parts[0], "#") && StringUtils.endsWith(parts[0], "#")){
						// ���к����
						String sequence = StringUtils.substring(parts[0], 1,parts[0].length()-1).trim();
						String sparts[] = sequence.split("\\|");
						double startNumber = Double.parseDouble(sparts[0]);
						double step = Double.parseDouble(sparts[1]);
						rule.sequenceStartNumber = startNumber;
						rule.sequenceStep = step;
						sequences.add(rule);
					}
					else {
						// ��ת��
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

		return new Convertor(configMap, rules, defaults,sequences);
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
					// ����ת������
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
					// ����Ĭ��ֵ
					for (SimpleRule simpleRule : defaults) {
						int targetSheetIndex = simpleRule.targetSheetIndex;
						int targetColumnIndex = simpleRule.targetColumnIndex;
						HSSFSheet tSheet = target.getSheetAt(targetSheetIndex);
						int rowCount = tSheet.getLastRowNum();
						for (int i = targetSkipRows; i < rowCount; i++) {
							HSSFRow row = tSheet.getRow(i);
							HSSFCell cell = row.getCell(targetColumnIndex);
							if (cell == null) {
								cell = row
										.createCell((short) targetColumnIndex);
							}
							String defaultValue = simpleRule.defaultValue;
							try{
								double d = Double.parseDouble(defaultValue);
								cell.setCellValue(d);
							}catch(Exception e){
								cell.setCellValue(new HSSFRichTextString(
										defaultValue));
							}
						}
					}
					// �������к�
					for(SimpleRule seq : sequences){
						int targetSheetIndex = seq.targetSheetIndex;
						int targetColumnIndex = seq.targetColumnIndex;
						HSSFSheet tSheet = target.getSheetAt(targetSheetIndex);
						int rowCount = tSheet.getLastRowNum();
						double seqNum = seq.sequenceStartNumber;
						double step = seq.sequenceStep;
						for (int i = targetSkipRows; i < rowCount; i++) {
							HSSFRow row = tSheet.getRow(i);
							HSSFCell cell = row.getCell(targetColumnIndex);
							if (cell == null) {
								cell = row
										.createCell((short) targetColumnIndex);
							}
							cell.setCellValue(seqNum);
							seqNum += step;
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
