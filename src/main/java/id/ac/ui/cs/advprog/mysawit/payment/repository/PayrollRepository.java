package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<Payroll> findAll(Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p ORDER BY p.id DESC", 
                Payroll.class)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p", Long.class)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
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

    public Page<Payroll> findByTanggal(LocalDate tanggal, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE p.tanggal = :tanggal", Long.class)
                .setParameter("tanggal", tanggal)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public List<Payroll> findByStatus(String status) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status)", 
                Payroll.class)
                .setParameter("status", status)
                .getResultList();
    }

    public Page<Payroll> findByStatus(String status, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status) "
                + "ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("status", status)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE UPPER(p.status) = "
                + "UPPER(:status)", Long.class)
                .setParameter("status", status)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
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

    public Page<Payroll> findByTanggalAndStatus(LocalDate tanggal, String status,
                                                   Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status) ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status)", Long.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public List<Payroll> findByPayrollType(String payrollType) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.payrollType) = UPPER(:payrollType)", 
                Payroll.class)
                .setParameter("payrollType", payrollType)
                .getResultList();
    }

    public Page<Payroll> findByPayrollType(String payrollType, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.payrollType) = UPPER(:payrollType) "
                + "ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("payrollType", payrollType)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE UPPER(p.payrollType) = "
                + "UPPER(:payrollType)", Long.class)
                .setParameter("payrollType", payrollType)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public List<Payroll> findByWorkerId(String workerId) {
        return entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.workerId = :workerId", 
                Payroll.class)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    public Page<Payroll> findByWorkerId(String workerId, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.workerId = :workerId "
                + "ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("workerId", workerId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE p.workerId = :workerId", Long.class)
                .setParameter("workerId", workerId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }


    public List<Payroll> findByTanggalAndWorkerId(LocalDate tanggal, String workerId) {
        String jpql = "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND p.workerId = :workerId";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    public Page<Payroll> findByTanggalAndWorkerId(LocalDate tanggal, String workerId,
                                                    Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND p.workerId = :workerId ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("workerId", workerId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND p.workerId = :workerId", Long.class)
                .setParameter("tanggal", tanggal)
                .setParameter("workerId", workerId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public List<Payroll> findByStatusAndWorkerId(String status, String workerId) {
        String jpql = "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status) " +
                "AND p.workerId = :workerId";
        return entityManager.createQuery(jpql, Payroll.class)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .getResultList();
    }

    public Page<Payroll> findByStatusAndWorkerId(String status, String workerId,
                                                  Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE UPPER(p.status) = UPPER(:status) " +
                "AND p.workerId = :workerId ORDER BY p.id DESC", 
                Payroll.class)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE UPPER(p.status) = UPPER(:status) " +
                "AND p.workerId = :workerId", Long.class)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
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

    public Page<Payroll> findByTanggalAndStatusAndWorkerId(
            LocalDate tanggal, String status, String workerId, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int offset = pageNumber * pageSize;

        List<Payroll> content = entityManager.createQuery(
                "SELECT p FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status) AND p.workerId = :workerId " +
                "ORDER BY p.id DESC",
                Payroll.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();

        Long total = entityManager.createQuery(
                "SELECT COUNT(p) FROM Payroll p WHERE p.tanggal = :tanggal " +
                "AND UPPER(p.status) = UPPER(:status) AND p.workerId = :workerId", Long.class)
                .setParameter("tanggal", tanggal)
                .setParameter("status", status)
                .setParameter("workerId", workerId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
    public boolean existsByReferenceId(String referenceId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Payroll p WHERE p.referenceId = :refId",
                        Long.class)
                .setParameter("refId", referenceId)
                .getSingleResult();
        return count > 0;
    }
}