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
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES
(-23.384900, -46.737400),
(-23.384468, -46.737803),
(-23.385000, -46.738000),
(-7.488700, -36.287900),
(-7.489100, -36.287200),
(-7.490600, -36.285900);

INSERT INTO propriedade (nome, tamanho_total, produtor_id_produtor, localizacao_id_localizacao) VALUES
('Fazenda Terra Nova', 500.0, (SELECT id_produtor FROM produtor WHERE email = 'enzo@terranova.com'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -23.384900 AND loc_longitude = -46.737400)),
('Fazenda Semiarido Demo', 100.0, (SELECT id_produtor FROM produtor WHERE email = 'enzo@terranova.com'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -7.488700 AND loc_longitude = -36.287900));

INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES
('Talhao Alpha', 100.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Soja'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Terra Nova'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -23.384468 AND loc_longitude = -46.737803)),
('Talhao Beta', 200.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Milho'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Terra Nova'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -23.385000 AND loc_longitude = -46.738000)),
('Talhao Seca', 50.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Milho'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Semiarido Demo'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -7.489100 AND loc_longitude = -36.287200)),
('Talhao Cobertura Baixa', 50.0, (SELECT id_tipo_plant FROM tipo_plantacao WHERE tipo_plant = 'Soja'), (SELECT id_propriedade FROM propriedade WHERE nome = 'Fazenda Semiarido Demo'), (SELECT id_localizacao FROM localizacao WHERE loc_latitude = -7.490600 AND loc_longitude = -36.285900));
