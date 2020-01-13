package com.dingtu.Funtion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseXmlService {

	public HashMap<String, String> parseXml(InputStream inStream) throws Exception {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		// ʵ����һ���ĵ�����������
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// ͨ���ĵ�������������ȡһ���ĵ�������
		DocumentBuilder builder = factory.newDocumentBuilder();
		// ͨ���ĵ�ͨ���ĵ�����������һ���ĵ�ʵ��
		Document document = builder.parse(inStream);
		// ��ȡXML�ļ����ڵ�
		Element root = document.getDocumentElement();
		// ��������ӽڵ�
		NodeList childNodes = root.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			// �����ӽڵ�
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) childNode;
				// �汾��
				if ("version".equals(childElement.getNodeName())) {
					hashMap.put("version", childElement.getFirstChild().getNodeValue());
				}
				// �������
				else if (("name".equals(childElement.getNodeName()))) {
					hashMap.put("name", childElement.getFirstChild().getNodeValue());
				}
				// ���ص�ַ
				else if (("url".equals(childElement.getNodeName()))) {
					hashMap.put("url", childElement.getFirstChild().getNodeValue());}
				else if (("codeVersion".equals(childElement.getNodeName()))) {
						hashMap.put("codeVersion", childElement.getFirstChild().getNodeValue());}
			else if (("detail".equals(childElement.getNodeName()))) {
							hashMap.put("detail", childElement.getFirstChild().getNodeValue());
				}
			}
		}
		return hashMap;
	}

	public HashMap<String, String> getXML(String urlStr, String encoding) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// ����һ��URL����
			URL url = new URL(urlStr);
			// ����һ��Http����
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			// ʹ��IO����ȡ����
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), encoding));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {

		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new HashMap<String, String>();
	}

}
