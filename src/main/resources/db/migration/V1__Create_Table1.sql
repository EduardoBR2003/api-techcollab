-- Tabela base que não depende de nenhuma outra
CREATE TABLE IF NOT EXISTS `usuario` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `data_cadastro` datetime(6) NOT NULL,
    `email` varchar(100) NOT NULL,
    `nome` varchar(100) NOT NULL,
    `senha` varchar(255) NOT NULL,
    `tipo_usuario` enum('EMPRESA','PROFISSIONAL') NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabelas que dependem apenas de usuario
CREATE TABLE IF NOT EXISTS `empresa` (
                                         `cnpj` varchar(18) NOT NULL,
    `desc_empresa` text,
    `razao_social` varchar(150) NOT NULL,
    `site_url` varchar(255) DEFAULT NULL,
    `usuario_id` bigint NOT NULL,
    PRIMARY KEY (`usuario_id`),
    UNIQUE KEY `UK74xhe5obsc7li6x4q5wi75pd5` (`cnpj`),
    CONSTRAINT `FKs12udhh8f7taklesp1phv0ikg` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `profissional` (
                                              `curriculo_url` varchar(255) DEFAULT NULL,
    `nivel_experiencia` enum('ESPECIALISTA','JUNIOR','PLENO','SENIOR') DEFAULT NULL,
    `usuario_id` bigint NOT NULL,
    PRIMARY KEY (`usuario_id`),
    CONSTRAINT `FK509yihu28yuuinro8jxectk7q` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabelas que dependem de empresa
CREATE TABLE IF NOT EXISTS `projeto` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `data_conclusao_prevista` date DEFAULT NULL,
                                         `data_inicio_prevista` date DEFAULT NULL,
                                         `desc_detalhada` text NOT NULL,
                                         `preco_ofertado` double DEFAULT NULL,
                                         `status_projeto` enum('ABERTO_PARA_INTERESSE','CONCLUIDO','DESENVOLVIMENTO','EM_FORMACAO_EQUIPE') NOT NULL,
    `titulo` varchar(100) NOT NULL,
    `empresa_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKeidnrtaxuh43xugd9j8qnm6yk` (`empresa_id`),
    CONSTRAINT `FKeidnrtaxuh43xugd9j8qnm6yk` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`usuario_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabelas que dependem de projeto
CREATE TABLE IF NOT EXISTS `equipe_projeto` (
                                                `id` bigint NOT NULL AUTO_INCREMENT,
                                                `data_formacao` date NOT NULL,
                                                `nome_equipe` varchar(100) NOT NULL,
    `projeto_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKh3ph242wearqfu9eqx3f944bh` (`projeto_id`),
    CONSTRAINT `FK9v2jc6lsl6vwd6nggh5d8c8aw` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `vaga_projeto` (
                                              `id` bigint NOT NULL AUTO_INCREMENT,
                                              `nivel_exp_desejado` enum('ESPECIALISTA','JUNIOR','PLENO','SENIOR') NOT NULL,
    `quant_funcionarios` int NOT NULL,
    `titulo_vaga` varchar(100) NOT NULL,
    `projeto_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKm7mnguhyg4298cfe00ijbdack` (`projeto_id`),
    CONSTRAINT `FKm7mnguhyg4298cfe00ijbdack` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `entrega_projeto` (
                                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                                 `arquivo_url` varchar(255) DEFAULT NULL,
    `data_submissao` datetime(6) DEFAULT NULL,
    `desc_entrega` text NOT NULL,
    `status_entrega` enum('APROVADA','EM_REVISAO','PENDENTE','REJEITADA','SUBMETIDA') NOT NULL,
    `projeto_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK7sthn1rfjgefoorjukb1ugtvc` (`projeto_id`),
    CONSTRAINT `FK7sthn1rfjgefoorjukb1ugtvc` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabelas que dependem de profissional e outras tabelas
CREATE TABLE IF NOT EXISTS `interesse_projeto` (
                                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                                   `mensagem_motivacao` text,
                                                   `status_interesse` enum('ALOCADO','PENDENTE','RECUSADO_DO_PROF','SELECIONADO','RECUSADO_PELA_EMPRESA') NOT NULL,
    `profissional_id` bigint NOT NULL,
    `vagaprojeto_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKg71dcvhd5jwmpmpdwj128u1qn` (`profissional_id`),
    KEY `FKsseyirhrg8s37hea0rf1og2wy` (`vagaprojeto_id`),
    CONSTRAINT `FKg71dcvhd5jwmpmpdwj128u1qn` FOREIGN KEY (`profissional_id`) REFERENCES `profissional` (`usuario_id`),
    CONSTRAINT `FKsseyirhrg8s37hea0rf1og2wy` FOREIGN KEY (`vagaprojeto_id`) REFERENCES `vaga_projeto` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `membro_equipe` (
                                               `id` bigint NOT NULL AUTO_INCREMENT,
                                               `data_entrada` date NOT NULL,
                                               `papel` varchar(50) NOT NULL,
    `equipe_projeto_id` bigint NOT NULL,
    `profissional_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKd9f40sv5a762ywhxe9v9ey64i` (`equipe_projeto_id`),
    KEY `FKklesxds93qkll1w5pxh2afd2p` (`profissional_id`),
    CONSTRAINT `FKd9f40sv5a762ywhxe9v9ey64i` FOREIGN KEY (`equipe_projeto_id`) REFERENCES `equipe_projeto` (`id`),
    CONSTRAINT `FKklesxds93qkll1w5pxh2afd2p` FOREIGN KEY (`profissional_id`) REFERENCES `profissional` (`usuario_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `avaliacao` (
                                           `id` bigint NOT NULL AUTO_INCREMENT,
                                           `comentario` text,
                                           `data_avaliacao` datetime(6) NOT NULL,
    `nota` int NOT NULL,
    `tipo_avaliado` enum('EMPRESA','EQUIPE_PROJETO','PROFISSIONAL') NOT NULL,
    `avaliado_id` bigint NOT NULL,
    `avaliador_id` bigint NOT NULL,
    `projeto_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK97crmk3tocob3u1kn9f282fid` (`avaliado_id`),
    KEY `FKpm9gw7dxc4xxmyfayfkclxt9j` (`avaliador_id`),
    KEY `FKn683uwtfh4i8qko14a3ork4ll` (`projeto_id`),
    CONSTRAINT `FK97crmk3tocob3u1kn9f282fid` FOREIGN KEY (`avaliado_id`) REFERENCES `usuario` (`id`),
    CONSTRAINT `FKn683uwtfh4i8qko14a3ork4ll` FOREIGN KEY (`projeto_id`) REFERENCES `projeto` (`id`),
    CONSTRAINT `FKpm9gw7dxc4xxmyfayfkclxt9j` FOREIGN KEY (`avaliador_id`) REFERENCES `usuario` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `mensagem` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `conteudo` text NOT NULL,
                                          `data_envio` datetime(6) NOT NULL,
    `lida` bit(1) NOT NULL,
    `destinatario_id` bigint NOT NULL,
    `remetente_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK4xfoplx81s0fkkevdd62gbv0o` (`destinatario_id`),
    KEY `FK6ct07n5wm2ci4hu2qgcvb7y46` (`remetente_id`),
    CONSTRAINT `FK4xfoplx81s0fkkevdd62gbv0o` FOREIGN KEY (`destinatario_id`) REFERENCES `usuario` (`id`),
    CONSTRAINT `FK6ct07n5wm2ci4hu2qgcvb7y46` FOREIGN KEY (`remetente_id`) REFERENCES `usuario` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabelas de relacionamento muitos-para-muitos
CREATE TABLE IF NOT EXISTS `profissional_habilidades` (
                                                          `profissional_id` bigint NOT NULL,
                                                          `habilidade` varchar(255) DEFAULT NULL,
    KEY `FKb151sernbc8s6oc9i0c86omqm` (`profissional_id`),
    CONSTRAINT `FKb151sernbc8s6oc9i0c86omqm` FOREIGN KEY (`profissional_id`) REFERENCES `profissional` (`usuario_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `vaga_habilidades` (
                                                  `vaga_id` bigint NOT NULL,
                                                  `habilidade_requerida` varchar(255) DEFAULT NULL,
    KEY `FKlr2emwxc8jdya5h5lptk61qyd` (`vaga_id`),
    CONSTRAINT `FKlr2emwxc8jdya5h5lptk61qyd` FOREIGN KEY (`vaga_id`) REFERENCES `vaga_projeto` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;