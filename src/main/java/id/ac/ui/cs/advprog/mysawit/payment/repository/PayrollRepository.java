package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PayrollRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Payroll save(Payroll payroll) {
        if (payroll.getId() == null) {
            entityManager.persist(payroll);
            return payroll;
        } else {
            return entityManager.merge(payroll);
        }
    }

    public List<Payroll> findAll() {
        return entityManager.createQuery("SELECT p FROM Payroll p", 
                Payroll.class).getResultList();
    }

    public Optional<Payroll> findById(UUID id) {
        Payroll payroll = entityManager.find(Payroll.class, id);
        return Optional.ofNullable(payroll);
    }

    public List<Payroll> findByTanggal(LocalDate tanggal) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal", 
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .getResultList();
    }

    public List<Payroll> findByStatus(String status) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status)", 
                Payroll.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Payroll> findByTanggalAndStatus(LocalDate tanggal, String status) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status)", 
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Payroll> findByPayrollType(String payrollType) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.payrollType) = UPPER(:payrollType)", 
                Payroll.class)
                .setParameter("payrollType", payrollType)
                .getResultList();
    }

    public List<Payroll> findByWorkerId(String workerId) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.workerId = :workerId", 
                Payroll.class)
                .setParameter("workerId", workerId)
                .getResultList();
    }


    public List<Payroll> findByTanggalAndWorkerId(LocalDate tanggal, String workerId) {
        String jpql = "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND p.workerId = :workerId";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    public List<Payroll> findByStatusAndWorkerId(String status, String workerId) {
        String jpql = "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status) " +
                "AND p.workerId = :workerId";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    public List<Payroll> findByTanggalAndStatusAndWorkerId(
            LocalDate tanggal, String status, String workerId) {
        String jpql = "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status) AND p.workerId = :workerId";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .getResultList();
    }
    public boolean existsByReferenceId(String referenceId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Payroll p WHERE p.referenceId = :refId", Long.class)
                .setParameter("refId", referenceId)
                .getSingleResult();
        return count > 0;
    }
}