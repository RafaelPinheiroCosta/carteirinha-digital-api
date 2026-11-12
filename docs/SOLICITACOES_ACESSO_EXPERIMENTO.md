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
- O catálogo de cursos e suas UCs agora possui tabelas próprias (`cursos` e `curso_unidades_curriculares`), mas ainda não possui telas administrativas de manutenção. As ofertas de turma e as atribuições docentes continuam nas tabelas didáticas existentes.
- Atribuições de professor são registradas copiando dados acadêmicos de turmas/UCs-modelo para o novo professor. Uma modelagem futura deve separar catálogo de oferta e vínculo docente.
- O número de matrícula institucional de aluno é fornecido pelo administrador. O sistema não inventa matrícula oficial.
- Cadastro rejeitado é apagado definitivamente, conforme requisito didático.
- A API usa H2 `create-drop` por padrão: os novos cadastros desaparecem ao reiniciar a API. Para persistência, configurar PostgreSQL e migrações adequadas.
- O bootstrap cria um administrador `admin-local` com senha `AdminLocal123!` no ambiente local padrão, salvo se variáveis de ambiente sobrescreverem os valores. Se a conta já existir em banco persistente, o bootstrap não altera sua senha.
- Recomendado reforçar validações, limitação de tentativas, auditoria administrativa e proteção de dados pessoais antes de qualquer uso real.

## Testes
`mvn test` inclui `GestaoSolicitacoesIntegrationTest` cobrindo cadastro pendente, bloqueio do login, aprovação de aluno e de professor com múltiplas atribuições, rejeição e bloqueio de cadastro ADMIN público.

O commit contém testes, **mas não implica que foram executados**.

## Matrícula automática por curso (12/11/2026)

**Nova regra:** não é necessário copiar unidades curriculares para cada aluno na aprovação. O administrador informa somente a matrícula e uma turma do curso solicitado. O `GET /unidades-curriculares` identifica o aluno pelo JWT, consulta seu curso cadastrado e retorna **todas** as UCs associadas ao curso, mesmo que ainda não haja docente designado.

- `cursos`: código e nome do curso.
- `curso_unidades_curriculares`: cada unidade curricular ligada ao código do curso.
- `usuarios`: aluno com curso e turma aprovados.
- `professor_unidades_curriculares`: atribuição de professor a uma UC de uma turma.
- O nome do professor vem do cadastro em `usuarios`, não de uma string fixa na UC do aluno.
- Quando não houver professor designado, a API informa `"Professor a definir"`.
- Notas/faltas existentes em `unidades_curriculares` são aproveitadas pelo aluno e nome da UC. Novos alunos recebem nota1=0, nota2=0 e faltas=0 como valores didáticos iniciais; ainda não há estado de avaliação "não lançada".

### Dados didáticos de exemplo

**Desenvolvimento de Sistemas (DS)**: 7 UCs — Banco de Dados; Desenvolvimento de Sistemas; Fundamentos de Programação; Programação Back-End; Programação para Dispositivos Móveis; Projeto de Software; Testes de Software. As turmas existentes foram preservadas.

**Redes de Computadores (REDES)**: 4 UCs — Fundamentos de Redes; Cabeamento Estruturado; Administração de Redes; Segurança de Redes. Turma `1REDES-A`, professor inicial `Professor Modelo` nas quatro disciplinas.

O `GET /public/cursos` continua retornando `List<String>` como antes. O `GET /unidades-curriculares` conserva o contrato atual do Android (`id`, `nome`, `professor`, `nota1`, `nota2`, `media`, `faltas`). Portanto, os DTOs, ViewModels e cartões Android existentes não precisam ser alterados.

Na aprovação de professor, o catálogo de UC disponível para cada turma vem do **curso** associado, mesmo que a UC ainda não tenha docente.

### Homologação

Execute `mvn test`, incluindo `CursoMatriculaIntegracaoTest`, e valide no emulador:

1. Registrar aluno de Redes, com senha de pelo menos 8 caracteres.
2. Entrar como administrador e aprovar na turma `1REDES-A`.
3. Entrar como aluno e abrir UCs: devem surgir 4 unidades de Redes, todas com `Professor Modelo`.
4. Entrar como aluno antigo de DS: devem surgir 7 UCs de DS.
5. Conferir que a lista não mistura disciplinas dos cursos.
6. Registrar e aprovar novo professor em turma/UC e verificar atualização do nome na listagem dos alunos da mesma turma.

**Nota de modelagem:** por enquanto o histórico individual de notas é legado; a evolução futura poderá introduzir `matriculas`, `ofertas` e `avaliacoes` independentes, com semestres e situações acadêmicas. A lógica atual preserva a API existente com mudanças incrementais.
