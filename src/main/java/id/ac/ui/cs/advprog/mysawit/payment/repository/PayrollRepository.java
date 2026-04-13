package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.Payroll;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class PayrollRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Payroll payroll) {
        if (payroll.getId() == null) {
            entityManager.persist(payroll);
        } else {
            entityManager.merge(payroll);
        }
    }

    public List<Payroll> findAll() {
        return entityManager.createQuery("SELECT p FROM Payroll p", Payroll.class).getResultList();
    }
}