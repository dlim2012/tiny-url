package com.dlim2012.url;


import com.dlim2012.clients.token.TokenClient;
import com.dlim2012.url.entity.URL;
import com.dlim2012.url.dto.ShortURLItem;
import com.dlim2012.url.repository.URLRepository;
import com.dlim2012.url.dto.LongURLItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class URLService {

    private final URLRepository urlRepository;
    private final URLGenerator urlGenerator;

    @Autowired
    public URLService(URLRepository urlRepository, TokenClient tokenClient) {
        this.urlRepository = urlRepository;
        this.urlGenerator = new URLGenerator(tokenClient);
    }

    public List<URL> getURLs() {
        return urlRepository.findAll();
    }

    public String shortURLPath(String longURL) {

        Optional<URL> urlOptional = urlRepository.findByLongURL(longURL);
        String path;



        if (urlOptional.isEmpty()) {
            path = urlGenerator.generateShortURL();
//            shortURLPath = "aaaaaaa";
            URL url = URL.builder()
                    .longURL(longURL)
                    .shortURLPath(path)
                    .build();
            urlRepository.save(url);
        } else {
            path = urlOptional.get().getShortURLPath();
        }

        // todo: add default url

        return path;
    }

    public String longURL(String shortPath){
        URL urlOptional = urlRepository.findByShortURL(shortPath).orElseThrow(
                () -> new IllegalStateException(
                        "short path \"" + shortPath + "\" does not exist."
                )
        );
        return urlOptional.getLongURL();
    }

}
