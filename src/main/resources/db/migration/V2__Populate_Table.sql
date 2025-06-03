-- Primeiro inserir os usuários
INSERT INTO `usuario` (`id`, `data_cadastro`, `email`, `nome`, `senha`, `tipo_usuario`) VALUES
                                                                                            (3, '2025-06-03 00:22:10.237000', 'carlos.santana@dev.com', 'Carlos Santana', 'senhaForte123', 'PROFISSIONAL'),
                                                                                            (4, '2025-06-03 00:22:22.598000', 'contato@nexusdigital.com', 'Nexus Digital', 'outraSenhaSegura456', 'EMPRESA'),
                                                                                            (8, '2025-06-03 00:27:48.831000', 'contato@nexusdigita.com', 'Eduardo Rodrigues', '123', 'EMPRESA'),
                                                                                            (12, '2025-06-03 00:28:36.787000', 'dudu.du@dev.com', 'Duduzinho Duds', '123', 'PROFISSIONAL');

-- Depois inserir as empresas (que dependem de usuario)
INSERT INTO `empresa` (`cnpj`, `desc_empresa`, `razao_social`, `site_url`, `usuario_id`) VALUES
                                                                                             ('98.765.432/0001-10', 'Especialistas em transformacao digital e solucoes em nuvem.', 'Nexus Solucoes Digitais LTDA', 'https://www.nexusdigital.com', 4),
                                                                                             ('98.765.432/0001-11', 'Especialistas em transformacao digital e solucoes em nuvem.', 'Nexus Solucoes Digitais LTDA', 'https://www.nexusdigital.com', 8);

-- Depois inserir os profissionais (que dependem de usuario)
INSERT INTO `profissional` (`curriculo_url`, `nivel_experiencia`, `usuario_id`) VALUES
                                                                                    ('https://www.linkedin.com/in/carlossantana-dev', 'SENIOR', 3),
                                                                                    ('https://www.linkedin.com/in/carlossantana-dev', 'SENIOR', 12);

-- Por fim, inserir as habilidades (que dependem de profissional)
INSERT INTO `profissional_habilidades` (`profissional_id`, `habilidade`) VALUES
                                                                             (3, 'Java'),
                                                                             (3, 'Spring Boot'),
                                                                             (3, 'React'),
                                                                             (3, 'Docker'),
                                                                             (12, 'Java'),
                                                                             (12, 'Spring Boot'),
                                                                             (12, 'React'),
                                                                             (12, 'Docker');