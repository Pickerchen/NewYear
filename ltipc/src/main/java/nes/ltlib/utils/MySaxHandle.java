package nes.ltlib.utils;

/**
 * Created by ZHOUDAO on 2017/9/1.
 */


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import nes.ltlib.interf.SaxParse;


public class MySaxHandle extends DefaultHandler {


    public MySaxHandle(SaxParse parse) {

        this.parse = parse;
    }



    private SaxParse parse;

    private boolean tag_flag_title = false;//是否读到textOSDTitle标签
    private boolean tag_flag_title_child = false;//是否读到textOSDTitle子标签
    private String tag_name = "TextOSDTitle";
    private String tag_child_name = "string";

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals(tag_name)) {
            tag_flag_title = true;
//                Logger.e("startElement","localName is "+localName+"aName is "+qName);
        }
        if (localName.equals(tag_child_name) && tag_flag_title) {
            tag_flag_title_child = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
//            Logger.e("startElement","localName is "+localName+"aName is "+qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (tag_flag_title_child && tag_flag_title) {

            tag_flag_title = false;
            tag_flag_title_child = false;

            if (parse!=null){
                parse.characters(ch, start, length);
            }


        }
    }
}
