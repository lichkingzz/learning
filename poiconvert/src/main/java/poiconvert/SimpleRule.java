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
	// 序列号开始符号
	double sequenceStartNumber = -1;
	// 序列号间隔
	double sequenceStep = -1;
}