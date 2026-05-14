package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.common.pageable.PageableRequest;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    public void findPageWithNoFiltersShouldReturnPage() {
        LoanSearchDto searchDto = new LoanSearchDto();
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setPageNumber(0);
        pageableRequest.setPageSize(5);
        searchDto.setPageable(pageableRequest);

        List<Loan> list = new ArrayList<>();
        list.add(new Loan());

        when(this.loanRepository.findAll()).thenReturn(list);

        Page<Loan> result = this.loanService.findPage(searchDto);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(this.loanRepository, times(1)).findAll();
    }


    @Test
    public void deleteShouldCallRepositoryDelete() throws Exception {
        Long loanId = 1L;
        doNothing().when(this.loanRepository).deleteById(loanId);

        this.loanService.delete(loanId);

        verify(this.loanRepository, times(1)).deleteById(loanId);
    }
}
