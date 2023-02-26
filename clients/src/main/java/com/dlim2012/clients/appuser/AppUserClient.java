package com.dlim2012.clients.appuser;


import com.dlim2012.clients.appuser.dto.ExpireDateRequest;
import com.dlim2012.clients.appuser.dto.ExpireDateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
        name = "appuser",
        url = "${clients.appuser.url}"
)
public interface AppUserClient {

    @PostMapping(path="/api/v1/user/expire-date")
    List<ExpireDateResponse> getExpireDate(List<ExpireDateRequest> requests);

}
