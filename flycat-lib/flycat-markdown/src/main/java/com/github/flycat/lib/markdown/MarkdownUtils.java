package com.github.flycat.lib.markdown;

import com.github.flycat.util.StringUtils;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.*;

public class MarkdownUtils {

    public static Map<String, List<String>> parseYamlFrontMatter(Parser parser,
                                                                 String content) {
        Node document = parser.parse(content);
        final Map<String, List<String>> data = parseYamlFrontMatter(document);
        return data;
    }

    public static Map<String, String> parseAndJoinYamlFrontMatter(Node document) {
        final Map<String, List<String>> stringListMap = parseYamlFrontMatter(document);
        final Set<Map.Entry<String, List<String>>> entries = stringListMap.entrySet();
        final HashMap<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : entries) {
            newMap.put(entry.getKey(), StringUtils.joinStringList(entry.getValue()));
        }
        return newMap;
    }

    public static Map<String, List<String>> parseYamlFrontMatter(Node document) {
        final AbstractYamlFrontMatterVisitor abstractYamlFrontMatterVisitor = new AbstractYamlFrontMatterVisitor();
        abstractYamlFrontMatterVisitor.visit(document);
        return abstractYamlFrontMatterVisitor.getData();
    }

    public static Parser createDefaultParser() {
        MutableDataHolder options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(YamlFrontMatterExtension.create(),
                StrikethroughExtension.create(), AutolinkExtension.create()));
        Parser parser = Parser.builder(options).build();
        return parser;
    }

    public static String renderToHtml(Document document) {
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
