INSERT INTO produtor (nome, email, senha) VALUES ('Enzo', 'enzo@terranova.com', '123456');

-- Localização 1 (Propriedade)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.384468, -46.737803);

-- Localização 2 (Talhão Alpha)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.384468, -46.737803);

-- Localização 3 (Talhão Beta)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.385000, -46.738000);

-- Criar a propriedade linkando o Produtor 1 e a Localização 1
INSERT INTO propriedade (nome, tamanho_total, produtor_id_produtor, localizacao_id_localizacao) VALUES ('Fazenda Terra Nova', 500.0, 1, 1);

-- Tipos de Plantação
INSERT INTO tipo_plantacao (tipo_plant) VALUES ('Soja');
INSERT INTO tipo_plantacao (tipo_plant) VALUES ('Milho');

-- Criar dois talhões (Alpha e Beta) linkando as localizações 2 e 3
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Alpha', 100.0, 1, 1, 2);
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Beta', 200.0, 2, 1, 3);
