package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {

        // findAll de todos los préstamos
        List<Loan> allLoans = (List<Loan>) this.loanRepository.findAll();

        // Se pasan una serie de filtros sobre los dto que vienen completos
        List<Loan> filteredLoans = allLoans.stream()
                .filter(loan -> {
                    if (dto.getGameTitle() != null && !dto.getGameTitle().trim().isEmpty()) {
                        if (loan.getGame() == null || loan.getGame().getTitle() == null ||
                                !loan.getGame().getTitle().toLowerCase().contains(dto.getGameTitle().toLowerCase())) {
                            return false;
                        }
                    }
                    if (dto.getClientName() != null && !dto.getClientName().trim().isEmpty()) {
                        if (loan.getClient() == null || loan.getClient().getName() == null ||
                                !loan.getClient().getName().toLowerCase().contains(dto.getClientName().toLowerCase())) {
                            return false;
                        }
                    }
                    if (dto.getDate() != null) {
                        LocalDate searchDate = dto.getDate();
                        if (loan.getLoanDate() == null || loan.getReturnDate() == null ||
                                searchDate.isBefore(loan.getLoanDate()) || searchDate.isAfter(loan.getReturnDate())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // Donde empieza y acaban las páginas
        int start = (int) dto.getPageable().getPageable().getOffset();
        int end = Math.min((start + dto.getPageable().getPageable().getPageSize()), filteredLoans.size());

        // Control de seguridad si el índice de página solicitado se desborda
        if (start > filteredLoans.size()) {
            start = 0;
            end = 0;
        }

        List<Loan> pageContent = filteredLoans.subList(start, end);

        // Return que devuelve todo lo que ha pasado los filtros y coincide
        return new PageImpl<>(pageContent, dto.getPageable().getPageable(), filteredLoans.size());
    }

    @Override
    public void save(Long id, LoanDto dto) {
        // Juego no es null
        if (dto.getGame() == null || dto.getGame().getId() == null) {
            throw new IllegalArgumentException("El juego es obligatorio");
        }
        // Cliente no es null
        if (dto.getClient() == null || dto.getClient().getId() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        // Fechas no son null
        if (dto.getLoanDate() == null || dto.getReturnDate() == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        // Uno de los filtros importantes, comprueba la fecha para que no sea anterior
        if (dto.getReturnDate().isBefore(dto.getLoanDate())) {
            throw new RuntimeException("La fecha de devolución no puede ser anterior a la fecha de préstamo.");
        }
        // Si pasa de 14 días, boom
        long loanDuration = java.time.temporal.ChronoUnit.DAYS.between(dto.getLoanDate(), dto.getReturnDate());
        if (loanDuration > 14) {
            throw new RuntimeException("El periodo de préstamo no puede superar el límite máximo de 14 días.");
        }

        List<Loan> overlappingLoans = this.loanRepository.findOverlappingLoans(
                id,
                dto.getGame().getId(),
                dto.getLoanDate(),
                dto.getReturnDate()
        );

        if (!overlappingLoans.isEmpty()) {
            throw new RuntimeException("El juego seleccionado ya está prestado a otro cliente en el periodo solicitado.");
        }

        List<Loan> clientLoans = this.loanRepository.findByClientId(dto.getClient().getId());
        LocalDate currentDay = dto.getLoanDate();
        LocalDate lastDay = dto.getReturnDate();

        while (!currentDay.isAfter(lastDay)) {
            int activeLoansCount = 0;

            for (Loan existing : clientLoans) {
                if (id != null && existing.getId().equals(id)) {
                    continue;
                }

                boolean isDayActiveInLoan = (currentDay.isAfter(existing.getLoanDate()) || currentDay.isEqual(existing.getLoanDate()))
                        && (currentDay.isBefore(existing.getReturnDate()) || currentDay.isEqual(existing.getReturnDate()));

                if (isDayActiveInLoan) {
                    activeLoansCount++;
                }
                // Otro filtro importante, antes gracias a un boolean se comprueba cuántos
                // préstamos tiene el cliente y se van sumando al contador
                if (activeLoansCount >= 2) {
                    throw new RuntimeException("El cliente seleccionado ya tiene el cupo máximo de 2 préstamos activos para el día " +
                            currentDay.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
                }
            }
            currentDay = currentDay.plusDays(1);
        }

        Loan loan;
        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        }

        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());

        loan.setGame(this.gameRepository.findById(dto.getGame().getId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found")));

        loan.setClient(this.clientRepository.findById(dto.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found")));

        this.loanRepository.save(loan);
    }


    @Override
    public void delete(Long id) throws Exception {
        this.loanRepository.deleteById(id);
    }
}
