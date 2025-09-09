# Configuration MySQL avec XAMPP

Ce guide explique comment configurer l'application Musique pour utiliser une base de données MySQL via XAMPP au lieu de la base de données H2 par défaut.

## 1. Prérequis

- XAMPP installé sur votre machine (version 7.4+ recommandée)
- Application Musique déjà configurée et fonctionnelle avec H2

## 2. Installation et démarrage de XAMPP

1. Téléchargez XAMPP depuis le site officiel : [https://www.apachefriends.org/](https://www.apachefriends.org/)
2. Installez XAMPP en suivant les instructions pour votre système d'exploitation
3. Lancez le panneau de contrôle XAMPP et démarrez les modules suivants :
   - Apache
   - MySQL

## 3. Création de la base de données MySQL

1. Ouvrez votre navigateur et accédez à phpMyAdmin : [http://localhost/phpmyadmin/](http://localhost/phpmyadmin/)
2. Cliquez sur "New" (Nouveau) dans le menu de gauche
3. Créez une nouvelle base de données avec les paramètres suivants :
   - Nom de la base de données : `musique_db`
   - Jeu de caractères : `utf8mb4_unicode_ci`
4. Cliquez sur "Create" (Créer)

## 4. Configuration de l'application Musique

L'application est déjà configurée avec les paramètres nécessaires pour MySQL. Pour activer cette configuration, vous devez simplement spécifier le profil MySQL au démarrage.

### Fichier de configuration MySQL
Le fichier de configuration MySQL se trouve à : `src/main/resources/application-mysql.properties`

Ce fichier contient les paramètres suivants :
```properties
# Configuration de la base de données MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/musique_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```

### Démarrer l'application avec le profil MySQL

#### Via la ligne de commande
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

#### Via un IDE (comme IntelliJ IDEA ou Eclipse)
Ajoutez `-Dspring-boot.run.profiles=mysql` aux arguments VM de votre configuration de lancement.

## 5. Vérification de la configuration

Pour vérifier que l'application utilise bien MySQL :

1. Démarrez l'application avec le profil MySQL comme indiqué ci-dessus
2. Accédez à l'application dans votre navigateur : [http://localhost:5000](http://localhost:5000)
3. Vérifiez dans la console que les logs indiquent l'utilisation de MySQL
4. Dans phpMyAdmin, vous devriez voir les tables créées automatiquement par Hibernate

## 6. Dépannage

### Problème de connexion
- Vérifiez que le service MySQL est bien démarré dans XAMPP
- Vérifiez les identifiants dans le fichier `application-mysql.properties`
- Le port par défaut de MySQL est 3306, assurez-vous qu'il n'est pas déjà utilisé

### Tables non créées
- Vérifiez que `spring.jpa.hibernate.ddl-auto=update` est bien configuré
- Regardez les logs de l'application pour identifier les erreurs SQL

### Problème d'encodage des caractères
- Assurez-vous que la base de données utilise bien `utf8mb4_unicode_ci`
- Vérifiez que la connexion JDBC inclut bien les paramètres d'encodage appropriés

## 7. Migration des données existantes

Si vous avez déjà des données dans la base H2 que vous souhaitez migrer vers MySQL :

1. Exportez les données de H2 via la console H2 (format SQL)
2. Adaptez le script SQL si nécessaire pour MySQL
3. Importez le script dans phpMyAdmin

L'application est déjà configurée pour initialiser des données de test, donc si vous n'avez pas de données critiques, vous pouvez simplement laisser l'application créer de nouvelles données dans MySQL.