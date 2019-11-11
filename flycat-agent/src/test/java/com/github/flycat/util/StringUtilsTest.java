package com.github.flycat.util;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testSubstringNotInHtmlTag() {
        String html = "<h1>hello</h1>";
        final String s = StringUtils.substringNotInHtmlTag(html);
        assert s.equals(html);
        String html2 = "<h1>hello";
        final String s2 = StringUtils.substringNotInHtmlTag(html2);
        System.out.println(s2);
        assert "".equals(s2);
        String html3 = "test<h1>hello";
        final String s3 = StringUtils.substringNotInHtmlTag(html3);
        assert "test".equals(s3);
        String html4 = "test</h1>hello";
        final String s4 = StringUtils.substringNotInHtmlTag(html4);
        assert "test</h1>hello".equals(s4);
        String html5 = "test<a href";
        final String s5 = StringUtils.substringNotInHtmlTag(html5);
        System.out.println(s5);
        assert "test".equals(s5);

    }
}
