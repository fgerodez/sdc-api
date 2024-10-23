# API Produit

## Frameworks / librairies utilisés 

- Spring Boot
- Spring Web
- Spring Data
- MapStruct

## Gestion du multibackend

L'interface ProductRepository est utilisée pour faire le routage entre les différents backends possibles (Jpa, MongoDb, Fichier plat json) 

```java 
public interface ProductRepository extends Repository<Product, Long> {
    void deleteById(Long id);

    Optional<Product> findById(Long id);

    Product save(Product product);

    Iterable<Product> findAll();
}
```
C'est une interface Spring Data standard qui peut être implémentée via spring-data-jpa pour les bdd classiques, via spring-data-mongodb pour le NoSQL ou via l'implémentation FileProductRepository pour un fichier plat.

La localisation de la base de données fichier est configurable via les properties :

```properties
json.db.path=/tmp/appdb.json
```

## Tests

Les tests unitaires sont disponibles dans le package com.sdc.fgerodez.

Une séquence de tests end-to-end est disponible dans la classe com.sdc.fgerodez.acceptance.ProductApiTests.

J'ai également joint une collection postman à la racine du projet (SDC Backend.postman_collection.json).

