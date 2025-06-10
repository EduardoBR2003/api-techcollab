-- V2__Insert_Fictitious_Data.sql (Corrigido)

-- =================================================================
-- SEÇÃO 1: CRIAÇÃO DE USUÁRIOS, EMPRESAS E PROFISSIONAIS
-- =================================================================

-- Inserindo usuários para 3 empresas e 5 profissionais
INSERT INTO `usuario` (`id`, `data_cadastro`, `email`, `nome`, `senha`, `tipo_usuario`) VALUES
                                                                                            (10, NOW(), 'rh@inovatech.com', 'InovaTech Soluções', 'senha_empresa_1', 'EMPRESA'),
                                                                                            (11, NOW(), 'contato@legacysystems.com', 'Legacy Systems', 'senha_empresa_2', 'EMPRESA'),
                                                                                            (12, NOW(), 'gestao@dataanalytics.br', 'Data Analytics BR', 'senha_empresa_3', 'EMPRESA'),
                                                                                            (20, NOW(), 'carlos.moura@dev.com', 'Carlos Moura', 'senha_dev_1', 'PROFISSIONAL'),
                                                                                            (21, NOW(), 'beatriz.costa@dev.com', 'Beatriz Costa', 'senha_dev_2', 'PROFISSIONAL'),
                                                                                            (22, NOW(), 'fernanda.lima@datascience.com', 'Fernanda Lima', 'senha_dev_3', 'PROFISSIONAL'),
                                                                                            (23, NOW(), 'rodrigo.alves@devops.com', 'Rodrigo Alves', 'senha_dev_4', 'PROFISSIONAL'),
                                                                                            (24, NOW(), 'lucas.gomes@manager.com', 'Lucas Gomes', 'senha_dev_5', 'PROFISSIONAL');

-- Populando a tabela `empresa`
INSERT INTO `empresa` (`usuario_id`, `cnpj`, `razao_social`, `desc_empresa`, `site_url`) VALUES
                                                                                             (10, '22.222.222/0001-22', 'InovaTech Soluções Digitais LTDA', 'Startup focada em soluções mobile e web.', 'http://inovatech.com'),
                                                                                             (11, '33.333.333/0001-33', 'Legacy Systems Consultoria', 'Consultoria especializada na modernização de sistemas legados.', 'http://legacysystems.com'),
                                                                                             (12, '44.444.444/0001-44', 'Data Analytics Brasil Análise de Dados', 'Empresa focada em Business Intelligence e Data Science.', 'http://dataanalyticsbr.com');

-- Populando a tabela `profissional` e suas habilidades
INSERT INTO `profissional` (`usuario_id`, `curriculo_url`, `nivel_experiencia`) VALUES
                                                                                    (20, 'http://linkedin.com/in/carlosmoura', 'SENIOR'),
                                                                                    (21, 'http://linkedin.com/in/beatrizcosta', 'JUNIOR'),
                                                                                    (22, 'http://linkedin.com/in/fernandalima', 'PLENO'),
                                                                                    (23, 'http://linkedin.com/in/rodrigoalves', 'SENIOR'),
                                                                                    (24, 'http://linkedin.com/in/lucasgomes', 'ESPECIALISTA');

INSERT INTO `profissional_habilidades` (`profissional_id`, `habilidade`) VALUES
                                                                             (20, 'Java'), (20, 'Node.js'), (20, 'React'), (20, 'PostgreSQL'), -- Carlos (Full-Stack Sênior)
                                                                             (21, 'HTML'), (21, 'CSS'), (21, 'JavaScript'), (21, 'Vue.js'), -- Beatriz (Frontend Júnior)
                                                                             (22, 'Python'), (22, 'Pandas'), (22, 'Scikit-learn'), (22, 'PowerBI'), -- Fernanda (Data Scientist Pleno)
                                                                             (23, 'Docker'), (23, 'Kubernetes'), (23, 'AWS'), (23, 'Terraform'), -- Rodrigo (DevOps Sênior)
                                                                             (24, 'Scrum'), (24, 'Kanban'), (24, 'Gestão de Riscos'), (24, 'Jira'); -- Lucas (Gerente de Projetos/Especialista)

-- =================================================================
-- SEÇÃO 2: PROJETOS, VAGAS E INTERESSES
-- =================================================================

-- Projeto 1 (da InovaTech): Aberto para interesse
INSERT INTO `projeto` (`id`, `titulo`, `desc_detalhada`, `preco_ofertado`, `status_projeto`, `data_inicio_prevista`, `data_conclusao_prevista`, `empresa_id`) VALUES
    (10, 'Plataforma de Streaming de Música SomLivre', 'API e Frontend para uma nova plataforma de streaming de música.', 80000.00, 'ABERTO_PARA_INTERESSE', '2025-09-01', '2026-03-01', 10);
INSERT INTO `vaga_projeto` (`id`, `titulo_vaga`, `nivel_exp_desejado`, `quant_funcionarios`, `projeto_id`) VALUES
                                                                                                               (10, 'Desenvolvedor Full-Stack (Node.js/React)', 'SENIOR', 1, 10),
                                                                                                               (11, 'Engenheiro DevOps', 'SENIOR', 1, 10);
-- Interesses para o Projeto 1 (pendentes)
INSERT INTO `interesse_projeto` (`profissional_id`, `vagaprojeto_id`, `status_interesse`, `mensagem_motivacao`) VALUES
                                                                                                                    (20, 10, 'PENDENTE', 'Vasta experiência em full-stack, pronto para liderar o desenvolvimento.'),
                                                                                                                    (23, 11, 'PENDENTE', 'Experiência comprovada em automação de CI/CD para plataformas de alta disponibilidade.');


-- Projeto 2 (da Legacy Systems): Já em desenvolvimento, com equipe formada
INSERT INTO `projeto` (`id`, `titulo`, `desc_detalhada`, `preco_ofertado`, `status_projeto`, `data_inicio_prevista`, `data_conclusao_prevista`, `empresa_id`) VALUES
    (11, 'Migração de Sistema Legado COBOL', 'Migrar módulos de um sistema COBOL para uma nova plataforma Java.', 120000.00, 'DESENVOLVIMENTO', '2025-05-01', '2026-01-01', 11);
INSERT INTO `vaga_projeto` (`id`, `titulo_vaga`, `nivel_exp_desejado`, `quant_funcionarios`, `projeto_id`) VALUES
    (12, 'Especialista em Modernização de Sistemas', 'ESPECIALISTA', 1, 11);
-- Interesse que foi alocado
INSERT INTO `interesse_projeto` (`profissional_id`, `vagaprojeto_id`, `status_interesse`) VALUES
    (24, 12, 'ALOCADO');
-- Equipe já formada para o Projeto 2
INSERT INTO `equipe_projeto` (`id`, `data_formacao`, `nome_equipe`, `projeto_id`) VALUES
    (11, '2025-04-25', 'Equipe Legacy', 11);
INSERT INTO `membro_equipe` (`data_entrada`, `papel`, `equipe_projeto_id`, `profissional_id`) VALUES
    ('2025-05-01', 'Especialista em Modernização de Sistemas', 11, 24);


-- Projeto 3 (da Data Analytics BR): Concluído, permite avaliações
INSERT INTO `projeto` (`id`, `titulo`, `desc_detalhada`, `preco_ofertado`, `status_projeto`, `data_inicio_prevista`, `data_conclusao_prevista`, `empresa_id`) VALUES
    (12, 'Dashboard de Análise de Vendas', 'Criação de um dashboard interativo em PowerBI.', 35000.00, 'CONCLUIDO', '2025-02-01', '2025-04-30', 12);

-- #################### CORREÇÃO APLICADA AQUI ####################
INSERT INTO `vaga_projeto` (`id`, `titulo_vaga`, `nivel_exp_desejado`, `quant_funcionarios`, `projeto_id`) VALUES
                                                                                                               (13, 'Cientista de Dados', 'PLENO', 1, 12), -- Adicionado projeto_id 12
                                                                                                               (14, 'Desenvolvedora Frontend', 'JUNIOR', 1, 12); -- Adicionado projeto_id 12
-- ###############################################################

-- Interesses que foram alocados
INSERT INTO `interesse_projeto` (`profissional_id`, `vagaprojeto_id`, `status_interesse`) VALUES
                                                                                              (22, 13, 'ALOCADO'),
                                                                                              (21, 14, 'ALOCADO');
-- Equipe que foi formada para o Projeto 3
INSERT INTO `equipe_projeto` (`id`, `data_formacao`, `nome_equipe`, `projeto_id`) VALUES
    (12, '2025-01-30', 'Time de Dados', 12);
INSERT INTO `membro_equipe` (`data_entrada`, `papel`, `equipe_projeto_id`, `profissional_id`) VALUES
                                                                                                  ('2025-02-01', 'Cientista de Dados', 12, 22),
                                                                                                  ('2025-02-01', 'Desenvolvedora Frontend', 12, 21);


-- Projeto 4 (da InovaTech): Em formação de equipe
INSERT INTO `projeto` (`id`, `titulo`, `desc_detalhada`, `preco_ofertado`, `status_projeto`, `data_inicio_prevista`, `data_conclusao_prevista`, `empresa_id`) VALUES
    (13, 'Aplicativo de Saúde Mental Serenus', 'App mobile para meditação e bem-estar.', 65000.00, 'EM_FORMACAO_EQUIPE', '2025-10-01', '2026-02-01', 10);
INSERT INTO `vaga_projeto` (`id`, `titulo_vaga`, `nivel_exp_desejado`, `quant_funcionarios`, `projeto_id`) VALUES
    (15, 'Desenvolvedor Backend (Node.js)', 'SENIOR', 1, 13);
-- Interesse selecionado pela empresa, aguardando resposta do profissional
INSERT INTO `interesse_projeto` (`profissional_id`, `vagaprojeto_id`, `status_interesse`, `mensagem_motivacao`) VALUES
    (20, 15, 'SELECIONADO', 'Carlos também se candidatou a esta vaga.');

-- =================================================================
-- SEÇÃO 3: AVALIAÇÕES (para o projeto concluído)
-- =================================================================

-- Empresa (ID 12) avalia a profissional Fernanda (ID 22) no projeto 12
INSERT INTO `avaliacao` (`projeto_id`, `avaliador_id`, `avaliado_id`, `tipo_avaliado`, `nota`, `comentario`, `data_avaliacao`) VALUES
    (12, 12, 22, 'PROFISSIONAL', 5, 'Excelente profissional, muito competente e proativa. As entregas foram de altíssima qualidade.', NOW());

-- Profissional Fernanda (ID 22) avalia a empresa Data Analytics BR (ID 12) no projeto 12
INSERT INTO `avaliacao` (`projeto_id`, `avaliador_id`, `avaliado_id`, `tipo_avaliado`, `nota`, `comentario`, `data_avaliacao`) VALUES
    (12, 22, 12, 'EMPRESA', 5, 'Empresa muito organizada, comunicação clara e projeto bem definido. Ótima experiência.', NOW());