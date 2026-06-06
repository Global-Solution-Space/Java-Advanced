-- Data SQL para popular as tabelas com dados iniciais

INSERT INTO tipo_api (tipo_api)
SELECT 'SATVEG'
WHERE NOT EXISTS (SELECT 1 FROM tipo_api WHERE tipo_api = 'SATVEG');

INSERT INTO tipo_api (tipo_api)
SELECT 'NASAPOWER'
WHERE NOT EXISTS (SELECT 1 FROM tipo_api WHERE tipo_api = 'NASAPOWER');

INSERT INTO tipo_plantacao (tipo_plant)
SELECT 'Soja'
WHERE NOT EXISTS (SELECT 1 FROM tipo_plantacao WHERE tipo_plant = 'Soja');

INSERT INTO tipo_plantacao (tipo_plant)
SELECT 'Milho'
WHERE NOT EXISTS (SELECT 1 FROM tipo_plantacao WHERE tipo_plant = 'Milho');

INSERT INTO produtor (nome, email, senha) VALUES ('Enzo', 'enzo@terranova.com', '123456');

INSERT INTO telefone (ddd, numero, produtor_id_produtor) VALUES ('11', '999999999', (SELECT id_produtor FROM produtor WHERE email = 'enzo@terranova.com'));
INSERT INTO localizacao (id_localizacao, coordenadas) VALUES
(1, 'SRID=4326;POINT(-46.737400 -23.384900)'),
(2, 'SRID=4326;POINT(-46.737803 -23.384468)'),
(3, 'SRID=4326;POINT(-46.738000 -23.385000)'),
(4, 'SRID=4326;POINT(-36.287900 -7.488700)'),
(5, 'SRID=4326;POINT(-36.287200 -7.489100)'),
(6, 'SRID=4326;POINT(-36.285900 -7.490600)');

-- Ajustar a sequence para não conflitar com os IDs manuais (caso o H2 reclame em inserções futuras)
ALTER TABLE localizacao ALTER COLUMN id_localizacao RESTART WITH 7;

INSERT INTO propriedade (nome, tamanho_total, produtor_id_produtor, localizacao_id_localizacao) VALUES
('Fazenda Terra Nova', 500.0, (SELECT id_produtor FROM produtor WHERE email = 'enzo@terranova.com'), 1),
('Fazenda Semiarido Demo', 100.0, (SELECT id_produtor FROM produtor WHERE email = 'enzo@terranova.com'), 4);

INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES
('Talhao Alpha', 100.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Soja'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Terra Nova'), 2),
('Talhao Beta', 200.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Milho'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Terra Nova'), 3),
('Talhao Seca', 50.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Milho'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Semiarido Demo'), 5),
('Talhao Cobertura Baixa', 50.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Soja'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Semiarido Demo'), 6);
