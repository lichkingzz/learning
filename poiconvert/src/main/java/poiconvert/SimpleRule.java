package poiconvert;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class SimpleRule{
	// ������
	int sourceSheetIndex = -1;
	// ������
	int sourceColumnIndex = -1;
	// ������
	int targetSheetIndex = 0;
	// �����
	int targetColumnIndex = 0;
	// Ĭ��ֵ
	String defaultValue = null;
	// Ĭ��ֵ����
	String defaultValueType = "s";
	// ���кſ�ʼ����
	double sequenceStartNumber = -1;
	// ���кż��
	double sequenceStep = -1;
	// ���к�ӳ���У����б任�Ż��������к�������-1��ʾ����Ҫ��������������ʾͬ���е���
	int sequenceIndex = -1;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}