package com.sen5.launchertools.xmlparser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author aiheng@jd.com
 * @date 2014年10月31日 上午11:44:29
 * @desc Xml解析工具类
 */
public abstract class XmlUtils {

    /**
     * 将xml转成对应类型的对象
     *
     * @param xml   xml内容
     * @param clazz 转成对象的calss
     * @return
     */
    public static <T> T xmlToBean(String xml, Class<T> clazz) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
            return parseDocument(clazz, doc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T parseDocument(Class<T> clazz, Document doc)
            throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        T obj = clazz.newInstance();
        String rootName = getRootName(clazz);
        // 获取所有属性
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> mappers = buildDataMapper(fields);
        Element rootElement = doc.getRootElement();
        if (!rootName.equals(rootElement.getName())) {
            throw new DomParseException("the rootName is [" + rootName + "], but the element root is [" + rootElement.getName() + "]");
        }
        for (Iterator<Element> iters = rootElement.elementIterator(); iters.hasNext(); ) {
            Element _element = iters.next();
            String _elementName = _element.getName();
            Field field = mappers.get(_elementName);
            if (field != null) {
                doFillObject(clazz, obj, _element, field);
            }
        }
        return obj;
    }

    /**
     * 填充值，解析属性
     *
     * @param clazz
     * @param obj
     * @param _element
     * @param field
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void doFillObject(Class<?> clazz, Object obj, Element element, Field field)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String _setMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method _setMethod = clazz.getMethod(_setMethodName, field.getType());
        Object _value = element.getData();
        // 属性中包含转换器
        if (field.isAnnotationPresent(DomFieldConvert.class)) {
            Class<? extends DomConvert> _convert = field.getAnnotation(DomFieldConvert.class).value();
            if (_convert != null) {
                DomConvert domConvert = _convert.newInstance();
                Object newValue = domConvert.convert(_value);
                _setMethod.invoke(obj, newValue);
                return;
            }
        }
        // 属性中包含@DomFieldRoot注解
        if (field.isAnnotationPresent(DomFieldRoot.class)) {
            DomFieldRoot _domFieldRoot = field.getAnnotation(DomFieldRoot.class);
            Class<?> subClass = _domFieldRoot.value();
            Field[] fields = subClass.getDeclaredFields();
            Map<String, Field> mappers = buildDataMapper(fields);
            String docText = element.asXML();
            Document _doc = null;
            try {
                Object newValue = null;
                _doc = DocumentHelper.parseText(docText);
                String fieldTypeName = field.getType().getName();
                Element rootElement = _doc.getRootElement();
                if ("java.util.List".equals(fieldTypeName)) {
                    List lists = new ArrayList();
                    for (Iterator<Element> iters = rootElement.elementIterator(); iters.hasNext(); ) {
                        Object subValue = subClass.newInstance();
                        Element subElement = iters.next();
                        for (Iterator<Element> _iters = subElement.elementIterator(); _iters.hasNext(); ) {
                            Element _element = _iters.next();
                            Field subField = mappers.get(_element.getName());
                            if (subField != null) {
                                doFillObject(subClass, subValue, _element, subField);
                            }
                        }
                        lists.add(subValue);
                    }
                    newValue = lists;
                } else {
                    newValue = parseDocument(subClass, _doc);
                }
                _setMethod.invoke(obj, newValue);
                return;
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        if (_value != null && !"".equals(_value)) {
            String localValue = (String) _value;
            String fieldTypeName = field.getType().getName();
            if ("java.lang.Long".equals(fieldTypeName)) {
                _setMethod.invoke(obj, Long.valueOf(localValue));
                return;
            }
            if ("java.lang.Integer".equals(fieldTypeName)) {
                _setMethod.invoke(obj, Integer.valueOf(localValue));
                return;
            }
            if ("java.lang.Double".equals(fieldTypeName)) {
                _setMethod.invoke(obj, Double.valueOf(localValue));
                return;
            }
            if ("java.lang.Float".equals(fieldTypeName)) {
                _setMethod.invoke(obj, Float.valueOf(localValue));
                return;
            }
            _setMethod.invoke(obj, _value);
        }
    }

    private static <T> Map<String, Field> buildDataMapper(Field[] fields)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, Field> mappers = new HashMap<String, Field>();
        for (Field field : fields) {
            // 如果实体属性中包含@DomFieldIngore注解,即不解析
            if (field.isAnnotationPresent(DomFieldIngore.class)) {
                continue;
            }
            String fieldName = null;

            //包含DomField注解
            if (field.isAnnotationPresent(DomField.class)) {
                DomField domField = field.getAnnotation(DomField.class);
                if (!"".equals(domField.value())) {
                    fieldName = domField.value();
                }
            }

            if (fieldName == null) {
                fieldName = field.getName();
            }

            mappers.put(fieldName, field);

        }
        return mappers;
    }

    /**
     * 获取根名称
     *
     * @param clazz
     * @return
     */
    private static <T> String getRootName(Class<T> clazz) {
        String rootName = null;
        if (clazz.isAnnotationPresent(DomRoot.class)) {
            DomRoot domRoot = clazz.getAnnotation(DomRoot.class);
            if (!"".equals(domRoot.value())) {
                rootName = domRoot.value();
            }
        } else {
            rootName = clazz.getSimpleName();
        }
        return rootName;
    }

    /**
     * 将对象转成xml
     *
     * @param t
     * @param encoding 编码
     * @return
     */
    public static <T> String beanToXml(T t, String encoding) {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
            Class<?> clazz = t.getClass();
            String rootName = getRootName(clazz);
            sb.append("<");
            sb.append(rootName);
            sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
            sb.append(">");
            // 获取所有属性
            Field[] fields = clazz.getDeclaredFields();
            buildXmlUseFields(t, sb, clazz, fields);
            sb.append("</");
            sb.append(rootName);
            sb.append(">");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return sb.toString();
    }

    private static <T> void buildXmlUseFields(T t, StringBuffer sb, Class<?> clazz, Field[] fields)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, Field> mappers = buildDataMapper(fields);
        Iterator<Map.Entry<String, Field>> entrys = mappers.entrySet().iterator();
        while (entrys.hasNext()) {
            Map.Entry<String, Field> entry = entrys.next();
            String key = entry.getKey();
            Field field = entry.getValue();
            String _getMethodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method _getMethod = clazz.getMethod(_getMethodName);
            Object value = _getMethod.invoke(t);
            sb.append("<");
            sb.append(key);
            sb.append(">");
            if (field.getType().getName().startsWith("java.") && !field.getType().getName().equals("java.util.List")) {
                if (field.getType().getName().equals("java.util.Date") && value != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sb.append(sdf.format(value));
                } else if (value != null) {
                    sb.append(value);
                }
            } else if (field.getType().getName().equals("java.util.List")) {
                List list = (List) value;
                for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                    Object obj = iter.next();
                    Class<?> _clazz = obj.getClass();
                    String _rootName = getRootName(_clazz);
                    sb.append("<");
                    sb.append(_rootName);
                    sb.append(">");
                    buildXmlUseFields(obj, sb, _clazz, _clazz.getDeclaredFields());
                    sb.append("</");
                    sb.append(_rootName);
                    sb.append(">");
                }
            } else {
                Class<?> _clazz = field.getType();
                buildXmlUseFields(value, sb, _clazz, _clazz.getDeclaredFields());
            }
            sb.append("</");
            sb.append(key);
            sb.append(">");
        }
    }

    public static <T> String beanToXml(T t) {
        return beanToXml(t, "UTF-8");
    }

}
