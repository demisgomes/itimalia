CREATE TABLE IF NOT EXISTS animal_to_adopter (id SERIAL PRIMARY KEY, animal_id INT NOT NULL, user_id INT NOT NULL, CONSTRAINT fk_animal_to_deficiency_animal_id_id FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE CASCADE ON UPDATE RESTRICT, CONSTRAINT fk_animal_to_adopter_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE RESTRICT)