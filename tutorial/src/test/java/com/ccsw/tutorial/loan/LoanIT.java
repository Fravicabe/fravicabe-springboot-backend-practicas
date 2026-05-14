package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.pageable.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LoanIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String HOST = "http://localhost:";
    private static final String ENDPOINT = "/loan";

    private ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {};

    @Test
    public void findPageWithoutFiltersShouldReturnAllLoansFromDataSql() {
        LoanSearchDto searchDto = new LoanSearchDto();
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize(5);
        searchDto.setPageable(pageableRequest);

        HttpEntity<LoanSearchDto> requestEntity = new HttpEntity<>(searchDto);

        ResponseEntity<ResponsePage<LoanDto>> response = this.restTemplate.exchange(
                HOST + port + ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                responseTypePage
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getTotalElements());
    }

    @Test
    public void findPageWithGameFilterShouldReturnFilteredLoans() {
        LoanSearchDto searchDto = new LoanSearchDto();
        searchDto.setGameTitle("On Mars");

        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize(5);
        searchDto.setPageable(pageableRequest);

        HttpEntity<LoanSearchDto> requestEntity = new HttpEntity<>(searchDto);

        ResponseEntity<ResponsePage<LoanDto>> response = this.restTemplate.exchange(
                HOST + port + ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                responseTypePage
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalElements());
    }
}
