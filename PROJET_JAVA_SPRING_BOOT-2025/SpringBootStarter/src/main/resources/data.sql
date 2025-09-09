-- Insert admin and regular user
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@music.com', '$2a$10$dPMuRDtRqv8Q0aW50MWKW.0DKlSvbXmCgP9s5Z3g2Ut9Qf.7tq2ya', 'ADMIN'), -- password: admin123
('User Test', 'user@music.com', '$2a$10$FTzn37qPQ1Zgo25SCHpA3OwPR6xIJ0.WFQnoRaif7EyKkxPkKuIF2', 'USER'); -- password: user123

-- Insert equipment
INSERT INTO equipment (name, description, price_sale, price_rental, quantity_available, image_url) VALUES
('Fender Stratocaster', 'Guitare électrique classique avec corps en aulne, manche en érable et trois micros single-coil. Parfaite pour le blues, le rock et la pop.', 899.99, 49.99, 10, 'https://cdn.feather.icons/stratocaster.svg'),
('Gibson Les Paul', 'Guitare électrique haut de gamme avec corps en acajou, table en érable et deux micros humbucker. Son riche et puissant idéal pour le rock et le metal.', 2499.99, 129.99, 5, 'https://cdn.feather.icons/lespaul.svg'),
('Yamaha Drum Kit', 'Batterie acoustique professionnelle comprenant grosse caisse, caisse claire, toms, cymbales hi-hat et ride. Construction en bois de bouleau pour une sonorité exceptionnelle.', 1200.00, 79.99, 7, 'https://cdn.feather.icons/drums.svg'),
('Marshall Amplifier', 'Amplificateur guitare 100W avec préampli à lampes et section de puissance à transistors. Offre des sons clairs cristallins et des distorsions chaudes.', 1500.00, 89.99, 4, 'https://cdn.feather.icons/amplifier.svg'),
('Nord Stage 3 Keyboard', 'Clavier de scène polyvalent avec synthétiseur, piano et orgue. 88 touches semi-lestées et nombreuses options de personnalisation sonore.', 3999.99, 199.99, 3, 'https://cdn.feather.icons/keyboard.svg'),
('Pioneer DJ Controller', 'Contrôleur DJ professionnel avec deux platines, mixeur central, pads performance et effets. Compatible avec Serato DJ et Rekordbox.', 2299.99, 129.99, 6, 'https://cdn.feather.icons/dj.svg'),
('JBL EON615 Speaker', 'Enceinte amplifiée portable 15" avec amplification 1000W et DSP intégré. Idéale pour la sonorisation de petits et moyens événements.', 549.99, 49.99, 8, 'https://cdn.feather.icons/speaker.svg'),
('Fender Precision Bass', 'Basse électrique classique avec corps en aulne, manche en érable et micro split-coil. La référence pour un son rond et puissant.', 1099.99, 59.99, 9, 'https://cdn.feather.icons/bass.svg'),
('Akai MPC Live', 'Station de production musicale autonome avec écran tactile, pads sensibles à la vélocité, séquenceur et nombreux effets intégrés.', 1199.99, 89.99, 5, 'https://cdn.feather.icons/sampler.svg'),
('Shure SM58 Microphone', 'Microphone dynamique vocal de référence mondiale. Robuste, fiable et offrant une réponse en fréquence optimisée pour la voix.', 99.99, 9.99, 20, 'https://cdn.feather.icons/microphone.svg');
