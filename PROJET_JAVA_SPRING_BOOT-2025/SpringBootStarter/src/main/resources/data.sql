-- Insertion d'un admin et d'un client test
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@music.com', '$2a$10$dPMuRDtRqv8Q0aW50MWKW.0DKlSvbXmCgP9s5Z3g2Ut9Qf.7tq2ya', 'admin'), -- password: admin123
('User Test', 'user@music.com', '$2a$10$FTzn37qPQ1Zgo25SCHpA3OwPR6xIJ0.WFQnoRaif7EyKkxPkKuIF2', 'client'); -- password: user123

-- Insertion de matériels de musique (vente uniquement)
INSERT INTO equipment (name, description, price_sale, quantity_available, image_url) VALUES
('Fender Stratocaster', 'Guitare électrique classique avec corps en aulne, manche en érable et trois micros single-coil. Parfaite pour le blues, le rock et la pop.', 899999.00, 10, 'https://upload.wikimedia.org/wikipedia/commons/6/64/Fender_Stratocaster.jpg'),
('Gibson Les Paul', 'Guitare électrique haut de gamme avec corps en acajou, table en érable et deux micros humbucker. Son riche et puissant idéal pour le rock et le metal.', 2499999.00, 5, 'https://upload.wikimedia.org/wikipedia/commons/4/45/Gibson_Les_Paul_Standard.jpg'),
('Yamaha Drum Kit', 'Batterie acoustique professionnelle comprenant grosse caisse, caisse claire, toms, cymbales hi-hat et ride. Construction en bois de bouleau pour une sonorité exceptionnelle.', 1200000.00, 7, 'https://upload.wikimedia.org/wikipedia/commons/1/1b/Yamaha_drum_kit.jpg'),
('Marshall Amplifier', 'Amplificateur guitare 100W avec préampli à lampes et section de puissance à transistors. Offre des sons clairs cristallins et des distorsions chaudes.', 1500000.00, 4, 'https://upload.wikimedia.org/wikipedia/commons/8/87/Marshall_JCM2000.jpg'),
('Nord Stage 3 Keyboard', 'Clavier de scène polyvalent avec synthétiseur, piano et orgue. 88 touches semi-lestées et nombreuses options de personnalisation sonore.', 3999999.00, 3, 'https://upload.wikimedia.org/wikipedia/commons/2/2c/Nord_Stage_3.jpg'),
('Pioneer DJ Controller', 'Contrôleur DJ professionnel avec deux platines, mixeur central, pads performance et effets. Compatible avec Serato DJ et Rekordbox.', 2299999.00, 6, 'https://upload.wikimedia.org/wikipedia/commons/7/7e/Pioneer_DDJ-SX.jpg'),
('JBL EON615 Speaker', 'Enceinte amplifiée portable 15" avec amplification 1000W et DSP intégré. Idéale pour la sonorisation de petits et moyens événements.', 549999.00, 8, 'https://upload.wikimedia.org/wikipedia/commons/0/08/JBL_EON615.jpg'),
('Fender Precision Bass', 'Basse électrique classique avec corps en aulne, manche en érable et micro split-coil. La référence pour un son rond et puissant.', 1099999.00, 9, 'https://upload.wikimedia.org/wikipedia/commons/5/57/Fender_Precision_Bass.jpg'),
('Akai MPC Live', 'Station de production musicale autonome avec écran tactile, pads sensibles à la vélocité, séquenceur et nombreux effets intégrés.', 1199999.00, 5, 'https://upload.wikimedia.org/wikipedia/commons/3/3f/Akai_MPC_Live.jpg'),
('Shure SM58 Microphone', 'Microphone dynamique vocal de référence mondiale. Robuste, fiable et offrant une réponse en fréquence optimisée pour la voix.', 99999.00, 20, 'https://upload.wikimedia.org/wikipedia/commons/f/f9/Shure_SM58.jpg');
