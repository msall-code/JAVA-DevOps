-- Création de la base pour Keycloak
CREATE DATABASE keycloak_db;

-- Création de la base pour les Microservices (Partagée ou spécifique)
CREATE DATABASE microservices_db;

-- Optionnel : donner les droits à l'utilisateur
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE microservices_db TO postgres;