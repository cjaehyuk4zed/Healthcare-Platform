package platform.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import platform.domain.Posting_Info;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class HtmlTagService {

    public List<String> getSpanTagList(String post_content){
        if(post_content == null || post_content.isEmpty()){
            return new ArrayList<>(); // return empty list if post_content is empty
        }
        log.info("PostingInfoService getSpanTextList");
        List<String> spanTextList = new ArrayList<>();

        Document document = Jsoup.parse(post_content);
        Elements spanElements = document.select("span"); // get a collection of all <span> elements parsed from HTML String

        for (Element spanElement : spanElements){
            log.info(spanElement.toString());
            spanTextList.add(spanElement.text());
        }
        return spanTextList;
    }

    public boolean searchSpanTagList(List<String> spanTextList, String query){
        String queryPattern = ".*" + query + ".*";
        Pattern regexPattern = Pattern.compile(queryPattern, Pattern.CASE_INSENSITIVE);

        for (String str : spanTextList){
            if(regexPattern.matcher(str).matches()){
                return true;
            }
        }
        return false;
    }


    public List<String> getImgTagSrcList(Posting_Info postingInfo){
        log.info("PostingInfoService getImgTagList");
        List<String> imgTagSrcList = new ArrayList<>();

        Document document = Jsoup.parse(postingInfo.getPostingContent());
        Elements imgElements = document.select("img"); // get a collection of all <span> elements parsed from HTML String

        for (Element imgElement : imgElements){
            log.info(imgElement.toString());
            imgTagSrcList.add(imgElement.attr("src"));
        }
        return imgTagSrcList;
    }

    public String deleteScriptTags(String postContent){
        Document doc = Jsoup.parse(postContent);
        doc.select("script").remove();
        return doc.text();
    }

}
