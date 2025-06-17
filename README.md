
-----

# API TechCollab 🚀

## 📖 Sobre o Projeto

**TechCollab** é uma plataforma de conexão para projetos colaborativos de TI, com o objetivo de facilitar a interação e o gerenciamento entre empresas que buscam desenvolver projetos e profissionais que procuram oportunidades de trabalho de curta duração. A plataforma permite que empresas publiquem projetos detalhando os perfis necessários e que profissionais se candidatem, formem equipes e entreguem esses projetos.

Esta API foi desenvolvida como a espinha dorsal da plataforma, fornecendo todos os serviços necessários para o cadastro de usuários, gerenciamento de projetos, vagas, equipes e candidaturas.

-----

## ✨ Funcionalidades Principais

* **Gestão de Perfis**: Cadastro e gerenciamento de perfis para **Empresas** e **Profissionais**.
* **Publicação de Projetos**: Empresas podem criar, editar e excluir projetos, detalhando escopo, orçamento e prazo.
* **Criação de Vagas**: Empresas podem definir vagas específicas para seus projetos, com habilidades e níveis de experiência requeridos.
* **Busca e Candidatura**: Profissionais podem buscar projetos disponíveis e manifestar interesse nas vagas.
* **Formação de Equipes**: Empresas podem selecionar candidatos e montar as equipes para seus projetos.
* **Navegabilidade com HATEOAS**: A API utiliza os princípios HATEOAS para fornecer links que guiam o cliente através das ações e recursos disponíveis.
* **Documentação Interativa com Swagger**: Documentação completa e interativa gerada automaticamente com Springdoc OpenAPI.

-----

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com as seguintes tecnologias:

* **Backend**:
    * **Java 21**
    * **Spring Boot 3.4.6**
    * **Spring Data JPA** (Hibernate) para persistência de dados.
    * **Spring Web** para a construção da API REST.
    * **Spring HATEOAS** para a implementação de hipermídia.
    * **Caffeine Cache** para gerenciamento de cache de alta performance.
* **Banco de Dados**:
    * **MySQL**
    * **Flyway** para o versionamento e migração do schema do banco de dados.
* **Build e Dependências**:
    * **Maven**
* **Documentação da API**:
    * **Springdoc OpenAPI (Swagger)**

-----

## 🚀 Como Executar o Projeto

Siga os passos abaixo para configurar e executar a aplicação localmente.

### Pré-requisitos

* **Java Development Kit (JDK) 21** ou superior.
* **Apache Maven** 3.8 ou superior.
* **MySQL Server** 8.0 ou superior.
* Uma IDE de sua preferência (IntelliJ, VS Code, Eclipse, etc.).
* Um cliente de API como o **Postman** (opcional, pois o Swagger já oferece uma interface de testes).

### 1\. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/api-techcollab.git
cd api-techcollab
```

### 2\. Configurar o Banco de Dados

A aplicação utiliza o **Flyway** para criar e popular o banco de dados automaticamente. Você só precisa criar o banco de dados vazio.

a. Conecte-se ao seu servidor MySQL e execute o seguinte comando:

```sql
CREATE DATABASE IF NOT EXISTS api_techcollab_database;
```

b. Configure suas credenciais de acesso ao banco no arquivo `src/main/resources/application.yml`:

```yaml
spring:
  # ... outras configurações ...
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/api_techcollab_database?useTimezone=true&serverTimezone=UTC
    username: SEU_USUARIO_MYSQL # <-- Altere aqui
    password: SUA_SENHA_MYSQL   # <-- Altere aqui
  # ...
```

### 3\. Executar a Aplicação

Use o Maven para compilar e iniciar a aplicação:

```bash
mvn spring-boot:run
```

A API estará rodando em `http://localhost:8080`.

-----

## 📄 Documentação da API (Swagger)

Com a aplicação em execução, você pode acessar a documentação interativa da API gerada pelo Swagger. Ela permite visualizar todos os endpoints, testá-los e ver os modelos de dados.

➡️ **Acesse a UI do Swagger em:** [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

-----

## 🗺️ Visão Geral dos Endpoints Principais

| Recurso | Endpoint Base | Descrição |
| :--- | :--- |:---|
| **Empresas** | `/api/empresas` | Gerenciamento de perfis de empresas e seus projetos. |
| **Profissionais** | `/api/profissionais` | Gerenciamento de perfis de profissionais e suas candidaturas. |
| **Projetos** | `/api/projetos` | Ações gerais sobre projetos, como busca e visualização. |
| **Vagas** | `/api/projetos/{id}/vagas` | Gerenciamento de vagas dentro de um projeto. |
| **Interesses** | `/api/profissionais/{id}/interesses` ou `/api/empresas/{id}/...`| Gestão de candidaturas, tanto pela perspectiva da empresa quanto do profissional. |
| **Equipes** | `/api/projetos/{id}/equipe` | Montagem e visualização da equipe de um projeto. |

-----
