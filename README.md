## Don't use Hibernate in complex domain driven design  
Hibernate is an antipattern! Yes it is true.
Many experts in object oriented design and domain driven design now declare that there is a historical mistake of 
creating hibernate or any ORM! There is a concept called: "object relation mismatch" and so we can not map
complex interconnected objects(a graph of objects) into tables.
almost 50 percent of martin fowler's book on "Patterns of Enterprise Application Architecture" is 
talking about great patterns like data mappers which i think is much better than ORMs such as hibernate
that can not even encapsulate!
## use Hibernate rather than spring data
If your company says that you dont have time to design an advanced data mapper, go for 
Hibernate but don't use Spring data since it is another commercial advertisement!!!
So just extend your own abstract repository:
```java
public class Repository extends AbstractRepository{

    @PersistenceContext
    EntityManager em;


    @Autowired
    public Repository(EntityManager entityManager){
        this.em=entityManager;
    }

    @Transactional
    @Override
    public void save(Object entity) {
        em.persist(entity);
    }


    @Transactional
    public List<ETF> readETFByTypeUsingNativeQuery(String type) {
        Query q = em.createNativeQuery("SELECT * FROM etf WHERE type = :type ", ETF.class);
        q.setParameter("type", type);
        List<ETF> result = q.getResultList();
        return result;
    }

}
```
And use Generics to generalize as much as you can!!!
```java
public abstract class AbstractRepository<T> {
    public abstract void save(T entity);
}
```
## Scale out using Galera Cluster and MaxScale
You can scale out your write side of CQRS by using Galera Cluster and use multi master mode.
MaxScale is just a proxy to load balance your mariadb servers inside Galera Cluster.
So in your datasource put the ip and port and database name of your mariadb inside maxscale:
```java_holder_method_tree
 @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("db.datasource"));
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }
``` 

## result
```java 
Hibernate: select next_val as id_val from hibernate_sequence for update
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into etf (name, type, value, etfid) values (?, ?, ?, ?)
Hibernate: SELECT * FROM etf WHERE type = ? 
e=43.453
e=943.4
e=47.94
e=7.794
e=87.794
e=3.14
e=113.14
e=11.124
e=11.124
```