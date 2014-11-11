package poiconvert;


public class SimpleRule{
	// 输入表格
	int sourceSheetIndex = -1;
	// 输入列
	int sourceColumnIndex = -1;
	// 输出表格
	int targetSheetIndex = 0;
	// 输出列
	int targetColumnIndex = 0;
	// 默认值
	String defaultValue = null;
	// 默认值类型
	String defaultValueType = "s";
	// 序列号开始符号
	double sequenceStartNumber = -1;
	// 序列号间隔
	double sequenceStep = -1;
	// 序列号映射列，此列变换才会引起序列号增长，-1表示不需要，其他正整数表示同表中的列
	int sequenceIndex = -1;
}