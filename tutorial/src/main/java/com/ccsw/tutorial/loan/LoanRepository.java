package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Long> {
    // Declaramos lo mismo que en autores
    Page<Loan> findAll(Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.game.id = :gameId " +
            "AND (:id IS NULL OR l.id <> :id) " +
            "AND (l.loanDate <= :returnDate AND l.returnDate >= :loanDate)")
    List<Loan> findOverlappingLoans(
            @Param("id") Long id,
            @Param("gameId") Long gameId,
            @Param("loanDate") LocalDate loanDate,
            @Param("returnDate") LocalDate returnDate
    );

    List<Loan> findByClientId(Long clientId);
}
