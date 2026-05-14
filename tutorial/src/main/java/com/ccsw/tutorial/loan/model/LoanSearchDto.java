package com.ccsw.tutorial.loan.model;

import com.ccsw.tutorial.common.pageable.PageableRequest;
import java.time.LocalDate;

/**
 * @author ccsw
 *
 */
public class LoanSearchDto {

    private String gameTitle;
    private String clientName;
    private LocalDate date;
    private PageableRequest pageable;

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PageableRequest getPageable() {
        return pageable;
    }

    public void setPageable(PageableRequest pageable) {
        this.pageable = pageable;
    }
}
