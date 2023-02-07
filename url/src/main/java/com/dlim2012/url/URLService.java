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

    public ShortURLItem shortURL(LongURLItem shortURLRequest) {

        Optional<URL> urlOptional = urlRepository.findByLongURL(shortURLRequest.longURL());
        String shortURL;



        if (urlOptional.isEmpty()) {
            shortURL = urlGenerator.generateShortURL();
//            shortURL = "aaaaaaa";
            URL url = URL.builder()
                    .longURL(shortURLRequest.longURL())
                    .shortURLPath(shortURL)
                    .build();
            urlRepository.save(url);
        } else {
            shortURL = urlOptional.get().getShortURLPath();
        }

        return new ShortURLItem(shortURL);
    }

    public LongURLItem longURL(ShortURLItem longURLRequest){
        URL urlOptional = urlRepository.findByShortURL(longURLRequest.shortURL()).orElseThrow(
                () -> new IllegalStateException(
                        "short url \"" + longURLRequest.shortURL() + "\" does not exist."
                )
        );
        return new LongURLItem(urlOptional.getLongURL());
    }

}
