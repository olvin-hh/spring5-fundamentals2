package lab.dao.jpa;

import lab.dao.CountryDao;
import lab.model.Country;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class CountryJpaDaoImpl extends AbstractJpaDao implements CountryDao {

    @Override
    public void save(@NotNull Country country) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(country);
        transaction.commit();
        em.close();

    }

    @Override
    public Stream<Country> getAllCountries() {
        final EntityManager em = emf.createEntityManager();
        Stream<Country> countries = em.createQuery("FROM Country", Country.class)
                .getResultList().stream();
        em.close();
        return countries;
    }

    @Override
    public Optional<Country> getCountryByName(@NotNull String name) {
        final EntityManager em = emf.createEntityManager();
        Optional<Country> result = Optional
                .ofNullable(em.createQuery("SELECT c FROM Country c WHERE c.name LIKE :name", Country.class)
                        .setParameter("name", name).getSingleResult());
        em.close();
        return result;
    }

    @Override
    public void remove(Country exampleCountry) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.detach(exampleCountry);
        transaction.commit();
        em.close();
    }

}
