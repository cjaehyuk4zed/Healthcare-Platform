package platform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import platform.dto.postdto.PostInfoResponseDTO;
import platform.repository.PostInfoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TestService {

    public List<String> getImgTagList(PostInfoResponseDTO postInfoResponseDTO){
        log.info("TestController getImgTagList");
        List<String> imgTagList = new ArrayList<>();

        Document document = Jsoup.parse(postInfoResponseDTO.getPostContent());
        Elements imgElements = document.select("img"); // get a collection of all <span> elements parsed from HTML String

        for (Element imgElement : imgElements){
            log.info(imgElement.toString());
            imgTagList.add(imgElement.attr("src"));
        }
        return imgTagList;
    }

    public List<String> getSpanTagList(PostInfoResponseDTO postInfoResponseDTO){
        log.info("TestController getImgTagList");
        List<String> imgTagList = new ArrayList<>();

        Document document = Jsoup.parse(postInfoResponseDTO.getPostContent());
        Elements spanElements = document.select("span"); // get a collection of all <span> elements parsed from HTML String

        for (Element spanElement : spanElements){
            log.info(spanElement.toString());
            imgTagList.add(spanElement.text());
        }
        return imgTagList;
    }

}
