# Experimento — Gestão de Solicitações de Acesso

Branch: `feat/solicitacoes-acesso-cursos-admin-20261112`. Não fazer merge sem validar backend e Android.

## Fluxo
1. Visitante consulta `GET /public/cursos` (lista de nomes dos cursos derivados das turmas cadastradas).
2. Visitante envia `POST /public/cadastros`, com `nome`, `login`, `senha`, `perfil` (ALUNO ou PROFESSOR) e `curso` para aluno. Resposta 202: solicitação recebida.
3. Login de usuário não aprovado responde 403, sem gerar JWT.
4. Administrador entra pela rota normal `POST /auth/login` usando a conta didática definida em `src/main/resources/application.yml` (ou valores sobrescritos por variáveis de ambiente).
5. `GET /admin/solicitacoes?perfil=ALUNO` ou `PROFESSOR` lista solicitações; `GET /admin/catalogo/turmas` oferece opções.
6. `PUT /admin/solicitacoes/{id}/aprovacao` confirma o perfil solicitado e associa registros:
   - ALUNO: `{"turma":"2DEVEST-A","matricula":"MATRICULA_REAL"}`
   - PROFESSOR: `{"atribuicoes":[{"turma":"2DEVEST-A","unidadeCurricular":"Programação Back-End"},{"turma":"2DEVEST-B","unidadeCurricular":"Programação para Dispositivos Móveis"}]}`
7. `DELETE /admin/solicitacoes/{id}` exclui definitivamente solicitação rejeitada.
8. Usuário aprovado entra normalmente; professor recebe turmas e UCs associadas.

O perfil e o curso solicitado são imutáveis durante a aprovação; a API usa os dados persistidos da solicitação e ignora eventuais campos extras enviados pelo cliente.

Todas as rotas `/admin/**` exigem JWT com `ROLE_ADMIN`. `ADMIN` nunca é aceito no cadastro público.

## Executar a API no Windows PowerShell

```powershell
git switch feat/solicitacoes-acesso-cursos-admin-20261112
# Para desenvolvimento local, o application.yml ja define:
# login: admin-local
# senha: AdminLocal123!
# Remova variaveis de ambiente antigas que possam sobrescrever o YAML:
Remove-Item Env:APP_BOOTSTRAP_ADMIN_LOGIN -ErrorAction SilentlyContinue
Remove-Item Env:APP_BOOTSTRAP_ADMIN_PASSWORD -ErrorAction SilentlyContinue
mvn test
mvn spring-boot:run
```

A senha no YAML é intencionalmente pública e **exclusivamente didática**. Não reutilize esta senha em serviços reais; em produção, substitua-a por segredo seguro armazenado fora do repositório e faça bootstrap seguro do administrador.

## Observações e limites conhecidos
- O catálogo inicial é derivado das turmas/UCs de professor já existentes: ainda não há módulo de manutenção independente de cursos/turmas/UCs.
- Atribuições de professor são registradas copiando dados acadêmicos de turmas/UCs-modelo para o novo professor. Uma modelagem futura deve separar catálogo de oferta e vínculo docente.
- O número de matrícula institucional de aluno é fornecido pelo administrador. O sistema não inventa matrícula oficial.
- Cadastro rejeitado é apagado definitivamente, conforme requisito didático.
- A API usa H2 `create-drop` por padrão: os novos cadastros desaparecem ao reiniciar a API. Para persistência, configurar PostgreSQL e migrações adequadas.
- O bootstrap cria um administrador `admin-local` com senha `AdminLocal123!` no ambiente local padrão, salvo se variáveis de ambiente sobrescreverem os valores. Se a conta já existir em banco persistente, o bootstrap não altera sua senha.
- Recomendado reforçar validações, limitação de tentativas, auditoria administrativa e proteção de dados pessoais antes de qualquer uso real.

## Testes
`mvn test` inclui `GestaoSolicitacoesIntegrationTest` cobrindo cadastro pendente, bloqueio do login, aprovação de aluno e de professor com múltiplas atribuições, rejeição e bloqueio de cadastro ADMIN público.

O commit contém testes, **mas não implica que foram executados**.
