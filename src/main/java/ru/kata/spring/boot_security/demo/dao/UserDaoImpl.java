package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(User user) {
        entityManager.persist(user);
    }

    @Override
    public List<User> listUsers() {
        Query query = entityManager.createQuery("FROM User");
        return query.getResultList();
    }

    @Override
    public User getUserById(int id) {
        String hql = "SELECT u FROM User u WHERE u.id = :id";
        Query q = entityManager.createQuery(hql);
        q.setParameter("id", id);
        return (User) q.getSingleResult();
    }

    @Override
    public void deleteUserById(int id) {
        String hql = "DELETE FROM User WHERE id = :userId";
        Query q = entityManager.createQuery(hql);
        q.setParameter("userId", id);
        q.executeUpdate();
    }

    @Override
    public void updateUser(int id, User user) {
        String hql = "UPDATE User SET name = :name WHERE id = :userId";
        Query q = entityManager.createQuery(hql);
        q.setParameter("name", user.getName());
        q.setParameter("userId", id);
        q.executeUpdate();
    }
}
