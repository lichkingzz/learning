package my.httpclient.utils;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ����ת������
 * 
 * @author xzz
 */
public class ConvertUtils {

	private static final Logger logger = LogManager
			.getLogger(ConvertUtils.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * ������ת��Ϊform������
	 * 
	 * @param source
	 *            ת������
	 * @return form������
	 */
	public static List<NameValuePair> toNVList(Object source) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (source == null) {
			return list;
		}
		PropertyDescriptor[] descArray = BeanUtils
				.getPropertyDescriptors(source.getClass());
		for (PropertyDescriptor desc : descArray) {
			String name = desc.getName();
			if (StringUtils.equals("class", name)) {
				continue;
			}
			String value;
			try {
				value = desc.getReadMethod().invoke(source).toString();
				list.add(new BasicNameValuePair(name, value));
			} catch (Exception e) {
				logger.error("convert to from fail:class=" + source.getClass()
						+ ";properyName=" + name, e);
			}
		}
		return list;
	}

	/**
	 * ������ת��Ϊjson�ַ���
	 * 
	 * @param source
	 *            ����
	 * @return json�ַ���
	 */
	public static String toJson(Object source) {
		if (source == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(source);
		} catch (JsonProcessingException e) {
			logger.error("to json fail:class" + source.getClass(), e);
			return null;
		}
	}
	
	/**
	 * ��jsonתΪ����
	 * 
	 * @param in ����
	 * @param T ��������
	 * @return ������
	 */
	public static <T> T readJson(InputStream in,Class<T> T){
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			if(logger.isDebugEnabled()){
				logger.debug("parse json:" + writer.toString());
			}
			return objectMapper.readValue(writer.toString(), T);
		} catch (Exception e) {
			logger.error("read json fail:", e);
		}
		return null;
	}
}
