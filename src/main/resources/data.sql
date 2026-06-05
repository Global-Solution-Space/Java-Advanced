-- Criar Produtor 1
INSERT INTO produtor (nome, email, senha) VALUES ('Enzo', 'enzo@terranova.com', '123456');

-- Telefone do Produtor 1
INSERT INTO telefone (ddd, numero, produtor_id_produtor) VALUES ('11', '999999999', 1);

-- Localizações da Propriedade 1 e seus talhões
-- Localização 1 (Propriedade Fazenda Terra Nova)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.384468, -46.737803);
-- Localização 2 (Talhão Alpha)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.384468, -46.737803);
-- Localização 3 (Talhão Beta)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.385000, -46.738000);

-- Propriedade 1
INSERT INTO propriedade (nome, tamanho_total, produtor_id_produtor, localizacao_id_localizacao) VALUES ('Fazenda Terra Nova', 500.0, 1, 1);

-- Tipos de Plantação (Caso não criados ainda pelo DatabaseInitializer, garantimos os ids 1 e 2)
INSERT INTO tipo_plantacao (tipo_plant) VALUES ('Soja');
INSERT INTO tipo_plantacao (tipo_plant) VALUES ('Milho');

-- Talhões da Propriedade 1 (Alpha e Beta)
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Alpha', 100.0, 1, 1, 2);
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Beta', 200.0, 2, 1, 3);


-- CRIAÇÃO DA PROPRIEDADE 2 (PARA FORÇAR ALERTAS DA API REAL)
-- Localização 4 (Propriedade Extrema)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-7.489100, -36.287200); -- Cabaceiras, PB (Cidade mais seca do Brasil)
-- Localização 5 (Talhão Seca Absoluta - Para forçar Alerta da NASA)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-7.489100, -36.287200);
-- Localização 6 (Talhão Asfalto - Para forçar Alerta do SATVEG)
INSERT INTO localizacao (loc_latitude, loc_longitude) VALUES (-23.561684, -46.655981); -- Avenida Paulista, SP (Asfalto puro, NDVI zero)

-- Propriedade 2
INSERT INTO propriedade (nome, tamanho_total, produtor_id_produtor, localizacao_id_localizacao) VALUES ('Fazenda de Testes Extremos', 100.0, 1, 4);

-- Talhões da Propriedade 2
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Seca (Nordeste)', 50.0, 2, 2, 5);
INSERT INTO talhao (nome_talhao, volum_area, tipo_plantacao_id_tipo_plant, propriedade_id_propriedade, localizacao_id_localizacao) VALUES ('Talhao Urbano (Asfalto)', 50.0, 1, 2, 6);

-- CARGA DE DADOS DE APIS EXTERNAS PARA TESTE
-- Popula a tabela tipo_api antes para evitar erros de integridade (ids 1 e 2)
INSERT INTO tipo_api (tipo_api) VALUES ('SATVEG');
INSERT INTO tipo_api (tipo_api) VALUES ('NASAPOWER');

-- Requisições de API para testes (ids 1 e 2)
INSERT INTO req_api (tipo_param, data_analise, tipo_api_id_tipo) VALUES ('NDVI', CURRENT_TIMESTAMP(), 1);
INSERT INTO req_api (tipo_param, data_analise, tipo_api_id_tipo) VALUES ('PRECTOTCORR', CURRENT_TIMESTAMP(), 2);

-- 10 Dados temporais de SATVEG (Talhão Alpha, Req 1)
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-01-01', 0.3537, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-01-09', 0.3671, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-01-17', 0.3787, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-01-25', 0.3819, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-02-02', 0.3799, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-02-10', 0.3802, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-02-18', 0.3843, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-02-26', 0.3696, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-03-05', 0.3740, 1, 1);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2020-03-13', 0.3759, 1, 1);

-- 10 Dados temporais de NASA POWER (Talhão Alpha, Req 2)
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-15', 5.2, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-16', 0.0, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-17', 12.4, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-18', 2.1, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-19', 0.0, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-20', 0.0, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-21', 1.5, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-22', 0.0, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-23', 8.7, 1, 2);
INSERT INTO dado_temporal (data_leitura, valor, talhao_id_talhao, req_api_id_api) VALUES ('2026-05-24', 0.0, 1, 2);

