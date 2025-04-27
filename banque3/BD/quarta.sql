-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 27 avr. 2025 à 13:43
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `quarta`
--
create database quarta;
use quarta;
-- --------------------------------------------------------

--
-- Structure de la table `comptes`
--

CREATE TABLE `comptes` (
  `id` int(11) NOT NULL,
  `numero_compte` varchar(20) NOT NULL,
  `type_compte` enum('courant','epargne') NOT NULL,
  `solde` decimal(15,2) DEFAULT 0.00,
  `id_client` int(11) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `comptes`
--

INSERT INTO `comptes` (`id`, `numero_compte`, `type_compte`, `solde`, `id_client`, `date_creation`) VALUES
(1, 'D123456', 'courant', 5000.00, 6, '2025-04-27 11:37:43');

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(11) NOT NULL,
  `type` enum('depot','retrait','transfert') NOT NULL,
  `montant` decimal(15,2) NOT NULL,
  `id_compte_source` int(11) NOT NULL,
  `id_compte_destination` int(11) DEFAULT NULL,
  `date_transaction` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

CREATE TABLE `utilisateurs` (
  `id` int(11) NOT NULL,
  `type` enum('client','employe','directeur') NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `cin` varchar(20) DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `type`, `nom`, `prenom`, `cin`, `login`, `password`, `date_creation`) VALUES
(1, 'client', 'najdi', 'mohammed nourredine', 'M232680', 'admin', '123', '2025-04-27 11:27:38'),
(2, 'employe', 'tazi', 'jamal', 'H124567', 'lock', '456', '2025-04-27 11:27:38'),
(3, 'client', 'roumani', 'ahmed', 'H235567', 'med', '789', '2025-04-27 11:27:38'),
(4, 'client', 'sayeh', 'ali', 'M421160', 'nat', '345', '2025-04-27 11:27:38'),
(5, 'directeur', 'jamali', 'mostafa', 'K123961', 'jam', '357', '2025-04-27 11:27:38'),
(6, 'client', 'herrari', 'bouchaib', 'G125789', 'her', '246', '2025-04-27 11:36:54');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `comptes`
--
ALTER TABLE `comptes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero_compte` (`numero_compte`),
  ADD UNIQUE KEY `unique_client_compte_type` (`id_client`,`type_compte`);

--
-- Index pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_compte_source` (`id_compte_source`),
  ADD KEY `id_compte_destination` (`id_compte_destination`);

--
-- Index pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`),
  ADD UNIQUE KEY `cin` (`cin`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `comptes`
--
ALTER TABLE `comptes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `utilisateurs`
--
ALTER TABLE `utilisateurs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `comptes`
--
ALTER TABLE `comptes`
  ADD CONSTRAINT `comptes_ibfk_1` FOREIGN KEY (`id_client`) REFERENCES `utilisateurs` (`id`);

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`id_compte_source`) REFERENCES `comptes` (`id`),
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`id_compte_destination`) REFERENCES `comptes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
