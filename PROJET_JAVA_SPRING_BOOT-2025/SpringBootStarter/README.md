# Musique - Application de Gestion de Matériel Musical

Application Java Spring Boot pour la location et la vente de matériel musical. Cette application permet aux utilisateurs de parcourir le catalogue d'équipements, de passer des commandes et utilise un design responsive moderne.

## Fonctionnalités

- Catalogue d'équipements musical avec catégories
- Système de réservation et d'achat
- Panneau d'administration
- Gestion des utilisateurs et authentification
- Système de devises multiples (EUR/MGA)
- Factures imprimables
- Design responsive

## Technologies utilisées

- Spring Boot 3.1.5
- Spring Security
- Thymeleaf pour les templates
- Bootstrap 5 pour l'interface
- JPA/Hibernate pour la persistance
- H2 ou MySQL (via XAMPP) pour la base de données
- Feather Icons pour les icônes

## Configuration requise

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Base de données (H2 incluse par défaut ou MySQL via XAMPP)

## Installation et démarrage

### Avec H2 (par défaut)

```bash
# Cloner le dépôt
git clone <url-du-depot>
cd musique-app

# Compiler et lancer l'application
mvn spring-boot:run
```

### Avec MySQL (XAMPP)

Voir le guide détaillé dans [docs/XAMPP_MYSQL_SETUP.md](docs/XAMPP_MYSQL_SETUP.md)

## Accès à l'application

Une fois démarrée, l'application est accessible à l'adresse:

- http://localhost:5000/

### Comptes prédéfinis

- **Admin**: admin@musique.com / admin123
- **Utilisateur**: user@musique.com / user123

## Fonctionnalités principales

### Catalogue d'équipements

- Liste des équipements disponibles
- Filtrage par catégorie
- Tri par prix, nouveautés
- Vue détaillée de chaque équipement

### Panier et commandes

- Ajouter des articles au panier
- Passer des commandes d'achat
- Réserver du matériel pour une location
- Historique des commandes

### Administration

- Gestion des équipements
- Gestion des utilisateurs
- Suivi des commandes et locations
- Statistiques de vente

## Impression des factures

Chaque commande peut être imprimée en cliquant sur le bouton d'impression dans la page de détail de la commande.

## Support de devises multiples

L'application supporte l'affichage des prix en Euro (EUR) et en Ariary (MGA). Un sélecteur de devise est disponible en bas à droite de l'interface.